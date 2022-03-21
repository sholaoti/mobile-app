package network.etna.etnawallet.repository.wallets.etnawallet

import android.annotation.SuppressLint
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import network.etna.etnawallet.repository.blockchainnetworks.BlockchainNetworksRepository
import network.etna.etnawallet.repository.coinrate.*
import network.etna.etnawallet.repository.wallets.CurrencyAmount
import network.etna.etnawallet.sdk.api.ImageProvider
import network.etna.etnawallet.repository.wallets.cryptowallet.CryptoWalletsRepository
import network.etna.etnawallet.repository.wallets.walletbalances.WalletBalancesRepository
import network.etna.etnawallet.sdk.api.etna.wssapi.balance.BalanceCoinModelResponse
import network.etna.etnawallet.sdk.api.etna.wssapi.balance.BalanceTokenModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.math.BigDecimal
import java.util.*

@SuppressLint("CheckResult")
class EtnaWalletsRepositoryImpl : EtnaWalletsRepository, KoinComponent {

    private val coinRateRepository: CoinRateRepository by inject()
    private val blockchainNetworksRepository: BlockchainNetworksRepository by inject()
    private val cryptoWalletsRepository: CryptoWalletsRepository by inject()
    private val walletBalancesRepository: WalletBalancesRepository by inject()

    private val etnaWalletsSubject: BehaviorSubject<Optional<List<EtnaWalletModel>>> =
        BehaviorSubject.createDefault(Optional.empty())

    override fun getAssetsObservable(): Observable<List<BlockchainAsset>> {
        return getActiveWalletObservable()
            .map { wallet ->
                wallet.blockchains
                    .fold(emptyList()) { acc, value -> acc + value.getBlockchainAssets() }
            }
    }

    override fun getAssetObservable(assetId: AssetIdHolder): Observable<BlockchainAsset> {
        return getActiveWalletObservable()
            .map { wallet ->
                Optional.ofNullable(wallet.getAsset(assetId))
            }
            .filter { it.isPresent }
            .map { it.get() }
    }

    override fun getAsset(assetId: AssetIdHolder): BlockchainAsset? {
        return try {
            val asset = etnaWalletsSubject.value!!.get().first { it.isActive }.getAsset(assetId)
            asset
        } catch (e: Exception) {
            null
        }
    }

    override fun getActiveWalletObservable(): Observable<EtnaWalletModel> {
        return getWalletsObservable()
            .map {
                Optional.ofNullable(it.firstOrNull { it.isActive })
            }
            .filter { it.isPresent }
            .map { it.get() }
    }

    init {

        Observable.combineLatest(
            cryptoWalletsRepository
                .getAvailableWalletsObservable()
                .observeOn(Schedulers.computation()),
            walletBalancesRepository
                .getWalletBalancesObservable()
                .observeOn(Schedulers.computation()),
            coinRateRepository
                .getCoinRatesObservable()
                .observeOn(Schedulers.computation()),
            blockchainNetworksRepository
                .getPlatformsObservable()
                .observeOn(Schedulers.computation()))
            { cryptoWallets, balanceResponse, coinRates, platforms ->

                cryptoWallets.map { cryptoWallet ->
                    val etnaBlockchainModels = balanceResponse
                        .firstOrNull { it.id == cryptoWallet.id }
                        ?.blockchains
                        ?.map { resp ->
                            val platform = platforms.firstOrNull { it.id == resp.id }
                            EtnaBlockchainModel(
                                resp.id,
                                resp.coin.getEtnaCoinModel(platform?.mainNet ?: false, coinRates),
                                resp.tokens?.map { it.getEtnaTokenModel(platform?.id ?: "", platform?.mainNet ?: false, coinRates) } ?: emptyList(),
                                resp.address,
                                platform?.name,
                                platform?.symbol,
                                platform?.mainNet ?: false,
                                resp.weight,
                            )
                        }
                        ?: emptyList()

                    var walletCurrencyBalance: CurrencyAmount? = null
                    var walletCurrency24hChange: CurrencyAmount? = null

                    etnaBlockchainModels
                        .firstNotNullOfOrNull { it.coin.currencyBalance?.currency }
                        ?.let { activeCurrency ->

                            val tokenAmounts = etnaBlockchainModels
                                .map { it.tokens
                                    .mapNotNull { it.currencyBalance }
                                }
                                .fold(emptyList<CurrencyAmount>()) { acc, value -> acc + value }

                            val coinAmounts = etnaBlockchainModels
                                .mapNotNull { it.coin.currencyBalance }

                            walletCurrencyBalance = (tokenAmounts + coinAmounts)
                                .fold(
                                    CurrencyAmount(
                                        BigDecimal.ZERO,
                                        activeCurrency
                                    )
                                ) { acc, value -> acc + value }

                            val tokens24hChange = etnaBlockchainModels
                                .map { it.tokens
                                    .mapNotNull { it.currency24hChange }
                                }
                                .fold(emptyList<CurrencyAmount>()) { acc, value -> acc + value }

                            val coins24hChange = etnaBlockchainModels
                                .mapNotNull { it.coin.currency24hChange }

                            walletCurrency24hChange = (tokens24hChange + coins24hChange)
                                .fold(
                                    CurrencyAmount(
                                        BigDecimal.ZERO,
                                        activeCurrency
                                    )
                                ) { acc, value -> acc + value }
                        }

                    EtnaWalletModel(
                        cryptoWallet.name,
                        cryptoWallet.id,
                        cryptoWallet.isActive,
                        platforms.all { it.mainNet },
                        etnaBlockchainModels,
                        walletCurrencyBalance,
                        walletCurrency24hChange
                    )
                }
            }
            .observeOn(Schedulers.computation())
            .subscribe {
                etnaWalletsSubject.onNext(Optional.of(it))
            }
    }

