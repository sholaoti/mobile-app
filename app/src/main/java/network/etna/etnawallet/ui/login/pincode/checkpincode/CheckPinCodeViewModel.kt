package network.etna.etnawallet.ui.login.pincode.checkpincode

import network.etna.etnawallet.repository.biometry.BiometryRepository
import network.etna.etnawallet.repository.biometry.BiometryState
import network.etna.etnawallet.repository.pincode.PinCodeRepository
import network.etna.etnawallet.repository.pincode.PinCodeRepositoryError
import network.etna.etnawallet.repository.warningservice.WarningService
import network.etna.etnawallet.ui.login.pincode.PinCodeFragmentState
import network.etna.etnawallet.ui.login.pincode.PinCodeViewModel
import org.koin.core.component.inject

class CheckPinCodeViewModel: PinCodeViewModel() {

    private val pinCodeRepository: PinCodeRepository by inject()
    private val biometryRepository: BiometryRepository by inject()
    private val warningService: WarningService by inject()

    private var issueId: String? = null

    override fun pinCodeEntered(pinCode: String) {
        if (pinCode == pinCodeRepository.getPinCode()) {
            pinCodeRepository.setPinCodeVerified()
            fragmentState.onNext(PinCodeFragmentState.SUCCESS)
        } else {
            fragmentState.onNext(PinCodeFragmentState.ERROR)
            issueId = warningService.handleIssue(PinCodeRepositoryError.WrongPin)
        }
    }

    private fun cancelWarning() {
        if (issueId != null) {
            warningService.cancelIssue(issueId!!)
            issueId = null
        }
    }

    override fun digitPressedEvent() {
        cancelWarning()
    }

    override fun clearPressed() {
        super.clearPressed()
        cancelWarning()
    }

    fun biometryPassed() {
        pinCode.onNext(pinCodeRepository.getPinCode())
        pinCodeEntered(pinCodeRepository.getPinCode())
    }

    fun isBiometryEnrolled(): Boolean {
        return biometryRepository.getBiometryState() == BiometryState.ENROLLED
    }
}