package network.etna.etnawallet.repository.wallets.cryptowallet

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import network.etna.etnawallet.repository.wallets.cryptowallet.model.CryptoBlockchainWallet
import network.etna.etnawallet.repository.wallets.cryptowallet.model.ERCToken
import network.etna.etnawallet.repository.wallets.cryptowalletcredentials.SignResult

interface CryptoWalletsRepository {
    fun importMultiWallet(walletName: String, seedPhrase: String): Completable
    fun importFlatWalletByAddress(walletName: String, platformId: String, address: String): Completable
    fun importFlatWalletByPrivateKey(walletName: String, platformId: String, privateKey: String): Completable
    fun importFlatWalletBySeed(walletName: String, platformId: String, seedPhrase: String): Completable
    fun generateMultiWallet(walletName: String): Single<String>
    fun getWalletsObservable(): Observable<List<CryptoWalletModel>>
    fun getAvailableWalletsObservable(): Observable<List<CryptoWalletModel>>
    fun getWalletObservable(id: String): Observable<CryptoWalletModel>
    fun selectActiveWallet(id: String)
    fun deleteWallet(id: String): Completable
    fun getCryptoBlockchainWalletsObservable(): Observable<List<CryptoBlockchainWallet>>
    fun changeName(walletId: String, name: String)
    fun hasWallet(walletId: String): Boolean
    fun hasWallets(): Boolean
    fun selectWalletByPlatforms(platforms: List<String>): Completable
    fun addPlatformToken(platformId: String, token: ERCToken)
    fun addPlatformTokens(platformId: String, tokens: List<ERCToken>)
    fun getActiveWalletTokenList(platformId: String): Observable<List<ERCToken>>
    fun setTokenVisibility(platformId: String, tokenAddress: String, isVisible: Boolean)
}