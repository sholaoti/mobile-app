package network.etna.etnawallet.ui.profile.wallets.walletinfo

import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import network.etna.etnawallet.repository.wallets.cryptowallet.CryptoWalletsRepository
import network.etna.etnawallet.repository.wallets.cryptowalletcredentials.CryptoWalletCredentialsRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ProfileWalletInfoViewModel(
    private val wallet_id: String
) : ViewModel(), KoinComponent {
    private val walletsRepository: CryptoWalletsRepository by inject()
    private val cryptoWalletCredentialsRepository: CryptoWalletCredentialsRepository by inject()

    fun getWalletNameObservable(): Observable<String> {
        return walletsRepository
            .getWalletObservable(wallet_id)
            .map { it.name }
    }

    fun isWalletActiveObservable(): Observable<Boolean> {
        return walletsRepository
            .getWalletObservable(wallet_id)
            .map { it.isActive }
    }

    fun makeActiveWallet() {
        walletsRepository.selectActiveWallet(wallet_id)
    }

    fun isWalletAvailable(): Boolean {
        return walletsRepository.hasWallet(wallet_id)
    }

    fun hasRecoveryPhrase(): Boolean {
        return cryptoWalletCredentialsRepository
            .hasRecoveryPhrase(wallet_id)
    }
}