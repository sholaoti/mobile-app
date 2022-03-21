package network.etna.etnawallet.sdk.core

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import network.etna.etnawallet.extensions.GsonBuilderOptions
import network.etna.etnawallet.extensions.convert
import java.lang.reflect.Type

class APICoreRequestBuilder(private val apiCore: APICore) {
    private var waitLogined: Boolean = true
    private var eventName: String = ""
    private var eventObject: Any? = null
    private var gsonBuilderOptions: GsonBuilderOptions? = null

    fun waitLogined(value: Boolean): APICoreRequestBuilder {
        waitLogined = value
        return this
    }

    fun eventName(value: String): APICoreRequestBuilder {
        eventName = value
        return this
    }

    fun eventObject(value: Any?): APICoreRequestBuilder {
        eventObject = value
        return this
    }

    fun gsonBuilderOptions(value: GsonBuilderOptions): APICoreRequestBuilder {
        gsonBuilderOptions = value
        return this
    }

    fun <T> build(responseClazz: Class<T>): Single<T> {
        return build()
            .flatMap {
                it.convert(responseClazz, gsonBuilderOptions)
            }
    }

    fun <T> build(typeOfT: Type): Single<T> {
        return build()
            .flatMap {
                it.convert(typeOfT, gsonBuilderOptions)
            }
    }

    private fun build(): Single<String> {
        return if (waitLogined) {
            apiCore
                .pushWithAck(eventName, eventObject)
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())

        } else {
            apiCore
                .pushWithAckImmediately(eventName, eventObject)
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
        }
    }
}
