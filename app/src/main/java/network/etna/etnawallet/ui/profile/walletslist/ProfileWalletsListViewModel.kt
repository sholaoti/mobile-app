package network.etna.etnawallet.ui.profile.walletslist

import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import network.etna.etnawallet.repository.wallets.cryptowallet.CryptoWalletModel
import network.etna.etnawallet.repository.wallets.cryptowallet.CryptoWalletsRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ProfileWalletsListViewModel : ViewModel(), KoinComponent {

    private val cryptoWalletsRepository: CryptoWalletsRepository by inject()

    fun getWalletsObservable(): Observable<List<CryptoWalletModel>> {
        return cryptoWalletsRepository
            .getWalletsObservable()
    }
}