    override fun getWalletsObservable(): Observable<List<EtnaWalletModel>> {
        return etnaWalletsSubject
            .filter { it.isPresent }
            .map { it.get() }
    }
}

fun BalanceCoinModelResponse.getEtnaCoinModel(
    marketData: Boolean,
    coinRates: List<RateModel>
): EtnaCoinModel {

    var currencyBalance: CurrencyAmount? = null
    var currency24hChange: CurrencyAmount? = null

    val balance = balance?.toDouble()
    var change24h: Double? = null

    if (marketData && balance != null) {
        coinRates.getCoinRateModel(referenceId)?.let { rateModel ->
            currencyBalance = CurrencyAmount(
                BigDecimal(balance) * BigDecimal(rateModel.rate),
                rateModel.currency
            )

            currency24hChange = CurrencyAmount(
                BigDecimal(balance) * BigDecimal(rateModel.rate) * BigDecimal(rateModel.change24h) / BigDecimal(100),
                rateModel.currency
            )

            change24h = rateModel.change24h
        }
    }

    return EtnaCoinModel(
        id,
        referenceId,
        balance,
        change24h,
        currencyBalance,
        currency24hChange
    )
}

fun BalanceTokenModel.getEtnaTokenModel(
    platformId: String,
    marketData: Boolean,
    coinRates: List<RateModel>
): EtnaTokenModel {

    var currencyBalance: CurrencyAmount? = null
    var currency24hChange: CurrencyAmount? = null

    val balance = balance
    var change24h: Double? = null

    if (marketData && balance != null) {
        coinRates.getTokenRateModel(platformId, this.token.tokenAddress)?.let { rateModel ->
            currencyBalance = CurrencyAmount(
                BigDecimal(balance) * BigDecimal(rateModel.rate),
                rateModel.currency
            )

            currency24hChange = CurrencyAmount(
                BigDecimal(balance) * BigDecimal(rateModel.rate) * BigDecimal(rateModel.change24h) / BigDecimal(100),
                rateModel.currency
            )

            change24h = rateModel.change24h
        }
    }


    return EtnaTokenModel(
        token,
        balance,
        change24h,
        currencyBalance,
        currency24hChange
    )
}

fun EtnaBlockchainModel.getBlockchainAssets(): List<BlockchainAsset> {
    return listOf(getBlockchainCoinAsset()) +
            tokens.map {
                val token = it.token
                BlockchainAsset(
                    token.tokenAddress,
                    token.tokenAddress,
                    token.symbol,
                    id,
                    address,
                    name,
                    { size ->
                        ImageProvider.getTokenURL(token.tokenAddress, id, size)
                    },
                    BlockchainAssetType.TOKEN,
                    it.balance,
                    it.change24h,
                    it.currencyBalance,
                    it.currency24hChange
                )
            }
}

fun EtnaBlockchainModel.getBlockchainCoinAsset(): BlockchainAsset {
    return BlockchainAsset(
        id + coin.id,
        coin.referenceId,
        symbol ?: "",
        id,
        address,
        name,
        {
            ImageProvider.getImageURL(coin.referenceId, it)
        },
        BlockchainAssetType.CURRENCY,
        coin.balance,
        coin.change24h,
        coin.currencyBalance,
        coin.currency24hChange
    )
}

fun EtnaWalletModel.getAsset(assetId: AssetIdHolder): BlockchainAsset? {
    return if (assetId.isCurrency) {
        blockchains
            .firstOrNull { it.id == assetId.platform }
            ?.getBlockchainCoinAsset()
    } else {
        blockchains
            .firstOrNull { it.id == assetId.platform }
            ?.getBlockchainAssets()
            ?.firstOrNull { it.id == assetId.assetId }
    }
}