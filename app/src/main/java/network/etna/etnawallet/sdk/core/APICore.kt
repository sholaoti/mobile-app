package network.etna.etnawallet.sdk.core

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import org.json.JSONObject
import java.lang.reflect.Type

interface APICore {

    fun requestBuilder(): APICoreRequestBuilder

    fun push(eventName: String, obj: Any? = null): Completable

    fun <T> pushWithAck(eventName: String, obj: Any?, responseClazz: Class<T>): Single<T>
    fun <T> pushWithAck(eventName: String, obj: Any?, typeOfT: Type): Single<T>

    fun pushWithAck(eventName: String, obj: Any?): Single<String>
    fun pushWithAckImmediately(eventName: String, obj: Any?): Single<String>

    fun subscribe(eventName: String): Observable<JSONObject>
    fun <T> subscribe(eventName: String, clazz: Class<T>): Observable<T>
    fun <T> subscribe(eventName: String, typeOfT: Type): Observable<T>
}