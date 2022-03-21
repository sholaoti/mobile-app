package network.etna.etnawallet.repository.pincode

import network.etna.etnawallet.R
import network.etna.etnawallet.sdk.error.BaseError

sealed class PinCodeRepositoryError {
    object WrongPin: BaseError.Error() {
        override val resourceId: Int
            get() = R.string.error_wrong_pin
    }
}