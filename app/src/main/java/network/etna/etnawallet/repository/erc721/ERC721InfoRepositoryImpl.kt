package network.etna.etnawallet.repository.erc721

import io.reactivex.Single
import network.etna.etnawallet.repository.wallets.cryptowallet.model.ERCToken
import network.etna.etnawallet.sdk.api.httpservice.HttpService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ERC721InfoRepositoryImpl: ERC721InfoRepository, KoinComponent {

    private val httpService: HttpService by inject()

    private val cache: Map<String, ERC721TokenInfo> = HashMap()

    override fun getERC721TokenInfo(token: ERCToken.ERC721): Single<ERC721TokenInfo> {

        val cachedItem = cache[token.uniqueId]
        return if (cachedItem != null) {
            Single.just(cachedItem)
        } else {
            httpService.request(token.tokenURI, ERC721TokenInfo::class.java) {
                registerTypeAdapter(
                    ERC721TokenInfo::class.java,
                    ERC721TokenInfoJsonDeserializer()
                )
            }
        }

    }

}
