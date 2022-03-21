package network.etna.etnawallet.ui.profile.wallets.delete.confirmation

import androidx.lifecycle.ViewModel
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import network.etna.etnawallet.repository.wallets.cryptowallet.CryptoWalletsRepository
import network.etna.etnawallet.ui.profile.wallets.delete.DeleteWalletScenarioModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DeleteWalletConfirmationViewModel : ViewModel(), KoinComponent {
    private val deleteWalletScenarioModel: DeleteWalletScenarioModel by inject()

    private val walletsRepository: CryptoWalletsRepository by inject()

    private val phraseSubject: BehaviorSubject<String> = BehaviorSubject.createDefault("")

    private val deleteDescAcceptedSubject: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false)

    fun acceptDeleteDesc() {
        deleteDescAcceptedSubject.onNext(true)
    }

    fun isDeleteDescAccepted(): Observable<Boolean> {
        return deleteDescAcceptedSubject
    }

    fun isDeleteWalletAllowed(): Observable<Boolean> {
        return Observable.combineLatest(
            phraseSubject
                .map { it == "Wallet, wallet, wallet" },
            deleteDescAcceptedSubject)
            { isPhraseCorrect, isDeleteDescAccepted ->
                isPhraseCorrect && isDeleteDescAccepted
            }

    }

    fun phraseChanged(text: String) {
        phraseSubject.onNext(text)
    }

    fun deleteWallet(): Completable {
        return walletsRepository
            .deleteWallet(deleteWalletScenarioModel.walletId)
    }
}