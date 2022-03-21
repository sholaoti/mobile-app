package network.etna.etnawallet.ui.profile.settings

import androidx.lifecycle.ViewModel
import network.etna.etnawallet.repository.coinrate.CoinRateRepository
import network.etna.etnawallet.repository.wallets.networkset.NetworkSet
import network.etna.etnawallet.sdk.network.Currency
import io.reactivex.Observable
import network.etna.etnawallet.repository.blockchainnetworks.BlockchainNetworksRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ProfileSettingsViewModel : ViewModel(), KoinComponent {

    private val coinRateRepository: CoinRateRepository by inject()
    private val blockchainNetworksRepository: BlockchainNetworksRepository by inject()

    fun getActiveCurrencyObservable(): Observable<Currency> {
        return coinRateRepository
            .getActiveCurrencyObservable()
    }

    fun getActiveNetworkSetObservable(): Observable<NetworkSet> {
        return blockchainNetworksRepository
            .getNetworkSetModelsObservable()
            .map { networkSetModels ->
                networkSetModels.first { it.isActive }.network
            }
    }
}