package network.etna.etnawallet.ui.asset.assetinfo.txshistory

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import network.etna.etnawallet.repository.loadingIndicatorService.LoadingIndicatorState
import network.etna.etnawallet.repository.refresh.RefreshInterface
import network.etna.etnawallet.sdk.api.etna.wssapi.EtnaWSSAPI
import network.etna.etnawallet.sdk.api.etna.wssapi.txshistory.TxHistoryTransaction
import network.etna.etnawallet.sdk.api.etna.wssapi.txshistory.getTxsHistory
import network.etna.etnawallet.ui.asset.AssetScenarioModel
import network.etna.etnawallet.ui.base.loading.LoadingViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TxsHistoryViewModel : LoadingViewModel(), RefreshInterface, KoinComponent {
    private val etnaWSSAPI: EtnaWSSAPI by inject()
    private val scenarioModel: AssetScenarioModel by inject()

    private val dataSubject: PublishSubject<List<TxHistoryTransaction>> = PublishSubject.create()

    init {
        composite.add(
            refresh()
                .subscribe()
        )
    }

    fun getDataObservable(): Observable<List<TxHistoryTransaction>> {
        return dataSubject
    }

    override fun refresh(): Completable {
        return etnaWSSAPI
            .getTxsHistory(scenarioModel.walletAddress, scenarioModel.platform)
            .map {
                it.txs
            }
            .onErrorReturn { emptyList() }
            .doOnSuccess {
                dataSubject.onNext(it)
                loadingSubject.onNext(LoadingIndicatorState.NORMAL)
            }
            .flatMapCompletable {
                Completable.complete()
            }
    }
}