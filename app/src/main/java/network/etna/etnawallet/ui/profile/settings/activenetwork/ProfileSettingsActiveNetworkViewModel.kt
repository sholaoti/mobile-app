package network.etna.etnawallet.ui.profile.settings.activenetwork

import androidx.lifecycle.ViewModel
import network.etna.etnawallet.R
import network.etna.etnawallet.repository.wallets.networkset.NetworkSet
import network.etna.etnawallet.repository.wallets.networkset.NetworkSetModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import network.etna.etnawallet.repository.blockchainnetworks.BlockchainNetworksRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ProfileSettingsActiveNetworkViewModel: ViewModel(), KoinComponent {

    private val blockchainNetworksRepository: BlockchainNetworksRepository by inject()

    private val composite by lazy { CompositeDisposable() }


    fun getNetworkSetModelsObservable(): Observable<List<NetworkSetModel>> {
        return blockchainNetworksRepository
            .getNetworkSetModelsObservable()
    }

    fun selectActiveNetworkSet(networkSet: NetworkSet) {

        composite.add(
            blockchainNetworksRepository
                .selectActiveNetwork(networkSet)
                .subscribe(
                    {
                        val x = 1
                    },
                    {
                        val x = 1
                    }
                )
        )
    }

    fun showTestnetWarning(): Boolean {
        return blockchainNetworksRepository.showTestnetWarning()
    }

    override fun onCleared() {
        super.onCleared()
        composite.dispose()
    }
}

val NetworkSet.humanReadableNameResource: Int
    get() {
        return when (this) {
            NetworkSet.MAINNET -> R.string.mainnet
            NetworkSet.TESTNET -> R.string.testnet
        }
    }

val NetworkSet.icDrawableResource: Int
    get() {
        return when (this) {
            NetworkSet.MAINNET -> R.drawable.profile_active_network_main
            NetworkSet.TESTNET -> R.drawable.profile_active_network_test
        }
    }
