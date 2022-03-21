package network.etna.etnawallet.ui.base.tokenstack

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import network.etna.etnawallet.R
import network.etna.etnawallet.ui.helpers.extensions.dpToPx

class FloatingButtonsLayoutBehavior(
    context: Context,
    attrs: AttributeSet?
) : CoordinatorLayout.Behavior<View>(context, attrs) {

    private val margin: Int by lazy {
        context.dpToPx(16)
    }

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        return dependency.id == R.id.bottomSheet
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        child.y = dependency.y - (child.height) - margin
        return false
    }
}