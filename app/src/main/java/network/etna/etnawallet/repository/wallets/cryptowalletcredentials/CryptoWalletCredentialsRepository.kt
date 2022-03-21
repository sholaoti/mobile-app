package network.etna.etnawallet.repository.wallets.cryptowalletcredentials

import io.reactivex.Single

interface CryptoWalletCredentialsRepository {
    fun saveCredentials(walletId: String, credentials: CryptoWalletCredentials)
    fun deleteCredentials(walletId: String)
    fun getPublicCredential(walletId: String, path: IntArray): PublicCredential
    fun getPublicCredentials(walletId: String, paths: List<IntArray>): List<PublicCredential>
    fun getRecoveryPhrase(walletId: String): List<String>
    fun hasRecoveryPhrase(walletId: String): Boolean
    fun getPublicKeyByPrivateKey(privateKey: String): PublicCredential.PublicKey
    fun signTransaction(walletId: String, path: IntArray, toSign: String): Single<SignResult>
}