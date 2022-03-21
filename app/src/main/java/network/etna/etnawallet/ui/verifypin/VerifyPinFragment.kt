package network.etna.etnawallet.ui.verifypin

import network.etna.etnawallet.ui.login.pincode.checkpincode.CheckPinCodeFragment

class VerifyPinFragment: CheckPinCodeFragment() {

    override fun pinCodeSuccess() {
        activity?.finish()
    }
}