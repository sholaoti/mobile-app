package network.etna.etnawallet.ui.login.pincode.createpincode

import network.etna.etnawallet.ui.login.pincode.PinCodeFragmentState
import network.etna.etnawallet.ui.login.pincode.PinCodeViewModel

class CreatePinCodeViewModel: PinCodeViewModel() {

    override fun pinCodeEntered(pinCode: String) {
        fragmentState.onNext(PinCodeFragmentState.DONE)
    }

}