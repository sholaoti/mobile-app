package network.etna.etnawallet.ui.asset.assetinfo.marketdata

import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import network.etna.etnawallet.repository.coinrate.CoinRateRepository
import network.etna.etnawallet.repository.loadingIndicatorService.LoadingIndicatorState
import network.etna.etnawallet.repository.refresh.RefreshInterface
import network.etna.etnawallet.repository.refresh.RefreshRepository
import network.etna.etnawallet.sdk.api.etna.wssapi.EtnaWSSAPI
import network.etna.etnawallet.sdk.api.etna.wssapi.getmarketdata.getMarketData
import network.etna.etnawallet.sdk.network.Currency
import network.etna.etnawallet.ui.asset.AssetScenarioModel
import network.etna.etnawallet.ui.asset.assetinfo.AssetInfoMarketData
import network.etna.etnawallet.ui.base.loading.LoadingViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit

class MarketDataViewModel : LoadingViewModel(), RefreshInterface, KoinComponent {
    private val etnaWSSAPI: EtnaWSSAPI by inject()
    private val coinRateRepository: CoinRateRepository by inject()
    private val scenarioModel: AssetScenarioModel by inject()

    private val dataSubject: PublishSubject<List<AssetInfoMarketData>> = PublishSubject.create()

    private var activeCurrency: Currency? = null

    fun getDataObservable(): Flowable<List<AssetInfoMarketData>> {
        return dataSubject
            .toFlowable(BackpressureStrategy.LATEST)
    }

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
            .getMarketData(activeCurrency!!.name.lowercase(), scenarioModel.assetIdHolder)
            .map { data ->
                data.groups
                    .map { marketDataGroup ->
                        listOf(AssetInfoMarketData.Title(marketDataGroup.title ?: ""))
                            .filter { it.title.isNotEmpty() } +
                                marketDataGroup.rows.map { marketDataRow ->
                                    AssetInfoMarketData.Row(
                                        marketDataRow.title,
                                        marketDataRow.value.toString()
                                    )
                                } +
                                listOf(AssetInfoMarketData.Line)
                    }
//                            .map {
//                                it + it + it + it + it + it + it + it + it + it + it + it + it
//                            }
                    .fold(listOf<AssetInfoMarketData>(AssetInfoMarketData.Line)) { acc, value -> acc + value }
            }
            .retryWhen { throwable ->
                throwable.delay(3, TimeUnit.SECONDS)
            }
            .doOnSuccess {
                dataSubject.onNext(it)
                loadingSubject.onNext(LoadingIndicatorState.NORMAL)
            }
            .flatMapCompletable {
                Completable.complete()
            }
    }
}