package network.etna.etnawallet.ui.walletplatform.addtoken

import androidx.lifecycle.ViewModel
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import network.etna.etnawallet.repository.blockchainnetworks.BlockchainNetworksRepository
import network.etna.etnawallet.sdk.api.etna.model.BlockchainPlatformModel
import network.etna.etnawallet.sdk.api.etna.wssapi.EtnaWSSAPI
import network.etna.etnawallet.sdk.api.etna.wssapi.checkwalletvalid.checkWalletValidByAddressCompletable
import network.etna.etnawallet.ui.asset.AssetScenarioModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class WalletPlatformAddTokenViewModel : ViewModel(), KoinComponent {

    private val scenarioModel: AssetScenarioModel by inject()
    private val etnaWSSAPI: EtnaWSSAPI by inject()
    private val blockchainNetworksRepository: BlockchainNetworksRepository by inject()
    //private val addressSubject: BehaviorSubject<String> = BehaviorSubject.createDefault("0x51f35073ff7cf54c9e86b7042e59a8cc9709fc46")
    private val addressSubject: BehaviorSubject<String> = BehaviorSubject.createDefault("")

    fun getAddress(): String {
        return addressSubject.value ?: ""
    }

    fun addressChanged(value: String) {
        addressSubject.onNext(value)
    }

    fun checkAddressValid(): Completable {
        return addressSubject
            .take(1)
            .flatMapCompletable {
                etnaWSSAPI
                    .checkWalletValidByAddressCompletable(scenarioModel.platform, it)
            }
    }

    fun getPlatformObservable(): Observable<BlockchainPlatformModel> {
        return blockchainNetworksRepository
            .getPlatformObservable(scenarioModel.platform)
    }

    fun isAddTokenAvailable(): Observable<Boolean> {
        return addressSubject
            .map {
                it.isNotEmpty()
            }
    }
}
