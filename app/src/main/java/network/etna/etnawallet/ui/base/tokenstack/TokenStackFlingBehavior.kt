package network.etna.etnawallet.ui.base.tokenstack

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout

class TokenStackFlingBehavior(context: Context, attrs: AttributeSet): AppBarLayout.Behavior(context, attrs) {

    var state: AppBarState = AppBarState.IDLE

    override fun onNestedFling(
        coordinatorLayout: CoordinatorLayout,
        child: AppBarLayout,
        target: View,
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean
    ): Boolean {
        if (velocityY > 0) {
            child.setExpanded(false, true)
        }
        if (velocityY < 0 && state == AppBarState.IDLE) {
            child.setExpanded(true, true)
        }
        return super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, true)
    }

}