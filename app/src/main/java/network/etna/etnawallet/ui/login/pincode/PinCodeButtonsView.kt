package network.etna.etnawallet.ui.login.pincode

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import network.etna.etnawallet.databinding.LayoutPinCodeButtonsBinding

sealed class PinCodeButtonAction {
    class Button(val digit: String): PinCodeButtonAction()
    object Clear: PinCodeButtonAction()
}

class PinCodeButtonsView: FrameLayout {

    private var _binding: LayoutPinCodeButtonsBinding? = null
    val binding get() = _binding!!

    private val actionSubject: PublishSubject<PinCodeButtonAction> = PublishSubject.create()

    fun getActionObservable(): Observable<PinCodeButtonAction> {
        return actionSubject
    }

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
        _binding = LayoutPinCodeButtonsBinding.inflate(LayoutInflater.from(context), this, true)
        binding.button0.setOnClickListener { actionSubject.onNext(PinCodeButtonAction.Button("0")) }
        binding.button1.setOnClickListener { actionSubject.onNext(PinCodeButtonAction.Button("1")) }
        binding.button2.setOnClickListener { actionSubject.onNext(PinCodeButtonAction.Button("2")) }
        binding.button3.setOnClickListener { actionSubject.onNext(PinCodeButtonAction.Button("3")) }
        binding.button4.setOnClickListener { actionSubject.onNext(PinCodeButtonAction.Button("4")) }
        binding.button5.setOnClickListener { actionSubject.onNext(PinCodeButtonAction.Button("5")) }
        binding.button6.setOnClickListener { actionSubject.onNext(PinCodeButtonAction.Button("6")) }
        binding.button7.setOnClickListener { actionSubject.onNext(PinCodeButtonAction.Button("7")) }
        binding.button8.setOnClickListener { actionSubject.onNext(PinCodeButtonAction.Button("8")) }
        binding.button9.setOnClickListener { actionSubject.onNext(PinCodeButtonAction.Button("9")) }
        binding.buttonClear.setOnClickListener { actionSubject.onNext(PinCodeButtonAction.Clear) }
    }
}