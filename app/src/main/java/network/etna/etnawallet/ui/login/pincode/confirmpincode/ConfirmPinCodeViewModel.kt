package network.etna.etnawallet.ui.login.pincode.confirmpincode

import network.etna.etnawallet.repository.pincode.PinCodeRepository
import network.etna.etnawallet.ui.login.pincode.PinCodeFragmentState
import network.etna.etnawallet.ui.login.pincode.PinCodeViewModel
import org.koin.core.component.inject

class ConfirmPinCodeViewModel(
    private val oldPinCode: String
): PinCodeViewModel() {

    private val pinCodeRepository: PinCodeRepository by inject()

    override fun pinCodeEntered(pinCode: String) {
        if (pinCode == oldPinCode) {
            pinCodeRepository.updatePinCode(pinCode)
            pinCodeRepository.setPinCodeVerified()
            fragmentState.onNext(PinCodeFragmentState.SUCCESS)
        } else {
            fragmentState.onNext(PinCodeFragmentState.ERROR)
        }
    }
}