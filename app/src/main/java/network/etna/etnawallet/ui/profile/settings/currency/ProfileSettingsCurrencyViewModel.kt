package network.etna.etnawallet.ui.profile.settings.currency

import androidx.lifecycle.ViewModel
import network.etna.etnawallet.repository.coinrate.CoinRateRepository
import network.etna.etnawallet.sdk.network.Currency
import io.reactivex.Observable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CurrencyListItem(
    val currency: Currency,
    val isActive: Boolean
)

class ProfileSettingsCurrencyViewModel: ViewModel(), KoinComponent {

    private val repository: CoinRateRepository by inject()

    fun getCurrenciesObservable(): Observable<List<CurrencyListItem>> {
        return Observable.combineLatest(
            repository.getCurrenciesObservable(),
            repository.getActiveCurrencyObservable())
            { currencies, activeCurrency ->
                currencies.map {
                    CurrencyListItem(it, it == activeCurrency)
                }
            }
    }

    fun setActiveCurrency(currency: Currency) {
        repository.setActiveCurrency(currency)
    }

}