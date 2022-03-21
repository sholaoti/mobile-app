package network.etna.etnawallet.ui.general.nftcollections

import androidx.lifecycle.ViewModel
import org.koin.core.component.KoinComponent

class NFTCollectionModel(
    val name: String,
    val platformName: String,
    val assetsCount: Int
)

class NftCollectionsViewModel: ViewModel(), KoinComponent {
}