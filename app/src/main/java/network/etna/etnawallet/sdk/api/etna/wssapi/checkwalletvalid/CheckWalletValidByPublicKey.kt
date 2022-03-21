package network.etna.etnawallet.sdk.api.etna.wssapi.checkwalletvalid

import io.reactivex.Single
import network.etna.etnawallet.sdk.api.etna.wssapi.EtnaWSSAPI

fun EtnaWSSAPI.checkWalletValidByPublicKey(platform: String, address: String): Single<Boolean> {
    val request = CheckWalletValidByPublicKeyRequest(platform, address)

    return apiCore
        .requestBuilder()
        .eventName("checkWalletValid")
        .eventObject(request)
        .build(CheckWalletValidByPublicKeyResponse::class.java)
        .map {
            it.result
        }
}

class CheckWalletValidByPublicKeyRequest(
    val paltform: String,
    val publicKey: String
)

class CheckWalletValidByPublicKeyResponse(
    val result: Boolean
)
