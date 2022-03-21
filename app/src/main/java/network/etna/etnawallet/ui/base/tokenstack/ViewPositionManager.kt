package network.etna.etnawallet.ui.base.tokenstack

import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.DecelerateInterpolator

class ViewPositionManager {

    private val animators: MutableMap<View, ObjectAnimator> = HashMap()
    private val positions: MutableMap<View, Float> = HashMap()

    private fun getPositionForView(view: View): Float {
        return positions[view] ?: 0F
    }

    fun moveViewTo(view: View, y: Float, animate: Boolean) {
        if (animate) {
            if (y != getPositionForView(view)) {
                animators[view]?.cancel()

                val animator = ObjectAnimator.ofFloat(view, View.Y, view.y, y)
                animators[view] = animator
                positions[view] = y
                animator.duration = 500
                animator.interpolator = DecelerateInterpolator(2.toFloat())
                animator.start()
            }
        } else {
            if (y != getPositionForView(view)) {
                animators[view]?.cancel()

                positions[view] = y
                view.y = y
            }
        }
    }
}