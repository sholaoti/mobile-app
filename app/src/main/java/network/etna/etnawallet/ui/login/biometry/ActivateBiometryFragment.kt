package network.etna.etnawallet.ui.login.biometry

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.biometric.BiometricManager.Authenticators.*
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.kizitonwose.android.disposebag.disposedWith
import network.etna.etnawallet.R
import network.etna.etnawallet.databinding.FragmentActivateBiometryBinding
import network.etna.etnawallet.ui.base.BaseFragment
import network.etna.etnawallet.ui.login.LoginStateViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ActivateBiometryFragment :
    BaseFragment<FragmentActivateBiometryBinding>(
        FragmentActivateBiometryBinding::inflate,
        null,
        R.menu.fragment_activate_biometry_menu
    ) {

    private val viewModel: ActivateBiometryViewModel by viewModel()
    private val loginStateViewModel: LoginStateViewModel by viewModel()

    override fun menuItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_skip -> {
                viewModel.skipAction()
                goToNextScreen()
                true
            } else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.activateBiometryButton.setOnClickListener {
            showBiometryPrompt()
        }
    }

    private fun showBiometryPrompt() {

        val executor = ContextCompat.getMainExecutor(requireContext())
        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult) {
                    viewModel.enrollAction()
                    goToNextScreen()
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(resources.getString(R.string.biometry_title))
            .setSubtitle(resources.getString(R.string.biometry_subtitle))
            .setAllowedAuthenticators(BIOMETRIC_STRONG or BIOMETRIC_WEAK or DEVICE_CREDENTIAL)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    fun goToNextScreen() {
        loginStateViewModel
            .goToNextState(this)
            .disposedWith(viewLifecycleOwner)
    }
}