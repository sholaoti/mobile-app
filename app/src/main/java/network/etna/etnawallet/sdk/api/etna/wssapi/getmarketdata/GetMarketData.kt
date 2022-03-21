package network.etna.etnawallet.sdk.api.etna.wssapi.getmarketdata

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import io.reactivex.Single
import network.etna.etnawallet.extensions.prettyPrint
import network.etna.etnawallet.repository.wallets.CurrencyAmount
import network.etna.etnawallet.repository.wallets.etnawallet.AssetIdHolder
import network.etna.etnawallet.sdk.api.etna.wssapi.EtnaWSSAPI
import network.etna.etnawallet.sdk.api.etna.wssapi.EtnaWSSBaseResponse
import network.etna.etnawallet.sdk.network.Currency
import network.etna.etnawallet.ui.helpers.extensions.formatAmount
import java.lang.reflect.Type
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

fun EtnaWSSAPI.getMarketData(currency: String, assetIdHolder: AssetIdHolder): Single<MarketData> {
    val request = GetMarketInfoRequest(currency, assetIdHolder.getMarketPlatform(), assetIdHolder.getContractAddress())
    val type: Type = object : TypeToken<EtnaWSSBaseResponse<MarketData>>() {}.type

    return apiCore
        .requestBuilder()
        .eventName("marketData")
        .eventObject(request)
        .gsonBuilderOptions {
            registerTypeAdapter(
                MarketDataRowValue::class.java,
                MarketDataRowValueJsonDeserializer()
            )
        }
        .build<EtnaWSSBaseResponse<MarketData>>(type)
        .flatMap {
            it.getResult()
        }
}

class GetMarketInfoRequest(
    val currency: String,
    val platform: String,
    val contractAddress: String?
)

class MarketData(
    val groups: List<MarketDataGroup>
)

class MarketDataGroup(
    val rows: List<MarketDataRow>,
    val title: String?
)

class MarketDataRow(
    val title: String,
    val value: MarketDataRowValue
)

sealed class MarketDataRowValue {
    class Integer(val value: Int): MarketDataRowValue()
    class Price(val amount: BigDecimal, val currency: Currency, val precision: Int): MarketDataRowValue()
    class DateDay(val date: Date): MarketDataRowValue()
    class Pretty(val value: BigDecimal): MarketDataRowValue()
    object Unsupported: MarketDataRowValue()

    override fun toString(): String {
        return when (this) {
            is Integer -> value.toString()
            is Price -> {
                CurrencyAmount(
                    amount,
                    currency
                ).formatAmount(precision)
            }
            is DateDay -> {
                val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale("en", "US"))
                simpleDateFormat.format(date)
            }
            is Pretty -> {
                value.prettyPrint()
            }
            is Unsupported -> "Unsupported"
        }
    }
}

class MarketDataRowValueJsonDeserializer: JsonDeserializer<MarketDataRowValue> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): MarketDataRowValue {
        val jsonObject = json.asJsonObject

        return when (jsonObject.get("type").asString) {
            "integer" -> {
                MarketDataRowValue.Integer(
                    jsonObject.get("value").asInt
                )
            }
            "price" -> {
                val value = jsonObject.getAsJsonObject("value")
                MarketDataRowValue.Price(
                    BigDecimal(value.get("amount").asString),
                    Currency.valueOf(value.get("currency").asString.uppercase()),
                    value.get("precision").asInt
                )
            }
            "date" -> {
                MarketDataRowValue.DateDay(
                    Date(jsonObject.get("value").asLong)
                )
            }
            "pretty" -> {
                MarketDataRowValue.Pretty(
                    BigDecimal(jsonObject.get("value").asString)
                )
            }
            else -> MarketDataRowValue.Unsupported
        }
    }
}