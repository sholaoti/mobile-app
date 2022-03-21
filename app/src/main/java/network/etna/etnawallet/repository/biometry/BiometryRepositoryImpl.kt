package network.etna.etnawallet.repository.biometry

import android.content.Context
import androidx.biometric.BiometricManager
import network.etna.etnawallet.repository.storage.DataStorage
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BiometryRepositoryImpl(private val context: Context): BiometryRepository, KoinComponent {

    private val dataStorage: DataStorage by inject()

    companion object {
        private const val BIOMETRY_STATE = "BIOMETRY_STATE"
    }

    override fun getBiometryState(): BiometryState {
        var state = dataStorage.get(BiometryState::class.java, BIOMETRY_STATE) ?: BiometryState.NONE

        if (state == BiometryState.NONE && isBiometricReady()) {
            state = BiometryState.ALLOWED
        }

        return state
    }

    override fun skipBiometry() {
        dataStorage.put(BiometryState.SKIP, BIOMETRY_STATE)
    }

    override fun enrollBiometry() {
        dataStorage.put(BiometryState.ENROLLED, BIOMETRY_STATE)
    }

    private fun hasBiometricCapability(): Int {
        val biometricManager = BiometricManager.from(context)
        return biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
    }

    private fun isBiometricReady() =
        hasBiometricCapability() == BiometricManager.BIOMETRIC_SUCCESS
}