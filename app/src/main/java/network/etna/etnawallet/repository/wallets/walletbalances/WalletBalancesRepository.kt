package network.etna.etnawallet.repository.wallets.walletbalances

import io.reactivex.Observable
import network.etna.etnawallet.sdk.api.etna.wssapi.balance.BalanceResponse

interface WalletBalancesRepository {
    fun getWalletBalancesObservable(): Observable<BalanceResponse>
}