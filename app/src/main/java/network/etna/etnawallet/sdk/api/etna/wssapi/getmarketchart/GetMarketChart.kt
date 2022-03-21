package network.etna.etnawallet.sdk.api.etna.wssapi.getmarketchart

import com.google.gson.reflect.TypeToken
import io.reactivex.Single
import network.etna.etnawallet.repository.wallets.etnawallet.AssetIdHolder
import network.etna.etnawallet.sdk.api.etna.wssapi.EtnaWSSAPI
import network.etna.etnawallet.sdk.api.etna.wssapi.EtnaWSSBaseResponse
import java.lang.reflect.Type

fun EtnaWSSAPI.getMarketChart(currency: String, assetIdHolder: AssetIdHolder): Single<MarketChart> {
    val request = GetMarketChartRequest(currency, assetIdHolder.getMarketPlatform(), assetIdHolder.getContractAddress())
    val type: Type = object : TypeToken<EtnaWSSBaseResponse<MarketChart>>() {}.type

    return apiCore
        .requestBuilder()
        .eventName("marketChart")
        .eventObject(request)
        .build<EtnaWSSBaseResponse<MarketChart>>(type)
        .flatMap {
            it.getResult()
        }
}

class GetMarketChartRequest(
    val currency: String,
    val platform: String,
    val contractAddress: String?
)

class MarketChart(
    val day: MarketChartPeriod,
    val week: MarketChartPeriod,
    val month: MarketChartPeriod,
    val year: MarketChartPeriod
)

class MarketChartPeriod(
    val percent: Double,
    val startValue: Double,
    val endValue: Double,
    val points: List<MarketChartPoint>
)

class MarketChartPoint(
    val timestamp: Long,
    val label: String,
    val value: Float
)
