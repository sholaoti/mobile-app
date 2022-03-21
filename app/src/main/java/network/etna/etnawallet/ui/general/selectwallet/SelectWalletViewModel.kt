package network.etna.etnawallet.ui.general.selectwallet

import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import network.etna.etnawallet.repository.wallets.cryptowallet.CryptoWalletsRepository
import network.etna.etnawallet.repository.wallets.etnawallet.EtnaWalletModel
import network.etna.etnawallet.repository.wallets.etnawallet.EtnaWalletsRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SelectWalletViewModel: ViewModel(), KoinComponent {

    private val etnaWalletsRepository: EtnaWalletsRepository by inject()
    private val cryptoWalletsRepository: CryptoWalletsRepository by inject()

    fun getWalletModelsObservable(): Observable<List<EtnaWalletModel>> {
        return etnaWalletsRepository
            .getWalletsObservable()
    }

    fun selectActiveWallet(id: String) {
        cryptoWalletsRepository.selectActiveWallet(id)
    }
}