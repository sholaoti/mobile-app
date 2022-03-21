package network.etna.etnawallet.ui.addwallet.importsinglewallet.variants.privatekey

import androidx.lifecycle.ViewModel
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import network.etna.etnawallet.repository.wallets.cryptowallet.CryptoWalletsRepository
import network.etna.etnawallet.repository.wallets.cryptowalletcredentials.CryptoWalletCredentialsRepository
import network.etna.etnawallet.sdk.api.etna.wssapi.EtnaWSSAPI
import network.etna.etnawallet.sdk.api.etna.wssapi.checkwalletvalid.checkWalletValidByPublicKey
import network.etna.etnawallet.ui.addwallet.importsinglewallet.ImportSingleWalletScenarioModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.web3j.crypto.WalletUtils

class ImportSingleWalletPrivateKeyViewModel: ViewModel(), KoinComponent {

    private val scenarioModel: ImportSingleWalletScenarioModel by inject()
    private val cryptoWalletsRepository: CryptoWalletsRepository by inject()
    private val cryptoWalletCredentialsRepository: CryptoWalletCredentialsRepository by inject()
    private val etnaWSSAPI: EtnaWSSAPI by inject()

    private val walletNameSubject: BehaviorSubject<String> = BehaviorSubject.createDefault("")
    private val walletPrivateKeySubject: BehaviorSubject<String> = BehaviorSubject.createDefault("")

    fun getPlatformName(): String {
        return scenarioModel.platform.name
    }

    fun getImportAvailableObservable(): Observable<Boolean> {
        return Observable.combineLatest(
            walletPrivateKeySubject,
            walletNameSubject)
            { walletPrivateKey, walletName ->
                walletPrivateKey.isNotEmpty() && walletName.isNotEmpty()
            }
    }

    fun walletNameChanged(text: String) {
        walletNameSubject.onNext(text)
    }

    fun walletPrivateKeyChanged(text: String) {
        walletPrivateKeySubject.onNext(text)
    }

    fun importPressed(): Completable {

        return try {
            val walletPrivateKey = walletPrivateKeySubject.value!!

            if (!WalletUtils.isValidPrivateKey(walletPrivateKey)) {
                return Completable.error(Exception("invalid key"))
            }

            val walletName = walletNameSubject.value!!
            val platformId = scenarioModel.platform.id

            val walletPublicKey = cryptoWalletCredentialsRepository
                .getPublicKeyByPrivateKey(walletPrivateKey)

            return etnaWSSAPI
                .checkWalletValidByPublicKey(platformId, walletPublicKey.value)
                .flatMapCompletable { walletAddressValid ->
                    if (walletAddressValid) {
                        cryptoWalletsRepository
                            .importFlatWalletByPrivateKey(
                                walletName,
                                platformId,
                                walletPrivateKey
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