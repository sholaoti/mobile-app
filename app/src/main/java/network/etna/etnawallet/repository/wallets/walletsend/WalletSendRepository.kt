package network.etna.etnawallet.repository.wallets.walletsend

import io.reactivex.Completable

interface WalletSendRepository {
    fun sendTransaction(model: WalletSendModel): Completable
}