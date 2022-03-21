package network.etna.etnawallet.ui.base.tokenstack

import android.annotation.SuppressLint
import android.util.Log
import android.widget.FrameLayout
import network.etna.etnawallet.R
import network.etna.etnawallet.ui.helpers.extensions.dpToPx

@SuppressLint("ViewConstructor")
internal class TokenStackFrameLayout(private val parent: TokenStackLayout) : FrameLayout(parent.context) {

    private val tokenHeight: Int by lazy {
        parent.context.resources.getDimension(R.dimen.token_view_height).toInt()
    }

    override fun onMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int
    ) {
        var heightMeasureSpecs = heightMeasureSpec
        val mAdapter = parent.adapter
        if (mAdapter != null) {
            val height = tokenHeight * mAdapter.count + tokenHeight + 1

            heightMeasureSpecs = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpecs)
    }
}