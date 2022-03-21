package network.etna.etnawallet.ui.helpers.recycler

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import android.util.TypedValue

import android.util.DisplayMetrics
import androidx.recyclerview.widget.RecyclerView.ItemDecoration


class BasicRecyclerViewMargin(
    private val margin: Int)
    : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.right = margin
        outRect.bottom = margin
        outRect.top = margin
        outRect.left = margin
    }
}

class ListPaddingDecoration(space: Int, context: Context) : ItemDecoration() {
    private val mPadding: Int

    init {
        val metrics: DisplayMetrics = context.resources.displayMetrics
        mPadding = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            space.toFloat(),
            metrics
        ).toInt()
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.left = mPadding
        outRect.right = mPadding
        outRect.top = mPadding / 2
        outRect.bottom = mPadding / 2
    }


}

class HorizontalListPaddingDecoration(space: Int, context: Context) : ItemDecoration() {
    private val mPadding: Int

    init {
        val metrics: DisplayMetrics = context.resources.displayMetrics
        mPadding = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            space.toFloat(),
            metrics
        ).toInt()
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.left = mPadding / 2
        outRect.right = mPadding / 2
        outRect.top = 0
        outRect.bottom = 0
    }


}

fun RecyclerView.addViewHolderSpacing(space: Int, context: Context) {
    //this.addItemDecoration(BasicRecyclerViewMargin(space))
    this.addItemDecoration(ListPaddingDecoration(space, context))
}

fun RecyclerView.addViewHolderHorizontalSpacing(space: Int, context: Context) {
    this.addItemDecoration(HorizontalListPaddingDecoration(space, context))
}