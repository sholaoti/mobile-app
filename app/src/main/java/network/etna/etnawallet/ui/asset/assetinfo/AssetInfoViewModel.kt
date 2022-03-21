package network.etna.etnawallet.ui.asset.assetinfo

import io.reactivex.Observable
import network.etna.etnawallet.repository.blockchainnetworks.BlockchainNetworksRepository
import network.etna.etnawallet.repository.loadingIndicatorService.LoadingIndicatorState
import network.etna.etnawallet.repository.wallets.etnawallet.AssetIdHolder
import network.etna.etnawallet.repository.wallets.etnawallet.BlockchainAsset
import network.etna.etnawallet.repository.wallets.etnawallet.EtnaWalletsRepository
import network.etna.etnawallet.sdk.api.etna.model.BlockchainPlatformModel
import network.etna.etnawallet.ui.asset.AssetScenarioModel
import network.etna.etnawallet.ui.base.loading.LoadingViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AssetInfoViewModel : LoadingViewModel(), KoinComponent {

    private val scenarioModel: AssetScenarioModel by inject()
    private val etnaWalletsRepository: EtnaWalletsRepository by inject()
    private val blockchainNetworksRepository: BlockchainNetworksRepository by inject()

    fun getAssetObservable(): Observable<BlockchainAsset> {
        return etnaWalletsRepository
            .getAssetObservable(scenarioModel.assetIdHolder)
            .doOnNext {
                loadingSubject.onNext(LoadingIndicatorState.NORMAL)
            }
    }

    fun getPlatform(): String {
        return scenarioModel.platform
    }

    fun getAssetIdHolder(): AssetIdHolder {
        return scenarioModel.assetIdHolder
    }

    fun getAssetId(): String {
        return scenarioModel.assetIdHolder.assetId
    }

    fun getPlatformObservable(): Observable<BlockchainPlatformModel> {
        return blockchainNetworksRepository
            .getPlatformObservable(getPlatform())
    }

    fun isAssetAvailable(): Boolean {
        return etnaWalletsRepository.getAsset(scenarioModel.assetIdHolder) != null
    }
}