package network.etna.etnawallet.ui.login.biometry

import androidx.lifecycle.ViewModel
import network.etna.etnawallet.repository.biometry.BiometryRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ActivateBiometryViewModel: ViewModel(), KoinComponent {

    private val biometryRepository: BiometryRepository by inject()

    fun skipAction() {
        biometryRepository.skipBiometry()
    }

    fun enrollAction() {
        biometryRepository.enrollBiometry()
    }
}