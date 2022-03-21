package network.etna.etnawallet.repository.mnemonic

import network.etna.etnawallet.R
import network.etna.etnawallet.sdk.error.BaseError

sealed class MnemonicRepositoryError {
    object InvalidMnemonic: BaseError.Error() {
        override val resourceId: Int
            get() = R.string.error_invalid_mnemonic
    }
}
