package network.etna.etnawallet.ui.asset.send.sendform

import network.etna.etnawallet.R
import network.etna.etnawallet.sdk.error.BaseError

sealed class NoCameraPermissionError {
    object NoCamera : BaseError.Warning() {
        override val resourceId: Int
            get() = R.string.error_no_camera_permission
    }
}