package network.etna.etnawallet.ui.recoveryphrase.show

import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import network.etna.etnawallet.repository.wallets.cryptowalletcredentials.CryptoWalletCredentialsRepository
import network.etna.etnawallet.ui.addwallet.importmultiwallet.WordModel
import network.etna.etnawallet.ui.addwallet.importmultiwallet.WordStatus
import network.etna.etnawallet.ui.recoveryphrase.RecoveryPhraseScenarioModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RecoveryPhraseShowViewModel: ViewModel(), KoinComponent {

    private val credentialsRepository: CryptoWalletCredentialsRepository by inject()
    private val scenarioModel: RecoveryPhraseScenarioModel by inject()

    fun getDataObservable(): Observable<List<WordModel>> {
        return Observable
            .just(
                credentialsRepository
                    .getRecoveryPhrase(scenarioModel.walletId)
                    .map {
                        WordModel(it, WordStatus.NORMAL, false)
                    }
            )
    }
}