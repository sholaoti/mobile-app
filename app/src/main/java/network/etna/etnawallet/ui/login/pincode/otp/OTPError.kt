package network.etna.etnawallet.ui.login.pincode.otp

import network.etna.etnawallet.R
import network.etna.etnawallet.sdk.error.BaseError

object OTPError: BaseError.Error() {
    override val resourceId: Int
        get() = R.string.error_wrong_otp
}