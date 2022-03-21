package network.etna.etnawallet.ui.base.loading

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout
import androidx.core.animation.doOnCancel
import androidx.core.animation.doOnEnd
import network.etna.etnawallet.databinding.LayoutBasicLoadingBinding

class LogoLoadingView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    companion object {
        private const val DURATION = 1500L
    }

    private var _binding: LayoutBasicLoadingBinding? = null
    val binding get() = _binding!!

    private var isAnimating: Boolean = false

    init {
        _binding = LayoutBasicLoadingBinding.inflate(LayoutInflater.from(context), this, true)

        viewTreeObserver.addOnGlobalLayoutListener {
            startAnimation()
        }
    }

    private fun startAnimation() {
        if (isAnimating) {
            return
        }
        isAnimating = true

        val logo2Animator = ObjectAnimator
            .ofFloat(binding.loadingLogo2, View.X,
                binding.loadingLogo2.x,
                binding.loadingLogo3.x
                        + binding.loadingLogo3.measuredWidth
                        - binding.loadingLogo2.measuredWidth,
                binding.loadingLogo2.x
            ).apply {
                interpolator = LinearInterpolator()
                duration = DURATION / 2
                startDelay = DURATION / 4
            }

        val logo1Animator = ObjectAnimator
            .ofFloat(binding.loadingLogo1, View.X,
                binding.loadingLogo1.x,
                binding.loadingLogo3.x
                        + binding.loadingLogo3.measuredWidth
                        - binding.loadingLogo1.measuredWidth,
                binding.loadingLogo1.x
            ).apply {
                interpolator = LinearInterpolator()
                duration = DURATION
            }

        logo1Animator.doOnCancel {
            it.removeAllListeners()
        }

        logo1Animator.doOnEnd {
            logo1Animator?.start()
            logo2Animator?.start()
        }

        logo1Animator.start()
        logo2Animator.start()
    }
}