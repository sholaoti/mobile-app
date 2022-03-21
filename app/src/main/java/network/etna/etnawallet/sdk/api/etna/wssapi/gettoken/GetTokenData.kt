package network.etna.etnawallet.sdk.api.etna.wssapi.gettoken

import io.reactivex.Single
import network.etna.etnawallet.R
import network.etna.etnawallet.repository.wallets.cryptowallet.model.ERCToken
import network.etna.etnawallet.repository.wallets.cryptowallet.model.ERCTokenJsonDeserializer
import network.etna.etnawallet.sdk.api.etna.wssapi.EtnaWSSAPI

fun EtnaWSSAPI.getTokenData(platform: String, tokenAddress: String, tokenId: String?): Single<ERCToken> {
    val request = GetTokenDataRequest(platform, tokenAddress, tokenId)

    return apiCore
        .requestBuilder()
        .eventName("getTokenData")
        .eventObject(request)
        .gsonBuilderOptions {
            registerTypeAdapter(ERCToken::class.java, ERCTokenJsonDeserializer())
        }
        .build(GetTokenDataResponse::class.java)
        .flatMap {
            if (it.result.success) {
                Single.just(it.result.tokenData)
            } else {
                Single.error(Exception("No token"))
            }
        }
}

class GetTokenDataRequest(
    val platform: String,
    val tokenAddress: String,
    val tokenId: String?
)

class GetTokenDataResponse(
    val result: GetTokenDataResult
)

class GetTokenDataResult(
    val success: Boolean,
    val tokenData: ERCToken
)
