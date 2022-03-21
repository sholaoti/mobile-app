package network.etna.etnawallet.ui.asset.send.sendform

import androidx.lifecycle.ViewModel
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import network.etna.etnawallet.repository.coinrate.CoinRateRepository
import network.etna.etnawallet.repository.wallets.etnawallet.BlockchainAsset
import network.etna.etnawallet.repository.wallets.etnawallet.EtnaWalletsRepository
import network.etna.etnawallet.repository.wallets.walletsend.WalletSendModel
import network.etna.etnawallet.repository.wallets.walletsend.WalletSendRepository
import network.etna.etnawallet.repository.warningservice.WarningService
import network.etna.etnawallet.sdk.api.etna.wssapi.EtnaWSSAPI
import network.etna.etnawallet.sdk.api.etna.wssapi.checkwalletvalid.checkWalletValidByAddress
import network.etna.etnawallet.ui.asset.send.SendScenarioModel
import network.etna.etnawallet.ui.helpers.extensions.formatAmount
import network.etna.etnawallet.ui.helpers.extensions.formatAmountRounded
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*
import java.util.concurrent.TimeUnit

class SendFormViewModel : ViewModel(), KoinComponent {

    private val composite by lazy { CompositeDisposable() }

    private val etnaWSSAPI: EtnaWSSAPI by inject()
    private val coinRateRepository: CoinRateRepository by inject()
    private val scenarioModel: SendScenarioModel by inject()
    private val etnaWalletsRepository: EtnaWalletsRepository by inject()
    private val warningService: WarningService by inject()
    private val walletSendRepository: WalletSendRepository by inject()

