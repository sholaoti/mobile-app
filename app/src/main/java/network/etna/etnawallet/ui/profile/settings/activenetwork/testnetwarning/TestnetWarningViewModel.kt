package network.etna.etnawallet.ui.profile.settings.activenetwork.testnetwarning

import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import network.etna.etnawallet.repository.blockchainnetworks.BlockchainNetworksRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TestnetWarningViewModel: ViewModel(), KoinComponent {

    private val blockchainNetworksRepository: BlockchainNetworksRepository by inject()


    fun showTestnetWarningObservable(): Observable<Boolean> {
        return blockchainNetworksRepository.showTestnetWarningObservable()
    }

    fun invertShowTestnetWarning() {
        blockchainNetworksRepository.setShowTestnetWarning(!blockchainNetworksRepository.showTestnetWarning())
    }

}