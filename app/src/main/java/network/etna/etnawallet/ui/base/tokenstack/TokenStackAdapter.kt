package network.etna.etnawallet.ui.base.tokenstack

import android.view.View
import android.widget.FrameLayout
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import network.etna.etnawallet.repository.wallets.etnawallet.BlockchainAsset

typealias TokenSelectListener = (BlockchainAsset) -> Unit

class TokenStackAdapter(
    private val dataObservable: Observable<List<BlockchainAsset>>,
    private val tokenSelectListener: TokenSelectListener
    ) {

    private var mFrame: FrameLayout? = null

    private var assetViews: HashMap<String, AssetViewHolder> = HashMap()

    var count: Int = 0
        private set(value) {
            field = value
            mFrame?.invalidate()
        }

    private fun createViewHolder(): AssetViewHolder {
        return AssetViewHolder(mFrame!!)
    }

    fun getViewsObservable(): Observable<List<View>> {
        return dataObservable
            .observeOn(AndroidSchedulers.mainThread())
            .map { assets ->
                count = assets.size
                assets.map { blockchainAsset ->
                    var viewHolder = assetViews[blockchainAsset.id]

                    if (viewHolder == null) {
                        viewHolder = createViewHolder()
                        assetViews[blockchainAsset.id] = viewHolder
                    }
                    viewHolder.bind(blockchainAsset)

                    viewHolder.binding.root.setOnClickListener {
                        tokenSelectListener(blockchainAsset)
                    }

                    viewHolder.binding.root
                }
            }
    }

    internal fun setAdapterParams(tokenStackLayout: TokenStackLayout) {
        mFrame = tokenStackLayout.frame
    }
}
