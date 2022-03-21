package network.etna.etnawallet.sdk.api.etna.wssapi.checkwalletvalid

import io.reactivex.Completable
import io.reactivex.Single
import network.etna.etnawallet.R
import network.etna.etnawallet.sdk.api.etna.wssapi.EtnaWSSAPI
import network.etna.etnawallet.sdk.error.BaseError

fun EtnaWSSAPI.checkWalletValidByAddress(platform: String, address: String): Single<Boolean> {
    val request = CheckWalletValidByAddressRequest(platform, address)

    return apiCore
        .requestBuilder()
        .eventName("checkWalletValid")
        .eventObject(request)
        .build(CheckWalletValidByAddressResponse::class.java)
        .map {
            it.result
        }
}

fun EtnaWSSAPI.checkWalletValidByAddressCompletable(platform: String, address: String): Completable {
    return checkWalletValidByAddress(platform, address)
        .flatMapCompletable {
            if (it) {
                Completable.complete()
            } else {
                Completable.error(CheckWalletValidByAddressError)
            }
        }
}

object CheckWalletValidByAddressError: BaseError.Error() {
    override val resourceId: Int
        get() = R.string.error_address_is_not_valid
}

class CheckWalletValidByAddressRequest(
    val platform: String,
    val address: String
)

class CheckWalletValidByAddressResponse(
    val result: Boolean
)
