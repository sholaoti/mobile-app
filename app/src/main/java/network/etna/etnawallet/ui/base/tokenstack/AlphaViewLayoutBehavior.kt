package network.etna.etnawallet.ui.base.tokenstack

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import network.etna.etnawallet.R

class AlphaViewLayoutBehavior(
    context: Context,
    attrs: AttributeSet?
) : CoordinatorLayout.Behavior<View>(context, attrs) {

    private val bottomSheetTopMargin: Float by lazy {
        context.resources.getDimension(R.dimen.general_fragment_bottom_sheet_expanded_top_margin)
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

        try {
            val anchorView = child.findViewById<View>(R.id.differenceTextView)
            val maxY = anchorView.y + anchorView.height - bottomSheetTopMargin

            val overlap = dependency.y - bottomSheetTopMargin

            var diff = overlap / maxY

            if(diff < 0) {
                diff = 0f
            } else if (diff > 1) {
                diff = 1f
            }

            child.alpha = diff

        } catch(e: Exception) {}

        return false
    }
}