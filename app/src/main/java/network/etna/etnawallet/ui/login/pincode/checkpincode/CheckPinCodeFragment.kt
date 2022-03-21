package network.etna.etnawallet.ui.login.pincode.checkpincode

import android.os.Bundle
import android.view.View
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.kizitonwose.android.disposebag.disposedWith
import io.reactivex.Observable
import network.etna.etnawallet.R
import network.etna.etnawallet.ui.login.LoginStateViewModel
import network.etna.etnawallet.ui.login.pincode.PinCodeFragment
import network.etna.etnawallet.ui.login.pincode.PinCodeViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

open class CheckPinCodeFragment: PinCodeFragment() {

    private val viewModel: CheckPinCodeViewModel by viewModel()
    private val loginStateViewModel: LoginStateViewModel by viewModel()

    override fun getViewModel(): PinCodeViewModel {
        return viewModel
    }

    override fun getTitleText(): String {
        return resources.getString(R.string.check_pin)
    }

    override fun getSubtitleTextObservable(): Observable<String> {
        return Observable.just("")
    }

    override fun pinCodeSuccess() {
        loginStateViewModel
            .goToNextState(this)
            .disposedWith(viewLifecycleOwner)
    }

    override fun getSecondaryActionResourceId(): Int? {
        return if (viewModel.isBiometryEnrolled()) R.drawable.ic_touch_id else null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.pinCodeButtonsView.binding.buttonSecondary.setOnClickListener {
            showBiometryPrompt()
        }

        if (viewModel.isBiometryEnrolled()) {
            showBiometryPrompt()
        }
    }

    private fun showBiometryPrompt() {

        val executor = ContextCompat.getMainExecutor(requireContext())
        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult) {
                    viewModel.biometryPassed()
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(resources.getString(R.string.biometry_title))
            .setSubtitle(resources.getString(R.string.biometry_subtitle))
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}