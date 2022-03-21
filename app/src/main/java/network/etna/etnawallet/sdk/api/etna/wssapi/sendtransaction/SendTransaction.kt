package network.etna.etnawallet.sdk.api.etna.wssapi.sendtransaction

import com.google.gson.JsonObject
import io.reactivex.Single
import network.etna.etnawallet.R
import network.etna.etnawallet.sdk.api.etna.wssapi.EtnaWSSAPI
import network.etna.etnawallet.sdk.error.BaseError

fun EtnaWSSAPI.sendTransaction(request: JsonObject): Single<SendTransactionResponse> {
    return apiCore
        .requestBuilder()
        .eventName("sendTransaction")
        .eventObject(request)
        .build(SendTransactionResponse::class.java)
        .flatMap {
            if (it.success) {
                Single.just(it)
            } else {
                Single.error(SendTransactionError.UnableToSend)
            }
        }
}

sealed class SendTransactionError {
    object UnableToSend: BaseError.Error() {
        override val resourceId: Int
            get() = R.string.error_unable_to_send_transaction
    }
}

//class SendTransactionRequest(
//    val platform: String,
//    val tx: JsonObject,
//    val tosign: List<String>,
//    val signatures: List<Signature>,
//    val pubkeys: List<String>
//)

class SendTransactionResponse(
    val success: Boolean
)
//mjppi7fZ8UWqdyczFCbQTzatATS7vg61Y8 - bitcoin-testnet
//0xbF4669b1f7fC070B09068a3F9DBD2D206456e7D4 - ethereum-testnet
//0xC5d72B2682d874a9A753198eb97fdA13140c1CE6 - marriage BNB wallet