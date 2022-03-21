package network.etna.etnawallet.sdk.core

import android.util.Log
import network.etna.etnawallet.sdk.core.model.LoginReq.LoginRequest
import network.etna.etnawallet.sdk.core.model.LoginReq.LoginResponse
import com.google.gson.Gson
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import io.socket.client.Ack
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import io.socket.engineio.client.transports.WebSocket
import network.etna.etnawallet.extensions.convert
import network.etna.etnawallet.repository.warningservice.subscribeWithErrorHandler
import org.json.JSONArray
import org.json.JSONObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.lang.reflect.Type
import java.util.*

enum class SocketStatus {
    CONNECTED, DISCONNECTED
}

class APICoreImpl(url: String) : APICore, KoinComponent {

    private val socket: Socket

    private val accessTokenProvider: AccessTokenProvider by inject()

    private val isLoggedIn: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false)

    private val socketStatusSubject: BehaviorSubject<SocketStatus> =
        BehaviorSubject.createDefault(SocketStatus.DISCONNECTED)

    private var loginObserverDisposable: Disposable? = null

    init {

        initObservers()

        val options = IO.Options().apply {
            timeout = 15000
            forceNew = true
            reconnectionAttempts = Int.MAX_VALUE
            transports = arrayOf(WebSocket.NAME)
        }

        socket = IO.socket(url, options)

        socket.on(Socket.EVENT_CONNECT_ERROR) {
            socketStatusSubject.onNext(SocketStatus.DISCONNECTED)
        }

        socket.on(Socket.EVENT_DISCONNECT) {
            socketStatusSubject.onNext(SocketStatus.DISCONNECTED)
        }

        socket.on(Socket.EVENT_CONNECT) {
            socketStatusSubject.onNext(SocketStatus.CONNECTED)
        }

        socket.connect()
    }

    private fun initObservers() {
        loginObserverDisposable = Observable.combineLatest(
            socketStatusSubject
                .distinctUntilChanged()
                .filter { it == SocketStatus.CONNECTED },
            accessTokenProvider.getAccessTokenObservable())
            { _, accessToken ->
                accessToken
            }
            .subscribe(
                {
                    if (it.isPresent) {
                        login(it.get())
                    }
                },
                {}
            )
    }

    override fun requestBuilder(): APICoreRequestBuilder {
        return APICoreRequestBuilder(this)
    }

    private fun waitLogined(): Completable {
        return isLoggedIn
            .filter { it }
            .firstElement()
            .ignoreElement()
    }

    private var loginDisposable: Disposable? = null

    private fun login(accessToken: String) {
        loginDisposable?.dispose()

        loginDisposable =
            Single.just(LoginRequest(accessToken))
                .flatMap {
                    pushWithAckImmediately("login", it, LoginResponse::class.java)
                }
                .flatMapCompletable {
                    if (it.success) {
                        Completable.complete()
                    } else {
                        Completable.error(LoginError.UnableLogin)
                    }
                }
                .subscribeWithErrorHandler {
                    isLoggedIn.onNext(true)
                }
    }

    override fun <T> pushWithAck(eventName: String, obj: Any?, responseClazz: Class<T>): Single<T> {
        return waitLogined()
            .andThen(pushWithAckImmediately(eventName, obj, responseClazz))
    }

    override fun <T> pushWithAck(eventName: String, obj: Any?, typeOfT: Type): Single<T> {
        return waitLogined()
            .andThen(pushWithAckImmediately(eventName, obj, typeOfT))
    }

    private fun <T> pushWithAckImmediately(eventName: String, obj: Any?, responseClazz: Class<T>): Single<T> {
        return pushWithAckImmediately(eventName, obj)
            .flatMap {
                it.convert(responseClazz)
            }
    }

    private fun <T> pushWithAckImmediately(eventName: String, obj: Any?, typeOfT: Type): Single<T> {
        return pushWithAckImmediately(eventName, obj)
            .flatMap {
                it.convert(typeOfT)
            }
    }

    override fun pushWithAck(eventName: String, obj: Any?): Single<String> {
        return waitLogined()
            .andThen(pushWithAckImmediately(eventName, obj))
    }

    override fun pushWithAckImmediately(eventName: String, obj: Any?): Single<String> {
        return Single.create { emitter ->

            var json: Any? = JSONObject()
            if (obj != null) {
                val jsonString = Gson().toJson(obj)
                json = if (obj is List<*>) {
                    JSONArray(jsonString)
                } else {
                    JSONObject(jsonString)
                }
            }

            Log.d("APICore", "push: $eventName $json")

            socket.emit(eventName, json, Ack { resp ->
                try {
                    if (resp.size == 1 && resp[0] != null) {
                        Log.d("APICore", "received: $eventName ${resp[0]}")
                        emitter.onSuccess(resp[0].toString())
                    }
                } catch (e: Exception) {
                    emitter.onError(e)
                }
            })
        }
    }

    override fun push(eventName: String, obj: Any?): Completable {
        return waitLogined()
            .andThen(Completable.create { emitter ->
                if (obj != null) {
                    val jsonString = Gson().toJson(obj)
                    val json = JSONObject(jsonString)
                    socket.emit(eventName, json)
                } else {
                    socket.emit(eventName, JSONObject())
                }
                emitter.onComplete()
            })
    }

    override fun <T> subscribe(eventName: String, typeOfT: Type): Observable<T> {
        return subscribe(eventName)
            .map {
                var obj: T? = null
                try {
                    obj = Gson().fromJson<T>(it.toString(), typeOfT)
                } catch (e: Exception) {
                    val x = 1
                }
                Optional.ofNullable(obj)
            }
            .filter { it.isPresent }
            .map { it.get() }
    }

    override fun <T> subscribe(eventName: String, clazz: Class<T>): Observable<T> {
        return subscribe(eventName)
            .map {
                var obj: T? = null
                try {
                    obj = Gson().fromJson(it.toString(), clazz)
                } catch (e: Exception) {
                    val x = 1
                }
                Optional.ofNullable(obj)
            }
            .filter { it.isPresent }
            .map { it.get() }
    }

    override fun subscribe(eventName: String): Observable<JSONObject> {
        return Observable.create { emitter ->

            val handler = Emitter.Listener { messages ->
                for (any in messages) {
                    (any as? JSONObject)?.let {
                        emitter.onNext(it)
                    }
                }
            }

            socket.on(eventName, handler)

            emitter.setCancellable {
                socket.off(eventName, handler)
            }
        }
    }
}