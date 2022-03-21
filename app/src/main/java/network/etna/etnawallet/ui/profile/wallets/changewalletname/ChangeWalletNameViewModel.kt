package network.etna.etnawallet.ui.profile.wallets.changewalletname

import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import network.etna.etnawallet.repository.wallets.cryptowallet.CryptoWalletsRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ChangeWalletNameViewModel(
    private val wallet_id: String
) : ViewModel(), KoinComponent {
    private val walletsRepository: CryptoWalletsRepository by inject()

    private val walletNameSubject: BehaviorSubject<String> = BehaviorSubject.createDefault("")

    fun getWalletName(): Single<String> {
        return walletsRepository
            .getWalletObservable(wallet_id)
            .map { it.name }
            .take(1)
            .doOnNext {
                walletNameSubject.onNext(it)
            }
            .singleOrError()
    }

    fun isSaveAllowed(): Observable<Boolean> {
        return walletNameSubject
            .map { it.isNotEmpty() }
    }

    fun walletNameChanged(text: String) {
        walletNameSubject.onNext(text)
    }

    fun setupName() {
        walletNameSubject.value?.let {
            walletsRepository.changeName(wallet_id, it)
        }
    }
}