package network.etna.etnawallet.sdk.api.etna.wssapi.preparetransaction

import com.google.gson.JsonObject
import io.reactivex.Single
import network.etna.etnawallet.R
import network.etna.etnawallet.sdk.api.etna.wssapi.EtnaWSSAPI
import network.etna.etnawallet.sdk.error.BaseError

fun EtnaWSSAPI.prepareTransaction(request: PrepareTransactionRequest): Single<JsonObject> {
    return apiCore
        .requestBuilder()
        .eventName("prepareTransaction")
        .eventObject(request)
        .build(JsonObject::class.java)
        .flatMap {
            try {
                if (it.get("success").asBoolean) {
                    it.remove("success")
                    Single.just(it)
                } else {
                    Single.error(PrepareTransactionError.UnableToPrepare)
                }
            } catch (e: Exception) {
                Single.error(PrepareTransactionError.UnableToPrepare)
            }
        }
}

sealed class PrepareTransactionError {
    object UnableToPrepare: BaseError.Error() {
        override val resourceId: Int
            get() = R.string.error_unable_to_prepare_transaction
    }
}

class PrepareTransactionRequest(
    val platform: String,
    val address: String,
    val receiverAddress: String,
    val value: String?, // except NFT
    val tokenAddress: String?, // only for ERC tokens
    val tokenId: String? // only for NFT tokens
)

//class PrepareTransactionResponse(
//    val success: Boolean,
//    val tx: JsonObject,
//    val tosign: List<String>
//)