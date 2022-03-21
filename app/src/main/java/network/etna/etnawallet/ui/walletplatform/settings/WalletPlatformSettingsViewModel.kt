package network.etna.etnawallet.ui.walletplatform.settings

import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import network.etna.etnawallet.repository.blockchainnetworks.BlockchainNetworksRepository
import network.etna.etnawallet.repository.wallets.etnawallet.EtnaWalletsRepository
import network.etna.etnawallet.sdk.api.etna.model.BlockchainPlatformModel
import network.etna.etnawallet.ui.asset.AssetScenarioModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class WalletPlatformSettingsViewModel : ViewModel(), KoinComponent {

    private val scenarioModel: AssetScenarioModel by inject()
    private val etnaWalletsRepository: EtnaWalletsRepository by inject()
    private val blockchainNetworksRepository: BlockchainNetworksRepository by inject()

    fun getWalletNameObservable(): Observable<String> {
        return etnaWalletsRepository
            .getActiveWalletObservable()
            .map { it.name }
    }

    fun getPlatformObservable(): Observable<BlockchainPlatformModel> {
        return blockchainNetworksRepository
            .getPlatformObservable(scenarioModel.platform)
    }
}