package network.etna.etnawallet.repository.wallets.walletbalances

import android.annotation.SuppressLint
import com.google.gson.reflect.TypeToken
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import network.etna.etnawallet.repository.coinrate.CoinRateRepository
import network.etna.etnawallet.repository.coinrate.PlatformTokensSearchModel
import network.etna.etnawallet.repository.refresh.RefreshInterface
import network.etna.etnawallet.repository.refresh.RefreshRepository
import network.etna.etnawallet.repository.wallets.cryptowallet.CryptoWalletsRepository
import network.etna.etnawallet.repository.wallets.cryptowallet.model.CryptoBlockchainWallet
import network.etna.etnawallet.sdk.api.etna.wssapi.EtnaWSSAPI
import network.etna.etnawallet.sdk.api.etna.wssapi.balance.BalanceRequest
import network.etna.etnawallet.sdk.api.etna.wssapi.balance.BalanceResponse
import network.etna.etnawallet.sdk.api.etna.wssapi.balance.getBalance
import network.etna.etnawallet.sdk.core.APICore
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.lang.reflect.Type
import java.util.*

@SuppressLint("CheckResult")
class WalletBalancesRepositoryImpl: WalletBalancesRepository, RefreshInterface, KoinComponent {

    private val etnaWSSAPI: EtnaWSSAPI by inject()
    private val cryptoWalletsRepository: CryptoWalletsRepository by inject()
    private val coinRateRepository: CoinRateRepository by inject()
    private val apiCore: APICore by inject()
    private val refreshRepository: RefreshRepository by inject()

    private val balanceResponseSubject: BehaviorSubject<Optional<BalanceResponse>> =
        BehaviorSubject.createDefault(Optional.empty())

    private var cryptoWallets: List<CryptoBlockchainWallet>? = null

    init {

        apiCore
            .subscribe("update")
            .subscribe {
                println(it)
                val x = 1
            }

        cryptoWalletsRepository
            .getCryptoBlockchainWalletsObservable()
            .distinctUntilChanged()
            .subscribe { cryptoWallets ->
                this.cryptoWallets = cryptoWallets
                updateBalance()
                    .subscribe(
                        {
                            val x = 1
                        },
                        {
                            val x = 1
                        }
                    )
            }

        refreshRepository.register(this)
    }

    private fun updateBalance(): Completable {
        if (cryptoWallets == null) {
            return Completable.complete()
        }

        return etnaWSSAPI
            .getBalance(cryptoWallets!!)
            .doOnSuccess {
                    balanceResponse ->
                val coinIds: MutableList<String> = mutableListOf()
                val tokens: MutableList<PlatformTokensSearchModel> = mutableListOf()

                balanceResponse.forEach { balanceWalletModelResponse ->
                    balanceWalletModelResponse.blockchains.forEach { balanceBlockchainModelResponse ->
                        val newTokens = (balanceBlockchainModelResponse.tokens ?: emptyList())
                            .filter {
                                cryptoWallets
                                    ?.firstOrNull { it.id == balanceWalletModelResponse.id }
                                    ?.blockchains
                                    ?.firstOrNull { it.id == balanceBlockchainModelResponse.id }
                                    ?.tokens
                                    ?.contains(it.token) ?: false
                            }

                        balanceBlockchainModelResponse.tokens = newTokens
                    }
                }

                balanceResponse.forEach {
                    it.blockchains.forEach {
                        coinIds.add(it.coin.referenceId)

                        if (it.tokens != null && it.tokens!!.isNotEmpty()) {
                            tokens.add(
                                PlatformTokensSearchModel(
                                    it.id,
                                    it.tokens!!
                                        .map { it.token.tokenAddress }
                                )
                            )
                        }

                        //CHECK FOR NEW TOKENS

                        val newTokens = (it.tokens ?: emptyList()).map { it.token }

                        if (newTokens.isNotEmpty()) {
                            cryptoWalletsRepository.addPlatformTokens(
                                it.id,
                                newTokens
                            )
                        }

                    }
                }

                coinRateRepository.observeCoinsAndTokens(coinIds, tokens)

                balanceResponseSubject.onNext(Optional.ofNullable(balanceResponse))
            }
            .flatMapCompletable {
                Completable.complete()
            }
    }

    override fun refresh(): Completable {
        return updateBalance()
    }

    override fun getWalletBalancesObservable(): Observable<BalanceResponse> {
        return balanceResponseSubject
            .filter { it.isPresent }
            .map { it.get() }
    }
}