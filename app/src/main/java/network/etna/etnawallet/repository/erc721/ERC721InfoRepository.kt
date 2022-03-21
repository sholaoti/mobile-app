package network.etna.etnawallet.repository.erc721

import io.reactivex.Single
import network.etna.etnawallet.repository.wallets.cryptowallet.model.ERCToken

interface ERC721InfoRepository {
    fun getERC721TokenInfo(token: ERCToken.ERC721): Single<ERC721TokenInfo>
}