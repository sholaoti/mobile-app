package network.etna.etnawallet.repository.pincode

interface PinCodeRepository {
    fun updatePinCode(pinCode: String)
    fun getPinCode(): String
    fun hasPinCode(): Boolean
    fun isPinCodeVerified(): Boolean
    fun setPinCodeVerified()
    fun setNeedsPinCode(value: Boolean)
    fun getNeedsPinCode(): Boolean
}