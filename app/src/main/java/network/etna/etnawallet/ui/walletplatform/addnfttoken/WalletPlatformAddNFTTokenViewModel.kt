package network.etna.etnawallet.ui.walletplatform.addnfttoken

import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import network.etna.etnawallet.repository.blockchainnetworks.BlockchainNetworksRepository
import network.etna.etnawallet.sdk.api.etna.model.BlockchainPlatformModel
import network.etna.etnawallet.ui.asset.AssetScenarioModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class WalletPlatformAddNFTTokenViewModel : ViewModel(), KoinComponent {

    private val scenarioModel: AssetScenarioModel by inject()
    private val blockchainNetworksRepository: BlockchainNetworksRepository by inject()
    private val nftIdSubject: BehaviorSubject<String> = BehaviorSubject.createDefault("1803")
    private val nftAddressSubject: BehaviorSubject<String> = BehaviorSubject.createDefault("0xd34Eb2d530245a60C6151B6cfa6D247Ee92668c7")

    fun getPlatformObservable(): Observable<BlockchainPlatformModel> {
        return blockchainNetworksRepository
            .getPlatformObservable(scenarioModel.platform)
    }

    fun nftIdChanged(value: String) {
        nftIdSubject.onNext(value)
    }

    fun nftAddressChanged(value: String) {
        nftAddressSubject.onNext(value)
    }

    fun isAddTokenAvailable(): Observable<Boolean> {
        return Observable.combineLatest(
            nftAddressSubject.map { it.isNotEmpty() },
            nftIdSubject.map { it.isNotEmpty() }
        ) { nftAddressNotEmpty, nftIdNotEmpty ->
            nftAddressNotEmpty && nftIdNotEmpty
        }
    }

    fun getNftId(): String {
        return nftIdSubject.value!!
    }

    fun getNftAddress(): String {
        return nftAddressSubject.value!!
    }
}