package network.etna.etnawallet.ui.asset.send.selecttoken

import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import network.etna.etnawallet.repository.wallets.etnawallet.AssetIdHolder
import network.etna.etnawallet.repository.wallets.etnawallet.EtnaWalletsRepository
import network.etna.etnawallet.repository.wallets.etnawallet.URLProvider
import network.etna.etnawallet.ui.asset.send.SendScenarioModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SelectTokenModel(
    val id: String,
    val platform: String,
    val name: String,
    val isSelected: Boolean,
    val iconURLProvider: URLProvider,
    val assetIdHolder: AssetIdHolder
)

class SelectTokenViewModel: ViewModel(), KoinComponent {

    private val etnaWalletsRepository: EtnaWalletsRepository by inject()
    private val sendScenarioModel: SendScenarioModel by inject()

    fun getTokensObservable(): Observable<List<SelectTokenModel>> {
        return Observable.combineLatest(
            etnaWalletsRepository
                .getAssetsObservable(),
            sendScenarioModel
                .getAssetIdObservable()
        ) { assets, assetId ->
            assets.map {
                SelectTokenModel(
                    it.id,
                    it.platform,
                    it.name,
                    it.id == assetId.assetId,
                    it.iconURLProvider,
                    assetId
                )
            }
        }
    }

    fun selectAssetIdHolder(id: AssetIdHolder) {
        sendScenarioModel.updateAsset(id)
    }

}