package network.etna.etnawallet.repository.mnemonic

import io.reactivex.Completable
import network.etna.etnawallet.extensions.toBinaryString
import org.web3j.crypto.MnemonicUtils
import java.security.MessageDigest
import java.security.SecureRandom

class MnemonicRepositoryImpl: MnemonicRepository {

    private val secureRandom by lazy { SecureRandom() }
    private val mnemonicWords by lazy { MnemonicUtils.getWords() }

    init {
        LinuxSecureRandom()
    }

    override fun generateMnemonic(): String {
        val iv = generateRandomBytes(16)

        val digest = MessageDigest.getInstance("SHA-256")
        val sha256 = digest.digest(iv)

        val entropy = "${iv.toBinaryString()}${sha256.toBinaryString().take(4)}"

        return entropy
            .chunked(11)
            .joinToString(" ") {
                mnemonicWords[it.toInt(2)]
            }
    }

    override fun validateMnemonicCompletable(mnemonic: String): Completable {
        return Completable.create { completable ->
            if (MnemonicUtils.validateMnemonic(mnemonic)) {
                completable.onComplete()
            } else {
                completable.onError(MnemonicRepositoryError.InvalidMnemonic)
            }
        }
    }

    private fun generateRandomBytes(size: Int): ByteArray {
        val bytes = ByteArray(size)
        secureRandom.nextBytes(bytes)
        return bytes
    }

}