    private val amountSubject: BehaviorSubject<String> = BehaviorSubject.createDefault("")
    private val currencySubject: BehaviorSubject<String> = BehaviorSubject.createDefault("")
    private val amountValidSubject: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false)

    private val recipientValidSubject: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false)

    private val preferCurrency: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false)
    private val isRateAvailable: BehaviorSubject<Boolean>

    private var checkRecipientAddressDisposable: Disposable? = null

    private var amountIssueId: String? = null
    private var recipientIssueId: String? = null

    init {
        composite.add(
            Observable.combineLatest(
                amountSubject
                    .skip(1),
                getAssetObservable())
                { amount, asset ->
                    Pair(amount, asset)
                }
                .subscribe {
                handleAmountChange(it.first, it.second)
            }
        )

        composite.add(
            Observable.combineLatest(
                scenarioModel
                    .getAssetIdObservable()
                    .map { it.platform },
                scenarioModel
                    .getRecipientAddressObservable()
                    .filter { it.isPresent }
                    .map { it.get() }
                    .skip(1)
                    .doOnNext {
                        hideRecipientIssue()
                    }
                    .debounce(1000, TimeUnit.MILLISECONDS))
            { platform, address ->
                Pair(platform, address)
            }
                .subscribe {
                    val platform = it.first
                    val address = it.second

                    checkRecipientAddress(platform, address)
                }
        )

        isRateAvailable = BehaviorSubject.createDefault(
            coinRateRepository.getRateModel(scenarioModel.getAssetId()) != null
        )
    }

    private fun clearAmountIssue() {
        if (amountIssueId != null) {
            warningService.cancelIssue(amountIssueId!!)
            amountIssueId = null
        }
    }

    private fun hideRecipientIssue() {
        if (recipientIssueId != null) {
            warningService.cancelIssue(recipientIssueId!!)
            recipientIssueId = null
        }
    }

    private fun showRecipientIssue() {
        recipientValidSubject.onNext(false)

        if (recipientIssueId == null) {
            recipientIssueId = warningService.handleIssue(SendError.WrongAddress, true)
        }
    }

    private fun checkRecipientAddress(platform: String, address: String) {

        checkRecipientAddressDisposable?.dispose()

        hideRecipientIssue()

        checkRecipientAddressDisposable = etnaWSSAPI
            .checkWalletValidByAddress(platform, address)
            .subscribe(
                {
                    if (!it) {
                        showRecipientIssue()
                    } else {
                        hideRecipientIssue()
                        recipientValidSubject.value?.let {
                            if (!it) {
                                recipientValidSubject.onNext(true)
                            }
                        }
                    }
                },
                {
                    showRecipientIssue()
                }
            )
    }

    private fun handleAmountChange(amountStr: String, asset: BlockchainAsset) {
        try {
            val amount = amountStr.toDouble()

            if (amount == 0.0 || amount > asset.balance!!) {
                throw SendError.InvalidAmount
            }

            clearAmountIssue()

            amountValidSubject.onNext(true)

        } catch (e: Exception) {
            if (amountIssueId == null) {
                amountIssueId = warningService.handleIssue(SendError.InvalidAmount, true)
            }
            amountValidSubject.onNext(false)
        }
    }

    fun amountTextChanged(value: String) {
        preferCurrency.value?.let {
            if (it) {

                currencySubject.onNext(value)

                val rateModel = coinRateRepository
                    .getRateModel(scenarioModel.getAssetId())

                if (rateModel != null) {
                    try {
                        val currencyAmount = value.toDouble()
                        val amount = currencyAmount / rateModel.rate
                        amountSubject.onNext(amount.toString())
                    } catch (e: Exception) {
                        amountSubject.onNext("")
                    }

                }

            } else {
                if (value != amountSubject.value) {
                    amountSubject.onNext(value)

                    coinRateRepository.getRateModel(scenarioModel.getAssetId())?.let {
                        try {
                            val amount = value.toDouble()
                            val currencyAmount = amount * it.rate
                            currencySubject.onNext(currencyAmount.toString())
                        } catch (e: Exception) {
                            currencySubject.onNext("")
                        }
                    }
                }
            }
        }
    }

    fun recipientTextChanged(value: String) {
        scenarioModel.updateRecipientAddress(value)
    }

    fun getAmountValidObservable(): Observable<Boolean> {
        return amountValidSubject
            .skip(1)
    }

    fun getRecipientValidObservable(): Observable<Boolean> {
        return recipientValidSubject
            .skip(1)
    }

    fun getSendAvailableObservable(): Observable<Boolean> {
        return Observable.combineLatest(
            getAmountValidObservable(),
            getRecipientValidObservable()
            ) { amountValid, addressValid ->
                amountValid && addressValid
            }
    }

    fun getAssetObservable(): Observable<BlockchainAsset> {
        return scenarioModel
            .getAssetIdObservable()
            .switchMap {
                etnaWalletsRepository
                    .getAssetObservable(it)
            }
    }

    fun getRecipientAddressObservable(): Observable<Optional<String>> {
        return scenarioModel.getRecipientAddressObservable()
    }

    override fun onCleared() {
        super.onCleared()
        composite.dispose()

        clearAmountIssue()
        hideRecipientIssue()
    }

    fun allFundsPressed(): Single<String> {
        return getAssetObservable()
            .take(1)
            .singleOrError()
            .map {
                it.balance?.formatAmount()?.trim() ?: ""
            }
            .doOnSuccess {
                amountTextChanged(it)
            }
    }

    fun sendPressed(): Completable {
        try {

            val recipientAddress =
                scenarioModel.getRecipientAddress() //"muZvLo6swWDB5JL4KcEWQcR3jVRV6QcFtY"

            val amount = amountSubject.value!!

            val assetId = scenarioModel.getAssetId()

            val sendModel = if (assetId.isCurrency)
                WalletSendModel.SendCurrency(
                    assetId.platform,
                    recipientAddress,
                    amount
                )
            else
                WalletSendModel.SendERC20(
                    assetId.platform,
                    recipientAddress,
                    amount,
                    assetId.assetId
                )

            return walletSendRepository
                .sendTransaction(sendModel)

        } catch (e: Exception) {
            return Completable.error(SendError.InvalidData)
        }
    }

    fun isRateAvailableObservable(): Observable<Boolean> {
        return isRateAvailable
    }

    fun isPreferCurrencyObservable(): Observable<Boolean> {
        return preferCurrency
    }

    fun changeCurrencyPreference(): String {
        var returnValue = ""

        preferCurrency.value?.let {
            val newValue = !it

            returnValue = if (newValue) {
                currencySubject.value?.toDouble()?.formatAmountRounded(false, 2) ?: ""

            } else {
                amountSubject.value?.toDouble()?.formatAmountRounded(false, 5) ?: ""
            }

            preferCurrency.onNext(newValue)
        }
        return returnValue.replace(",", "")
    }

    fun getTokenCurrencyNameObservable(): Observable<String> {
        return Observable.combineLatest(
            getAssetObservable()
                .map {
                    it.name
                },
            coinRateRepository
                .getActiveCurrencyObservable()
                .map {
                    it.name
                },
            preferCurrency)
            { assetName, currencyName, preferCurrency ->
                if (preferCurrency) {
                    currencyName
                } else {
                    assetName
                }
            }
    }

    fun getAdditionalAmountObservable(): Observable<String> {
        return Observable.combineLatest(
            amountSubject,
            currencySubject,
            preferCurrency,
            getAssetObservable()
                .map {
                    it.name
                },
            coinRateRepository
                .getActiveCurrencyObservable()
                .map {
                    it.name
                })
            { amount, currency, preferCurrency, assetName, currencyName ->
                if (!preferCurrency) {
                    if (currency.isEmpty()) {
                        ""
                    } else {
                        val formatted = currency.toDouble().formatAmountRounded(false, 2)
                        "$formatted $currencyName"
                    }
                } else {
                    if (amount.isEmpty()) {
                        ""
                    } else {
                        val formatted = amount.toDouble().formatAmountRounded(false, 5)
                        "$formatted $assetName"
                    }
                }
            }
    }
}