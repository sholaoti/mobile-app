package network.etna.etnawallet.ui.login.pincode

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.motion.widget.MotionLayout
import network.etna.etnawallet.R
import network.etna.etnawallet.databinding.LayoutPinCodeDotBinding

enum class PinCodeDotViewState {
    NORMAL, PRESSED, SUCCESS
}

class PinCodeDotView: MotionLayout {

    private var _binding: LayoutPinCodeDotBinding? = null
    val binding get() = _binding!!

    var currentState: PinCodeDotViewState = PinCodeDotViewState.NORMAL
        private set

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(
        context: Context,
        attrs: AttributeSet
    ) : super(context, attrs) {
        init()
    }

    constructor(
        context: Context,
        attrs: AttributeSet,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        _binding = LayoutPinCodeDotBinding.inflate(LayoutInflater.from(context), this, true)
    }

    fun changeState(state: PinCodeDotViewState) {
        if (state == currentState) {
            return
        }
        currentState = state
        when (state) {
            PinCodeDotViewState.NORMAL ->
                binding.motionLayout.transitionToState(R.id.normal)
            PinCodeDotViewState.PRESSED ->
                binding.motionLayout.transitionToState(R.id.pressed)
            PinCodeDotViewState.SUCCESS ->
                binding.motionLayout.transitionToState(R.id.success)
        }
    }
}