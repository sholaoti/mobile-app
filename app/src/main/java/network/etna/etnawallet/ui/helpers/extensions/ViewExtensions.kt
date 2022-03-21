package network.etna.etnawallet.ui.helpers.extensions

import android.animation.Animator
import android.animation.ObjectAnimator
import android.view.View
import androidx.core.animation.addListener

inline fun View.shake(shakeDistance: Float = 50F, crossinline onEnd: (animator: Animator) -> Unit = {}) {

    val startX = x

    val animator = ObjectAnimator
        .ofFloat(this, View.X,
            startX,
            startX + shakeDistance,
            startX - shakeDistance,
            startX + shakeDistance / 2,
            startX - shakeDistance / 2,
            startX + shakeDistance / 5,
            startX - shakeDistance / 5,
            startX)

    animator.addListener(onEnd)

    animator.duration = 500

    animator.start()
}

fun View.toggleVisibility(value: Boolean) {
    visibility = if (value) View.VISIBLE else View.INVISIBLE
}

fun View.toggleVisibilityGone(value: Boolean) {
    visibility = if (value) View.VISIBLE else View.GONE
}

fun View.bubbleAnimation(scaleFactor: Float = 1.05F) {
    val scaleXAnimator = ObjectAnimator
        .ofFloat(this, View.SCALE_X,
            1F,
            scaleFactor
        ).apply {
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }

    val scaleYAnimator = ObjectAnimator
        .ofFloat(this, View.SCALE_Y,
            1F,
            scaleFactor
        ).apply {
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }

    scaleXAnimator.start()
    scaleYAnimator.start()
}