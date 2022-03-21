package network.etna.etnawallet.ui.asset.send.sendform

import network.etna.etnawallet.R
import network.etna.etnawallet.sdk.error.BaseError

sealed class SendError {
    object InvalidAmount : BaseError.Error() {
        override val resourceId: Int
            get() = R.string.error_invalid_amount
    }

    object CheckingAddress : BaseError.Warning() {
        override val resourceId: Int
            get() = R.string.error_checking_address
    }

    object WrongAddress : BaseError.Error() {
        override val resourceId: Int
            get() = R.string.error_wrong_address
    }

    object InvalidData : BaseError.Error() {
        override val resourceId: Int
            get() = R.string.error_wrong_address
    }
}