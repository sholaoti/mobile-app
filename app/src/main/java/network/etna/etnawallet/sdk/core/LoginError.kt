package network.etna.etnawallet.sdk.core

import network.etna.etnawallet.R
import network.etna.etnawallet.sdk.error.BaseError

sealed class LoginError {
    object UnableLogin: BaseError.Error() {
        override val resourceId: Int
            get() = R.string.error_unable_login
    }
}
