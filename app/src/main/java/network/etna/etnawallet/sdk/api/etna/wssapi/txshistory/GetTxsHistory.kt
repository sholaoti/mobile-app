package network.etna.etnawallet.sdk.api.etna.wssapi.txshistory

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import io.reactivex.Single
import network.etna.etnawallet.sdk.api.etna.wssapi.EtnaWSSAPI
import network.etna.etnawallet.sdk.api.etna.wssapi.EtnaWSSBaseResponse
import network.etna.etnawallet.ui.helpers.extensions.FormatAmountString
import java.lang.reflect.Type
import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.*

fun EtnaWSSAPI.getTxsHistory(address: String, platform: String): Single<TxsHistory> {
    val request = GetTxsHistoryRequest(address, platform)
    val type: Type = object : TypeToken<EtnaWSSBaseResponse<TxsHistory>>() {}.type

    return apiCore
        .requestBuilder()
        .eventName("txsHistory")
        .eventObject(request)
        .gsonBuilderOptions {
            registerTypeAdapter(
                TxHistoryTransaction::class.java,
                TxHistoryTransactionJsonDeserializer()
            )
        }
        .build<EtnaWSSBaseResponse<TxsHistory>>(type)
        .flatMap {
            it.getResult()
        }
}

class GetTxsHistoryRequest(
    val address: String,
    val platform: String
)

class TxsHistory(
    val txs: List<TxHistoryTransaction>
)

enum class TxHistoryTransactionType {
    Send, Receive
}

class TxHistoryTransaction(
    val currency: String,
    val success: Boolean,
    val value: BigDecimal?,
    val type: TxHistoryTransactionType?,
    val date: Date
)

class TxHistoryTransactionJsonDeserializer: JsonDeserializer<TxHistoryTransaction> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): TxHistoryTransaction {
        val jsonObject = json.asJsonObject
        val currency = jsonObject.get("currency").asString
        val success = jsonObject.get("success").asBoolean
        var value: BigDecimal? = null
        if (jsonObject.has("value")) {
            value = jsonObject.get("value").asBigDecimal
        }
        var type: TxHistoryTransactionType? = null
        if (jsonObject.has("type")) {
            type = TxHistoryTransactionType.valueOf(jsonObject.get("type").asString)
        }
        val date = Date(jsonObject.get("timestamp").asLong)

        return TxHistoryTransaction(
            currency,
            success,
            value,
            type,
            date
        )
    }
}

fun TxHistoryTransaction.valueStringPresentation(): FormatAmountString? {
    if (value == null) {
        return null
    }
    val numberFormat = DecimalFormat.getInstance(Locale.GERMAN)
    numberFormat.maximumFractionDigits = 10
    numberFormat.minimumFractionDigits = 0
    val result = numberFormat.format(value.abs())

    return when (type) {
        TxHistoryTransactionType.Send -> FormatAmountString(false, "- $result")
        TxHistoryTransactionType.Receive -> FormatAmountString(true, "+ $result")
        null -> null
    }
}
