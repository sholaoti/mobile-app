package network.etna.etnawallet.ui.addwallet.importsinglewallet.variants.address

import androidx.lifecycle.ViewModel
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import network.etna.etnawallet.repository.wallets.cryptowallet.CryptoWalletsRepository
import network.etna.etnawallet.sdk.api.etna.wssapi.EtnaWSSAPI
import network.etna.etnawallet.sdk.api.etna.wssapi.checkwalletvalid.checkWalletValidByAddress
import network.etna.etnawallet.ui.addwallet.importsinglewallet.ImportSingleWalletScenarioModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ImportSingleWalletAddressViewModel: ViewModel(), KoinComponent {

    private val scenarioModel: ImportSingleWalletScenarioModel by inject()
    private val cryptoWalletsRepository: CryptoWalletsRepository by inject()
    private val etnaWSSAPI: EtnaWSSAPI by inject()

    private val walletNameSubject: BehaviorSubject<String> = BehaviorSubject.createDefault("")
    private val walletAddressSubject: BehaviorSubject<String> = BehaviorSubject.createDefault("")

    fun getPlatformName(): String {
        return scenarioModel.platform.name
    }

    fun getImportAvailableObservable(): Observable<Boolean> {
        return Observable.combineLatest(
            walletAddressSubject,
            walletNameSubject)
            { walletAddress, walletName ->
                walletAddress.isNotEmpty() && walletName.isNotEmpty()
            }
    }

    fun walletNameChanged(text: String) {
        walletNameSubject.onNext(text)
    }

    fun walletAddressChanged(text: String) {
        walletAddressSubject.onNext(text)
    }

    fun importPressed(): Completable {
        return try {
            val walletName = walletNameSubject.value!!
            val walletAddress = walletAddressSubject.value!!
            val platformId = scenarioModel.platform.id

            etnaWSSAPI
                .checkWalletValidByAddress(platformId, walletAddress)
                .flatMapCompletable { walletAddressValid ->
                    if (walletAddressValid) {
                        cryptoWalletsRepository
                            .importFlatWalletByAddress(
                                walletName,
                                platformId,
                                walletAddress
                            )
                    } else {
                        Completable.error(Exception("Wallet is not valid"))
                    }
                }
        } catch (e: Exception) {
            Completable.error(e)
        }
    }
}