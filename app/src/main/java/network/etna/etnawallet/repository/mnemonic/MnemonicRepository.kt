package network.etna.etnawallet.repository.mnemonic

import io.reactivex.Completable

interface MnemonicRepository {
    fun generateMnemonic(): String
    fun validateMnemonicCompletable(mnemonic: String): Completable
}