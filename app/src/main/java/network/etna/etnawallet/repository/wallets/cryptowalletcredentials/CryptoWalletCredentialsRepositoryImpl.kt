package network.etna.etnawallet.repository.wallets.cryptowalletcredentials

import io.reactivex.Single
import network.etna.etnawallet.extensions.toHexString
import network.etna.etnawallet.repository.storage.DataStorage
import okio.ByteString.Companion.decodeHex
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.web3j.crypto.Bip32ECKeyPair
import org.web3j.crypto.MnemonicUtils
import org.web3j.crypto.Sign
import java.math.BigInteger

class CryptoWalletCredentialsRepositoryImpl: KoinComponent, CryptoWalletCredentialsRepository {

    private val dataStorage: DataStorage by inject()

    companion object {
        private const val CREDENTIALS = "CREDENTIALS"
    }

    override fun saveCredentials(walletId: String, credentials: CryptoWalletCredentials) {
        dataStorage.put(credentials, getKeyForWalletId(walletId)) {
            registerTypeAdapter(
                CryptoWalletCredentials.Seed::class.java,
                CryptoWalletCredentialsJsonSerializer())
            registerTypeAdapter(
                CryptoWalletCredentials.Address::class.java,
                CryptoWalletCredentialsJsonSerializer())
            registerTypeAdapter(
                CryptoWalletCredentials.PrivateKey::class.java,
                CryptoWalletCredentialsJsonSerializer())
        }
    }

    override fun deleteCredentials(walletId: String) {
        dataStorage.put(null, getKeyForWalletId(walletId))
    }

    override fun getPublicCredential(walletId: String, path: IntArray): PublicCredential {
        return getPublicCredentials(walletId, listOf(path)).first()
    }

    override fun getPublicCredentials(walletId: String, paths: List<IntArray>): List<PublicCredential> {

        val credentials = dataStorage.get(
            CryptoWalletCredentials::class.java,
            getKeyForWalletId(walletId)
        ) {
            registerTypeAdapter(
                CryptoWalletCredentials::class.java,
                CryptoWalletCredentialsJsonDeserializer())
        }!!

        return when (credentials) {
            is CryptoWalletCredentials.Seed -> {
                val seed = MnemonicUtils.generateSeed(credentials.value, "")
                val masterKeyPair = Bip32ECKeyPair.generateKeyPair(seed)

                paths.map { path ->
                    val bip44Keypair = Bip32ECKeyPair.deriveKeyPair(masterKeyPair, path)
                    val compressed = bip44Keypair.publicKeyPoint.getEncoded(true).toHexString()
                    PublicCredential.PublicKey(compressed)
                }
            }

            is CryptoWalletCredentials.PrivateKey -> {
                paths.map {
                    val hexPrivateKey = credentials.value
                    getPublicKeyByPrivateKey(hexPrivateKey)
                }
            }

            is CryptoWalletCredentials.Address -> {
                paths.map {
                    PublicCredential.Address(credentials.value)
                }
            }
        }
    }

    override fun getPublicKeyByPrivateKey(privateKey: String): PublicCredential.PublicKey {
        val privateKeyBigInteger = BigInteger(privateKey, 16)
        val publicKeyPoint = Sign.publicPointFromPrivate(privateKeyBigInteger)
        val compressed = publicKeyPoint.getEncoded(true).toHexString()
        return PublicCredential.PublicKey(compressed)
    }

    override fun getRecoveryPhrase(walletId: String): List<String> {
        dataStorage.get(
            CryptoWalletCredentials::class.java,
            getKeyForWalletId(walletId)
        ) {
            registerTypeAdapter(
                CryptoWalletCredentials::class.java,
                CryptoWalletCredentialsJsonDeserializer()
            )
        }?.let { credentials ->
            when (credentials) {
                is CryptoWalletCredentials.Seed -> {
                    return credentials.value.split(" ")
                }
                else ->
                    throw Exception("Unsupported credentials type")
            }
        }
        return emptyList()
    }

    override fun hasRecoveryPhrase(walletId: String): Boolean {
        dataStorage.get(
            CryptoWalletCredentials::class.java,
            getKeyForWalletId(walletId)
        ) {
            registerTypeAdapter(
                CryptoWalletCredentials::class.java,
                CryptoWalletCredentialsJsonDeserializer())
        }?.let { credentials ->
            return credentials is CryptoWalletCredentials.Seed
        }

        return false
    }

    override fun signTransaction(walletId: String, path: IntArray, toSign: String): Single<SignResult> {

        return Single.create { single ->
            try {
                val credentials = dataStorage.get(
                    CryptoWalletCredentials::class.java,
                    getKeyForWalletId(walletId)
                ) {
                    registerTypeAdapter(
                        CryptoWalletCredentials::class.java,
                        CryptoWalletCredentialsJsonDeserializer())
                }!!

                val result: SignResult = when (credentials) {
                    is CryptoWalletCredentials.Seed -> {
                        val seed = MnemonicUtils.generateSeed(credentials.value, "")
                        val masterKeyPair = Bip32ECKeyPair.generateKeyPair(seed)
                        val keypair = Bip32ECKeyPair.deriveKeyPair(masterKeyPair, path)

                        val signature = CustomECDSASigner().sign(toSign.smartDecodeHex(), keypair)

                        val compressedPublicKey = keypair.publicKeyPoint.getEncoded(true).toHexString()

                        SignResult(
                            toSign,
                            signature,
                            compressedPublicKey
                        )
                    }

                    is CryptoWalletCredentials.PrivateKey -> {
                        val hexPrivateKey = credentials.value
                        val keypair = Bip32ECKeyPair.create(hexPrivateKey.smartDecodeHex())

                        val signature = CustomECDSASigner().sign(toSign.smartDecodeHex(), keypair)

                        val publicPoint = Sign.publicPointFromPrivate(BigInteger(hexPrivateKey))
                        val compressedPublicKey = publicPoint.getEncoded(true).toHexString()

                        SignResult(
                            toSign,
                            signature,
                            compressedPublicKey
                        )
                    }

                    is CryptoWalletCredentials.Address -> {
                        throw Exception("Unsupported")
                    }
                }

                single.onSuccess(result)
            } catch (e: Exception) {
                single.onError(e)
            }
        }
    }

    private fun getKeyForWalletId(walletId: String) = "${CREDENTIALS}_$walletId"
}

fun String.smartDecodeHex(): ByteArray {
    val str = if (startsWith("0x")) replaceFirst("0x", "") else this
    return str.decodeHex().toByteArray()
}