package network.etna.etnawallet.sdk.api.etna.wssapi.balance

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import io.reactivex.Single
import network.etna.etnawallet.repository.wallets.cryptowallet.model.CryptoBlockchainWallet
import network.etna.etnawallet.repository.wallets.cryptowallet.model.ERCToken
import network.etna.etnawallet.repository.wallets.cryptowallet.model.ERCTokenJsonDeserializer
import network.etna.etnawallet.sdk.api.etna.wssapi.EtnaWSSAPI
import java.lang.reflect.Type
import java.math.BigDecimal

fun EtnaWSSAPI.getBalance(cryptoWallets: List<CryptoBlockchainWallet>): Single<BalanceResponse> {
    val request = BalanceRequest(cryptoWallets)
    val type: Type = object : TypeToken<BalanceResponse>() {}.type

    return apiCore
        .requestBuilder()
        .eventName("balance")
        .eventObject(request)
        .gsonBuilderOptions {
            registerTypeAdapter(ERCToken::class.java, ERCTokenJsonDeserializer())
            registerTypeAdapter(BalanceTokenModel::class.java, BalanceTokenModelJsonDeserializer())
        }
        .build(type)
}

class BalanceRequest(
    val wallets: List<CryptoBlockchainWallet>
)

typealias BalanceResponse = List<BalanceWalletModelResponse>

class BalanceWalletModelResponse(
    val id: String,
    val blockchains: List<BalanceBlockchainModelResponse>
)

class BalanceBlockchainModelResponse(
    val id: String,
    val coin: BalanceCoinModelResponse,
    val address: String,
    val weight: Double,
    var tokens: List<BalanceTokenModel>?
)

class BalanceCoinModelResponse(
    val id: String,
    val referenceId: String,
    val balance: BigDecimal?
)

class BalanceTokenModel(
    val balance: Double?,
    val token: ERCToken
)

class BalanceTokenModelJsonDeserializer: JsonDeserializer<BalanceTokenModel> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): BalanceTokenModel {
        val jsonObject = json.asJsonObject
        val token: ERCToken = context.deserialize(jsonObject, ERCToken::class.java)

        return BalanceTokenModel(
            jsonObject.get("balance").asDouble,
            token
        )
    }
}
