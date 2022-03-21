package network.etna.etnawallet.ui.asset.assetinfo

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.widget.NestedScrollView
import network.etna.etnawallet.R

class SegmentedNestedScrollView(context: Context, attrs: AttributeSet) : NestedScrollView(context, attrs) {

    private val selectorView: View by lazy {
        findViewById(R.id.segmentedSelectorView)
    }

    override fun onScrollChanged(scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) {
        selectorView.y = scrollY.toFloat()
    }
}