package network.etna.etnawallet.ui.base.tokenstack

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior

class TokenStackBottomSheetBehavior(context: Context, attrs: AttributeSet) :
    BottomSheetBehavior<View>(context, attrs) {

    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
        if (target is TokenStackLayout) {
            target.onVerticalNestedPreScroll(dy)
        }
    }
}