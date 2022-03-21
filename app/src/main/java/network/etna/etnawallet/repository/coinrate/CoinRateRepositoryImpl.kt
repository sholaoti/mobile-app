package network.etna.etnawallet.repository.coinrate

import android.util.ArraySet
import io.reactivex.Completable
import network.etna.etnawallet.sdk.api.coingecko.CoinGekoAPI
import network.etna.etnawallet.sdk.network.Currency
import network.etna.etnawallet.repository.storage.DataStorage
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import network.etna.etnawallet.repository.refresh.RefreshInterface
import network.etna.etnawallet.repository.refresh.RefreshRepository
import network.etna.etnawallet.repository.wallets.etnawallet.AssetIdHolder
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class CoinRateRepositoryImpl : KoinComponent, CoinRateRepository, RefreshInterface {

    private val coinGekoAPI: CoinGekoAPI by inject()

    private val dataStorage: DataStorage by inject()

    private val refreshRepository: RefreshRepository by inject()

    companion object {
        private const val ACTIVE_CURRENCY = "ACTIVE_CURRENCY"
    }

    private val activeCurrencySubject: BehaviorSubject<Currency> =
        BehaviorSubject.createDefault(dataStorage.get(Currency::class.java, ACTIVE_CURRENCY) ?: Currency.USD)

    private var getPricesDisposable: Disposable? = null

    private val rateModelsSubject: BehaviorSubject<Optional<List<RateModel>>> =
        BehaviorSubject.createDefault(Optional.empty())

    private var timerDisposable: Disposable? = null

    private val coinIds: MutableSet<String> = ArraySet()
    private val platformTokens: MutableMap<String, MutableList<String>> = mutableMapOf()

    init {
        updatePrices()

//        timerDisposable = Observable
//            .interval(10, TimeUnit.SECONDS)
//            .subscribeOn(Schedulers.computation())
//            .observeOn(Schedulers.computation())
//            .subscribe {
//                updatePrices()
//            }

        refreshRepository.register(this)
    }

    override fun refresh(): Completable {
        return updatePricesCompletable()
    }

    override fun observeCoinsAndTokens(ids: List<String>, newPlatformTokens: List<PlatformTokensSearchModel>) {
        coinIds.addAll(ids)

        newPlatformTokens.forEach {
            if (!platformTokens.containsKey(it.platformId)) {
                platformTokens[it.platformId] = mutableListOf()
            }

            platformTokens[it.platformId]!!.addAll(it.tokens)
        }

        updatePrices()
    }

    private fun updatePricesCompletable(): Completable {
        if (coinIds.isEmpty() && platformTokens.isEmpty()) {
            return Completable.complete()
        }

        val coins = coinIds.joinToString(",")
        val currencies: String = Currency.values().toList().joinToString(",") { it.name }

        val getCoinPricesSingle = coinGekoAPI
            .getPrices(
                coins,
                currencies
            )
            .map { json ->
                val rateModels: MutableList<RateModel> = ArrayList()

                for (key in json.keySet()) {
                    val valuesJson = json.getAsJsonObject(key)

                    for (currency in Currency.values()) {
                        val cur = currency.name.lowercase()
                        if (valuesJson.has(cur) && valuesJson.has("${cur}_24h_change")) {
                            val rate: Double = valuesJson.get(cur).asDouble
                            val change24h: Double = valuesJson.get("${cur}_24h_change").asDouble

                            rateModels.add(RateModel.Coin(
                                key,
                                currency,
                                rate,
                                change24h
                            ))
                        }
                    }
                }

                rateModels.toList()
            }


        val platformTokensSingles = platformTokens
            .map {
                val platformId = it.key
                coinGekoAPI
                    .getTokenPrices(
                        platformId,
                        it.value.joinToString(","),
                        currencies
                    )
                    .map { json ->

                        val rateModels: MutableList<RateModel> = ArrayList()

                        for (tokenAddress in json.keySet()) {
                            val valuesJson = json.getAsJsonObject(tokenAddress)

                            for (currency in Currency.values()) {
                                val cur = currency.name.lowercase()
                                if (valuesJson.has(cur) && valuesJson.has("${cur}_24h_change")) {
                                    val rate: Double = valuesJson.get(cur).asDouble
                                    val change24h: Double =
                                        valuesJson.get("${cur}_24h_change").asDouble
                                    rateModels.add(
                                        RateModel.Token(
                                            platformId,
                                            tokenAddress,
                                            currency,
                                            rate,
                                            change24h
                                        )
                                    )
                                }
                            }
                        }

                        rateModels.toList()
                    }
            }

        return Single.zip(
            platformTokensSingles + listOf(getCoinPricesSingle)
            ) {
                it
                    .mapNotNull { it as? List<RateModel> }
                    .fold(emptyList<RateModel>()) { acc, value -> acc + value }
            }
            .flatMapCompletable {
                rateModelsSubject.onNext(Optional.of(it))
                Completable.complete()
            }
    }

    private fun updatePrices() {
        getPricesDisposable?.dispose()
        getPricesDisposable = updatePricesCompletable().subscribe()
    }

    override fun getCoinRatesObservable(): Observable<List<RateModel>> {
        return Observable.combineLatest(
            rateModelsSubject
                .filter { it.isPresent }
                .map { it.get() },
            getActiveCurrencyObservable())
            { models, activeCurrency ->
                models.filter { it.currency == activeCurrency }
            }
    }

    override fun getCurrenciesObservable(): Observable<List<Currency>> {
        return Observable
            .just(
                Currency.values().toList()
            )
    }

    override fun getActiveCurrencyObservable(): Observable<Currency> {
        return activeCurrencySubject
    }

    override fun setActiveCurrency(currency: Currency) {
        dataStorage.put(currency, ACTIVE_CURRENCY)
        activeCurrencySubject.onNext(currency)
    }

    override fun getRateModel(assetId: AssetIdHolder): RateModel? {
        rateModelsSubject.value?.let {
            if (it.isPresent) {
                val rateModels = it.get()
                return rateModels.getRateModel(assetId)
            }
        }
        return null
    }
}