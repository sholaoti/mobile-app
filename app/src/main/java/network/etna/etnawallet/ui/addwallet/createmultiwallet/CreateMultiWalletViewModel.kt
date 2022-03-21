package network.etna.etnawallet.ui.addwallet.createmultiwallet

import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import network.etna.etnawallet.repository.wallets.cryptowallet.CryptoWalletsRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CreateMultiWalletViewModel: ViewModel(), KoinComponent {

    private val walletsRepository: CryptoWalletsRepository by inject()

    private val walletNameSubject: BehaviorSubject<String> = BehaviorSubject.createDefault("")

    fun walletNameChanged(text: String) {
        walletNameSubject.onNext(text)
    }

    fun getCreateAvailableObservable(): Observable<Boolean> {
        return walletNameSubject
            .map { it.isNotEmpty() }
    }

    fun createMultiWallet(): Single<String> {
        val walletName = walletNameSubject.value ?: "New wallet"
        return walletsRepository
            .generateMultiWallet(walletName)
    }
}