package network.etna.etnawallet.ui.asset.assetinfo.marketchart

import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import network.etna.etnawallet.extensions.isSameDay
import network.etna.etnawallet.repository.coinrate.CoinRateRepository
import network.etna.etnawallet.repository.loadingIndicatorService.LoadingIndicatorState
import network.etna.etnawallet.repository.refresh.RefreshInterface
import network.etna.etnawallet.repository.wallets.CurrencyAmount
import network.etna.etnawallet.sdk.api.etna.wssapi.EtnaWSSAPI
import network.etna.etnawallet.sdk.api.etna.wssapi.getmarketchart.MarketChart
import network.etna.etnawallet.sdk.api.etna.wssapi.getmarketchart.getMarketChart
import network.etna.etnawallet.sdk.network.Currency
import network.etna.etnawallet.ui.asset.AssetScenarioModel
import network.etna.etnawallet.ui.base.loading.LoadingViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

enum class MarketChartPeriodType {
    DAY, WEEK, MONTH, YEAR
}

class MarketChartInfo(
    val lineDataSet: LineDataSet,
    val isPositive: Boolean,
    val price: CurrencyAmount,
    val percent: Double,
    val correction: Float,
    val xAxisCaps: List<String>
)

class MarketChartViewModel : LoadingViewModel(), RefreshInterface, KoinComponent {
    private val etnaWSSAPI: EtnaWSSAPI by inject()
    private val coinRateRepository: CoinRateRepository by inject()
    private val scenarioModel: AssetScenarioModel by inject()

    private val marketChartPeriodTypeSubject: BehaviorSubject<MarketChartPeriodType> =
        BehaviorSubject.createDefault(MarketChartPeriodType.YEAR)

    private val dataSubject: PublishSubject<Pair<MarketChart, Currency>> = PublishSubject.create()

    private var activeCurrency: Currency? = null

    init {
        composite.add(
            coinRateRepository
                .getActiveCurrencyObservable()
                .subscribe(
                    {
                        activeCurrency = it
                        composite.add(
                            refresh()
                                .subscribe()
                        )
                    },
                    {

                    }
                )
        )
    }

    override fun refresh(): Completable {
        if (activeCurrency == null) {
            return Completable.complete()
        }

        return etnaWSSAPI
            .getMarketChart(activeCurrency!!.name.lowercase(), scenarioModel.assetIdHolder)
            .retryWhen { throwable ->
                throwable.delay(3, TimeUnit.SECONDS)
            }
            .doOnSuccess {
                dataSubject.onNext(Pair(it, activeCurrency!!))
                loadingSubject.onNext(LoadingIndicatorState.NORMAL)
            }
            .flatMapCompletable {
                Completable.complete()
            }
    }

    fun getMarketChartInfoObservable(): Flowable<MarketChartInfo> {
        return Observable.combineLatest(
            dataSubject,
            marketChartPeriodTypeSubject)
            { pair, type ->

                val data = pair.first
                val activeCurrency = pair.second

                val period = when (type) {
                    MarketChartPeriodType.DAY -> data.day
                    MarketChartPeriodType.WEEK -> data.week
                    MarketChartPeriodType.MONTH -> data.month
                    MarketChartPeriodType.YEAR -> data.year
                }

                val points = period.points

                val correction = points.minByOrNull { it.value }?.value ?: 0F

                val entries = points.map {
                    val timestampSecondsFloat = (it.timestamp / 1000).toFloat()
                    Entry(timestampSecondsFloat, it.value - correction)
                }

                val vl = LineDataSet(entries, "My Type")

                val xAxisCaps: List<String> = when (type) {
                    MarketChartPeriodType.DAY -> {
                        val result: MutableList<String> = ArrayList()
                        val startTime = points.firstOrNull()?.timestamp
                        val endTime = points.lastOrNull()?.timestamp

                        if (startTime != null && endTime != null) {
                            val startDate = Date(startTime)
                            val endDate = Date(endTime)
                            val simpleDateFormat = SimpleDateFormat("HH:mm", Locale("en", "US"))
                            result.add(simpleDateFormat.format(startDate))
                            val count = 7

                            val timeSpace = (endTime - startTime) / count

                            for(i in 1 until count) {
                                result.add(simpleDateFormat.format(Date(startTime + timeSpace * i)))
                            }

                            result.add(simpleDateFormat.format(endDate))
                        }
                        result
                    }
                    MarketChartPeriodType.WEEK -> {
                        val result: MutableList<String> = ArrayList()
                        val startTime = points.firstOrNull()?.timestamp
                        val endTime = points.lastOrNull()?.timestamp
                        val simpleDateFormat = SimpleDateFormat("E", Locale("en", "US"))

                        if (startTime != null && endTime != null) {
                            val startDate = Date(startTime)
                            val endDate = Date(endTime)
                            result.add(simpleDateFormat.format(endDate))

                            val c = Calendar.getInstance()
                            c.time = endDate

                            while (!startDate.isSameDay(c.time) && result.size < 10) {
                                c.add(Calendar.DAY_OF_MONTH, -1)
                                result.add(simpleDateFormat.format(c.time))
                            }
                        }
                        result.reversed()
                    }
                    MarketChartPeriodType.MONTH -> {
                        val result: MutableList<String> = ArrayList()
                        val startTime = points.firstOrNull()?.timestamp
                        val endTime = points.lastOrNull()?.timestamp

                        val simpleDateFormat = SimpleDateFormat("d MMM", Locale("en", "US"))

                        if (startTime != null && endTime != null) {
                            result.add(simpleDateFormat.format(Date(startTime)))

                            val count = 8

                            val timeSpace = (endTime - startTime) / count

                            for(i in 1 until count) {
                                result.add(simpleDateFormat.format(Date(startTime + timeSpace * i)))
                            }

                            result.add(simpleDateFormat.format(Date(endTime)))
                        }
                        result
                    }
                    MarketChartPeriodType.YEAR -> {
                        val d = Date(points.lastOrNull()?.timestamp ?: Date().time)
                        val c = Calendar.getInstance()
                        c.time = d
                        c.set(Calendar.DAY_OF_MONTH, 1)

                        val result: MutableList<String> = ArrayList()

                        val simpleDateFormat = SimpleDateFormat("MMM", Locale("en", "US"))
                        for (i in 0 until 12) {
                            result.add(simpleDateFormat.format(c.time))
                            c.add(Calendar.MONTH, -1)
                        }
                        result.reversed()
                    }
                }

                MarketChartInfo(
                    vl,
                    period.endValue >= period.startValue,
                    CurrencyAmount(
                        BigDecimal(period.endValue),
                        activeCurrency
                    ),
                    period.percent,
                    correction,
                    xAxisCaps
                )
            }
            .doOnNext {
                loadingSubject.onNext(LoadingIndicatorState.NORMAL)
            }
            .toFlowable(BackpressureStrategy.LATEST)
    }

    fun updateMarketChartPeriodType(type: MarketChartPeriodType) {
        marketChartPeriodTypeSubject.onNext(type)
    }
}