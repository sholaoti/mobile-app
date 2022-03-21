package network.etna.etnawallet.ui.addwallet.importmultiwallet

import network.etna.etnawallet.R
import network.etna.etnawallet.sdk.error.BaseError

object SeedPhraseInputError: BaseError.Warning() {
    override val resourceId: Int
        get() = R.string.error_seed_phrase_space
}
