package network.etna.etnawallet.ui.helpers

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewMargin(
    private val margin: Int,
    private val columns: Int)
    : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildLayoutPosition(view)
        outRect.right = margin
        outRect.bottom = margin / 2
        if (position < columns) {
            outRect.top = margin / 2
        }

        if (position % columns == 0) {
            outRect.left = margin
        }
    }
}