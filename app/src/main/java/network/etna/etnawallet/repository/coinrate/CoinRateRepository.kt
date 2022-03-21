package network.etna.etnawallet.repository.coinrate

import network.etna.etnawallet.sdk.network.Currency
import io.reactivex.Observable
import network.etna.etnawallet.repository.wallets.etnawallet.AssetIdHolder

interface CoinRateRepository {
    fun getCoinRatesObservable(): Observable<List<RateModel>>
    fun getCurrenciesObservable(): Observable<List<Currency>>
    fun getActiveCurrencyObservable(): Observable<Currency>
    fun setActiveCurrency(currency: Currency)
    fun observeCoinsAndTokens(ids: List<String>, newPlatformTokens: List<PlatformTokensSearchModel>)
    fun getRateModel(assetId: AssetIdHolder): RateModel?
}