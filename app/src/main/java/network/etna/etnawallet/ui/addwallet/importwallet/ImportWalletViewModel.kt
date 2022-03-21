package network.etna.etnawallet.ui.addwallet.importwallet

import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import network.etna.etnawallet.repository.blockchainnetworks.BlockchainNetworksRepository
import network.etna.etnawallet.sdk.api.etna.model.BlockchainPlatformModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ImportWalletViewModel: ViewModel(), KoinComponent {

    private val blockchainNetworksRepository: BlockchainNetworksRepository by inject()

    fun getPlatformModelsObservable(): Observable<List<BlockchainPlatformModel>> {
        return blockchainNetworksRepository
            .getPlatformsObservable()
    }
}