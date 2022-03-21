package network.etna.etnawallet.ui.login.pincode

import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import org.koin.core.component.KoinComponent

enum class PinCodeFragmentState {
    NORMAL, SUCCESS, DONE, ERROR
}

abstract class PinCodeViewModel: ViewModel(), KoinComponent {

    protected val fragmentState: PublishSubject<PinCodeFragmentState> = PublishSubject.create()

    protected val pinCode: BehaviorSubject<String> = BehaviorSubject.createDefault("")

    open val pinCodeMaxLength: Int = 4

    fun getPinCode(): String {
        return pinCode.value!!
    }

    open fun digitPressed(digit: String) {
        pinCode.value?.let {
            if (it.length >= pinCodeMaxLength) {
                return
            }
            digitPressedEvent()
            fragmentState.onNext(PinCodeFragmentState.NORMAL)
            val newPinCode = it + digit
            pinCode.onNext(newPinCode)
            if (newPinCode.length == pinCodeMaxLength) {
                pinCodeEntered(newPinCode)
            }
        }
    }

    open fun digitPressedEvent() {}

    open fun clearPressed() {
        clearPin()
        fragmentState.onNext(PinCodeFragmentState.NORMAL)
    }

    fun clearPin() {
        pinCode.onNext("")
    }

    fun getPinCodeLengthObservable(): Observable<Int> {
        return pinCode
            .map { it.length }
    }

    fun getFragmentStateObservable(): Observable<PinCodeFragmentState> {
        return fragmentState
    }

    protected abstract fun pinCodeEntered(pinCode: String)
}