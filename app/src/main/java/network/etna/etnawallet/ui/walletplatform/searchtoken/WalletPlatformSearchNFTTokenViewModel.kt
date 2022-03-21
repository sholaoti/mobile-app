package network.etna.etnawallet.ui.walletplatform.searchtoken

import android.content.res.Resources
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import network.etna.etnawallet.R
import network.etna.etnawallet.repository.blockchainnetworks.BlockchainNetworksRepository
import network.etna.etnawallet.repository.erc721.ERC721InfoRepository
import network.etna.etnawallet.repository.erc721.ERC721TokenInfo
import network.etna.etnawallet.repository.wallets.cryptowallet.CryptoWalletsRepository
import network.etna.etnawallet.repository.wallets.cryptowallet.model.ERCToken
import network.etna.etnawallet.sdk.api.ImageProvider
import network.etna.etnawallet.sdk.api.etna.wssapi.EtnaWSSAPI
import network.etna.etnawallet.sdk.api.etna.wssapi.gettoken.getTokenData
import network.etna.etnawallet.sdk.api.httpservice.HttpService
import network.etna.etnawallet.ui.asset.AssetScenarioModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit

class WalletPlatformSearchNFTTokenViewModel(
    private val resources: Resources,
    private val input: WalletPlatformSearchTokenViewModelInput
) : WalletPlatformSearchTokenViewModelInterface(), KoinComponent {

    private val scenarioModel: AssetScenarioModel by inject()
    private val etnaWSSAPI: EtnaWSSAPI by inject()
    private val erc721InfoRepository: ERC721InfoRepository by inject()
    private val cryptoWalletsRepository: CryptoWalletsRepository by inject()

    private val composite by lazy { CompositeDisposable() }

    private var tokenData: ERCToken.ERC721? = null

    private val stateSubject: BehaviorSubject<SearchTokenViewState> = BehaviorSubject.createDefault(SearchTokenViewState.Searching)

    init {
        composite.add(
            Single.zip(
                etnaWSSAPI
                    .getTokenData(scenarioModel.platform, input.address, input.nftId)
                    .flatMap { ercToken ->
                        erc721InfoRepository
                            .getERC721TokenInfo(ercToken as ERCToken.ERC721)
                            .map { Pair(ercToken, it) }
                    },
                Single.just(true).delay(2000, TimeUnit.MILLISECONDS)
            )
            { tokenPair, _ ->
                tokenPair
            }
            .subscribe(
                { tokenPair ->

                    val token = tokenPair.first
                    val tokenInfo = tokenPair.second

                    stateSubject.onNext(SearchTokenViewState.Found(
                        if (tokenInfo.image != null) { _ -> tokenInfo.image } else null,
                        tokenInfo.name ?: "",
                        resources.getString(R.string.fragment_search_nft_token_tokeninfo_subtitle, token.tokenId)
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

    override fun addToken() {
        tokenData?.let {
            cryptoWalletsRepository.addPlatformToken(
                scenarioModel.platform,
                it
            )
        }
    }
}