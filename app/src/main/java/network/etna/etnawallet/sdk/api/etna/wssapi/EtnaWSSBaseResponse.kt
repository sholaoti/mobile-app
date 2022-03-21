package network.etna.etnawallet.sdk.api.etna.wssapi

import io.reactivex.Single

class EtnaWSSBaseResponse<T>(
    val success: Boolean,
    val result: T
) {
    fun getResult(): Single<T> {
        return if (success) {
            Single.just(result)
        } else {
            Single.error(Exception("no data"))
        }
    }
}