package network.etna.etnawallet.repository.pincode

import network.etna.etnawallet.repository.storage.DataStorage
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PinCodeRepositoryImpl: PinCodeRepository, KoinComponent {

    private val dataStorage: DataStorage by inject()

    private var _pinCodeVerified: Boolean = false

    private var needsPinCodeValue: Boolean = false

    companion object {
        private const val PIN_CODE = "PIN_CODE"
    }

    override fun updatePinCode(pinCode: String) {
        dataStorage.put(pinCode, PIN_CODE)
    }

    override fun getPinCode(): String {
        return dataStorage.get(String::class.java, PIN_CODE) ?: ""
    }

    override fun hasPinCode(): Boolean {
        return getPinCode() != ""
    }

    override fun isPinCodeVerified(): Boolean {
        return _pinCodeVerified
    }

    override fun setPinCodeVerified() {
        _pinCodeVerified = true
    }

    override fun setNeedsPinCode(value: Boolean) {
        needsPinCodeValue = value
    }

    override fun getNeedsPinCode(): Boolean {
        return needsPinCodeValue
    }
}