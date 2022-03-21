package network.etna.etnawallet.sdk.api.httpservice

import io.reactivex.Single
import network.etna.etnawallet.extensions.GsonBuilderOptions

interface HttpService {
    fun <T> request(url: String, clazz: Class<T>, gsonBuilderOptions: GsonBuilderOptions? = null): Single<T>
}