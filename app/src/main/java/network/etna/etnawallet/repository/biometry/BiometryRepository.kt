package network.etna.etnawallet.repository.biometry

interface BiometryRepository {
    fun getBiometryState(): BiometryState
    fun skipBiometry()
    fun enrollBiometry()
}