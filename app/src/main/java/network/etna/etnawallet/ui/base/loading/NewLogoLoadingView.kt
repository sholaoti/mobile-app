package network.etna.etnawallet.ui.base.loading

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout
import androidx.core.animation.doOnCancel
import network.etna.etnawallet.databinding.LayoutNewLoadingBinding

class NewLogoLoadingView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    companion object {
        private const val DURATION = 500L
        private const val SCALE_FACTOR = 0.8F
    }

    private var _binding: LayoutNewLoadingBinding? = null
    val binding get() = _binding!!

    private var isAnimating: Boolean = false

    init {
        _binding = LayoutNewLoadingBinding.inflate(LayoutInflater.from(context), this, true)

        viewTreeObserver.addOnGlobalLayoutListener {
            startAnimation()
        }
    }

    private fun startAnimation() {
        if (isAnimating) {
            return
        }
        isAnimating = true

        val logoAnimator1 = ObjectAnimator
            .ofFloat(binding.loadingLogo, View.SCALE_X,
                SCALE_FACTOR,
                1F
            ).apply {
                interpolator = LinearInterpolator()
                duration = DURATION
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
            }

        val logoAnimator2 = ObjectAnimator
            .ofFloat(binding.loadingLogo, View.SCALE_Y,
                SCALE_FACTOR,
                1F
            ).apply {
                interpolator = LinearInterpolator()
                duration = DURATION
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
            }

        logoAnimator1.doOnCancel {
            it.removeAllListeners()
        }

        logoAnimator2.doOnCancel {
            it.removeAllListeners()
        }

        logoAnimator1.start()
        logoAnimator2.start()
    }
}