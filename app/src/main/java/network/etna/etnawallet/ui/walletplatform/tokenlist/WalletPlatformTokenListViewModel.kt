package network.etna.etnawallet.ui.walletplatform.tokenlist

import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import network.etna.etnawallet.repository.blockchainnetworks.BlockchainNetworksRepository
import network.etna.etnawallet.repository.wallets.cryptowallet.CryptoWalletsRepository
import network.etna.etnawallet.repository.wallets.cryptowallet.model.ERCToken
import network.etna.etnawallet.sdk.api.etna.model.BlockchainPlatformModel
import network.etna.etnawallet.ui.asset.AssetScenarioModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class WalletPlatformTokenListViewModel : ViewModel(), KoinComponent {

    private val scenarioModel: AssetScenarioModel by inject()
    private val blockchainNetworksRepository: BlockchainNetworksRepository by inject()
    private val cryptoWalletsRepository: CryptoWalletsRepository by inject()

    fun getPlatformObservable(): Observable<BlockchainPlatformModel> {
        return blockchainNetworksRepository
            .getPlatformObservable(scenarioModel.platform)
    }

    fun getTokenListObservable(): Observable<List<ERCToken.ERC20>> {
        return cryptoWalletsRepository
            .getActiveWalletTokenList(getPlatformId())
            .map {
                it.mapNotNull { it as? ERCToken.ERC20 }
            }
    }

    fun getPlatformId(): String {
        return scenarioModel.platform
    }

    fun updateTokenVisibility(tokenAddress: String, isVisible: Boolean) {
        cryptoWalletsRepository.setTokenVisibility(getPlatformId(), tokenAddress, isVisible)
    }
}