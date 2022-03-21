package network.etna.etnawallet.ui.walletplatform.searchtoken

import android.content.res.Resources
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import network.etna.etnawallet.R
import network.etna.etnawallet.repository.blockchainnetworks.BlockchainNetworksRepository
import network.etna.etnawallet.repository.wallets.cryptowallet.CryptoWalletsRepository
import network.etna.etnawallet.repository.wallets.cryptowallet.model.ERCToken
import network.etna.etnawallet.repository.wallets.cryptowallet.model.ERCTokenType
import network.etna.etnawallet.sdk.api.ImageProvider
import network.etna.etnawallet.sdk.api.etna.wssapi.EtnaWSSAPI
import network.etna.etnawallet.sdk.api.etna.wssapi.gettoken.getTokenData
import network.etna.etnawallet.ui.asset.AssetScenarioModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit

class WalletPlatformSearchTokenViewModelInput(
    val address: String,
    val nftId: String?
)

abstract class WalletPlatformSearchTokenViewModelInterface: ViewModel() {
    abstract fun getStateObservable(): Observable<SearchTokenViewState>
    abstract fun addToken()
}

class WalletPlatformSearchTokenViewModel(
    private val resources: Resources,
    private val input: WalletPlatformSearchTokenViewModelInput
) : WalletPlatformSearchTokenViewModelInterface(), KoinComponent {

    private val scenarioModel: AssetScenarioModel by inject()
    private val etnaWSSAPI: EtnaWSSAPI by inject()
    private val blockchainNetworksRepository: BlockchainNetworksRepository by inject()
    private val cryptoWalletsRepository: CryptoWalletsRepository by inject()

    private var tokenData: ERCToken.ERC20? = null

    private val composite by lazy { CompositeDisposable() }

    private val stateSubject: BehaviorSubject<SearchTokenViewState> = BehaviorSubject.createDefault(SearchTokenViewState.Searching)

    init {
        composite.add(
            Single.zip(
                etnaWSSAPI
                    .getTokenData(scenarioModel.platform, input.address, null)
                    .map {
                        it as ERCToken.ERC20
                    }
                    .doOnSuccess {
                        tokenData = it
                    },
                getPlatformNameObservable()
                    .take(1)
                    .singleOrError(),
                Single.just(true).delay(2000, TimeUnit.MILLISECONDS)
            )
            { tokenData, platformName, _ ->
                Pair(tokenData, platformName)
            }
            .subscribe(
                { pair ->
                    val tokenData = pair.first
                    val platformName = pair.second

                    val tokenTypeName = resources.getString(tokenData.tokenTypeResourceId())

                    stateSubject.onNext(SearchTokenViewState.Found(
                        { imageSize ->
                            ImageProvider.getTokenURL(input.address, scenarioModel.platform, imageSize)
                        },
                        resources.getString(R.string.fragment_search_token_tokeninfo_title, tokenData.name, tokenData.symbol),
                        resources.getString(R.string.fragment_search_token_tokeninfo_subtitle, tokenTypeName, platformName)
                    ))

                },
                {
                    stateSubject.onNext(SearchTokenViewState.Failed)
                }
            )
        )
    }

    override fun getStateObservable(): Observable<SearchTokenViewState> {
        return stateSubject
    }

    private fun getPlatformNameObservable(): Observable<String> {
        return blockchainNetworksRepository
            .getPlatformObservable(scenarioModel.platform)
            .map {
                it.name
            }
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun addToken() {
        tokenData?.let {
            cryptoWalletsRepository.addPlatformToken(
                scenarioModel.platform,
                it
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        composite.dispose()
    }
}

fun ERCToken.ERC20.tokenTypeResourceId(): Int {
    return when (this.type) {
        ERCTokenType.erc20 -> R.string.token_type_erc20
        else -> R.string.token_type_unknown
    }
}
