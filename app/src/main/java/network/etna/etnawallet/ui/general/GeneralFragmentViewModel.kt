package network.etna.etnawallet.ui.general

import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import network.etna.etnawallet.repository.wallets.etnawallet.BlockchainAsset
import network.etna.etnawallet.repository.wallets.etnawallet.EtnaWalletModel
import network.etna.etnawallet.repository.wallets.etnawallet.EtnaWalletsRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GeneralFragmentViewModel: ViewModel(), KoinComponent {

    private val etnaWalletsRepository: EtnaWalletsRepository by inject()

    fun getActiveWalletObservable(): Observable<EtnaWalletModel> {
        return etnaWalletsRepository
            .getActiveWalletObservable()
    }

    fun getAssetsObservable(): Observable<List<BlockchainAsset>> {
        return etnaWalletsRepository
            .getAssetsObservable()
    }

}