package network.etna.etnawallet.repository.wallets.cryptowallet

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import network.etna.etnawallet.repository.blockchainnetworks.BlockchainNetworksRepository
import network.etna.etnawallet.repository.mnemonic.MnemonicRepository
import network.etna.etnawallet.repository.storage.DataStorage
import network.etna.etnawallet.repository.wallets.cryptowallet.model.*
import network.etna.etnawallet.repository.wallets.cryptowalletcredentials.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.web3j.crypto.MnemonicUtils
import java.math.BigInteger
import java.util.*
import java.util.concurrent.TimeUnit

class CryptoWalletsRepositoryImpl : KoinComponent, CryptoWalletsRepository {

    private val dataStorage: DataStorage by inject()
    private val credentialsRepository: CryptoWalletCredentialsRepository by inject()
    private val blockchainNetworksRepository: BlockchainNetworksRepository by inject()
    private val mnemonicRepository: MnemonicRepository by inject()

    companion object {
        private const val CRYPTO_WALLETS = "CRYPTO_WALLETS"
    }

    private val configSubject: BehaviorSubject<CryptoWalletConfig> =
        BehaviorSubject.createDefault(
            retrieveConfigFromStorage()
                ?: CryptoWalletConfig(emptyList())
        )

    private fun retrieveConfigFromStorage(): CryptoWalletConfig? {
        val res = dataStorage.get(
            CryptoWalletConfig::class.java,
            CRYPTO_WALLETS
        ) {
            registerTypeAdapter(
                ERCToken::class.java,
                ERCTokenJsonDeserializer()
            )
        }

//        res?.wallets?.forEach {
//            it.platformTokens.clear()
//        }
        return res
    }

    private fun updateConfig(config: CryptoWalletConfig) {
        dataStorage.put(
            config,
            CRYPTO_WALLETS
        ) {
            registerTypeAdapter(
                ERCToken.ERC20::class.java,
                ERCTokenJsonSerializer()
            )
        }

        configSubject.onNext(config)
    }

    override fun getWalletsObservable(): Observable<List<CryptoWalletModel>> {
        return configSubject
            .map {
                it.wallets
            }
    }

    override fun getAvailableWalletsObservable(): Observable<List<CryptoWalletModel>> {
        return Observable.combineLatest(
            getWalletsObservable(),
            blockchainNetworksRepository.getPlatformsObservable())
            { wallets, platforms ->
                val availablePlatformIds = platforms.map { it.id }
                wallets.filter {
                    it.platformId == null || availablePlatformIds.contains(it.platformId)
                }
            }
    }

    override fun getWalletObservable(id: String): Observable<CryptoWalletModel> {
        return getWalletsObservable()
            .map { Optional.ofNullable(it.firstOrNull { it.id == id }) }
            .filter { it.isPresent }
            .map { it.get() }
    }

    override fun generateMultiWallet(walletName: String): Single<String> {
        return Single.create<String> { single ->
            try {
                single.onSuccess(
                    mnemonicRepository
                        .generateMnemonic()
                )
            } catch (e: Exception) {
                single.onError(e)
            }
        }.flatMap { seedPhrase ->
            importWalletSingle(walletName, null, CryptoWalletCredentials.Seed(seedPhrase))
        }
    }

    override fun importMultiWallet(walletName: String, seedPhrase: String): Completable {
        return mnemonicRepository
            .validateMnemonicCompletable(seedPhrase)
            .andThen(
                importWalletSingle(walletName, null, CryptoWalletCredentials.Seed(seedPhrase))
                    .ignoreElement()
            )
    }

    override fun importFlatWalletBySeed(walletName: String, platformId: String, seedPhrase: String): Completable {
        return mnemonicRepository
            .validateMnemonicCompletable(seedPhrase)
            .andThen(
                importWalletSingle(walletName, platformId, CryptoWalletCredentials.Seed(seedPhrase))
                    .ignoreElement()
            )
    }

    override fun importFlatWalletByAddress(walletName: String, platformId: String, address: String): Completable {
        return importWalletSingle(walletName, platformId, CryptoWalletCredentials.Address(address))
            .ignoreElement()
    }

    override fun importFlatWalletByPrivateKey(walletName: String, platformId: String, privateKey: String): Completable {
        return importWalletSingle(walletName, platformId, CryptoWalletCredentials.PrivateKey(privateKey))
            .ignoreElement()
    }

    private fun importWalletSingle(walletName: String, platformId: String?, credentials: CryptoWalletCredentials): Single<String> {
        return Single.create { single ->
            try {
                val walletId = UUID.randomUUID().toString()

                credentialsRepository.saveCredentials(
                    walletId,
                    credentials
                )

                val config = configSubject.value!!

                val newWallet = CryptoWalletModel(
                    walletId,
                    walletName,
                    true,
                    platformId,
                    mutableMapOf()
                )
                val newWallets = config.wallets.toMutableList().onEach { it.isActive = false }
                newWallets.add(newWallet)
                config.wallets = newWallets

                updateConfig(config)

                single.onSuccess(walletId)

            } catch (e: Exception) {
                single.onError(e)
            }
        }
    }

    override fun selectActiveWallet(id: String) {
        configSubject.value?.let { config ->
            config.wallets.forEach {
                it.isActive = it.id == id
            }
            updateConfig(config)
        }
    }

    override fun changeName(walletId: String, name: String) {
        configSubject.value?.let { config ->
            config.wallets.find { it.id == walletId }?.name = name
            updateConfig(config)
        }
    }

    override fun deleteWallet(id: String): Completable {
        return Completable.create { completable ->
            try {

                configSubject.value?.let { config ->

                    if (config.wallets.any { it.id == id && it.isActive }) {
                        throw Exception("Can't delete active wallet")
                    }

                    config.wallets = config.wallets.filter { it.id != id }
                    updateConfig(config)
                }

                credentialsRepository.deleteCredentials(id)

                completable.onComplete()

            } catch (e: Exception) {
                completable.onError(e)
            }
        }
    }

    override fun getCryptoBlockchainWalletsObservable(): Observable<List<CryptoBlockchainWallet>> {
        return Observable.combineLatest(
            getAvailableWalletsObservable(),
            blockchainNetworksRepository.getPlatformsObservable())
            { wallets, platforms ->

                wallets.map { wallet ->
                    val publicCredentials = credentialsRepository.getPublicCredentials(
                        wallet.id,
                        platforms.map { it.getWalletPath() })

                    CryptoBlockchainWallet(
                        wallet.id,
                        platforms
                            .filter { platform ->
                                wallet.platformId == null || wallet.platformId == platform.id
                            }
                            .mapIndexed { index, platform ->
                                CryptoBlockchain(
                                    platform.id,
                                    (publicCredentials[index] as? PublicCredential.PublicKey)?.value,
                                    (publicCredentials[index] as? PublicCredential.Address)?.value,
                                    (wallet.platformTokens[platform.id] ?: emptyList())
                                        .mapNotNull { it as? ERCToken.ERC20 }
                                        .filter { it.isVisible }
                                )
                            }
                    )
                }
            }
            .debounce(300, TimeUnit.MILLISECONDS)
    }

    override fun hasWallet(walletId: String): Boolean {
        return configSubject.value?.wallets?.any { it.id == walletId } ?: false
    }

    override fun hasWallets(): Boolean {
        return configSubject.value?.wallets?.isNotEmpty() ?: false
    }

    override fun selectWalletByPlatforms(platforms: List<String>): Completable {
        return getWalletsObservable()
            .map {
                it.filter { it.platformId == null || platforms.contains(it.platformId) }
            }
            .take(1)
            .flatMapCompletable { wallets ->
                when {
                    wallets.any { it.isActive } -> {
                        Completable.complete()
                    }
                    wallets.isNotEmpty() -> {
                        selectActiveWallet(wallets.first().id)
                        Completable.complete()
                    }
                    else -> {
                        Completable.error(Exception(""))
                    }
                }
            }
    }

    override fun addPlatformToken(platformId: String, token: ERCToken) {
        configSubject.value?.let { config ->
            config.wallets
                .firstOrNull { it.isActive }
                ?.let { activeWallet ->
                    val mutableTokens =
                        (activeWallet.platformTokens[platformId] ?: emptyList()).toMutableList()

                    var needsUpdate = false

                    if (!mutableTokens.contains(token)) {
                        mutableTokens.add(token)
                        needsUpdate = true
                    }

                    if (needsUpdate) {
                        activeWallet.platformTokens[platformId] = mutableTokens

                        updateConfig(config)
                    }
                }
        }
    }

    override fun addPlatformTokens(platformId: String, tokens: List<ERCToken>) {
        configSubject.value?.let { config ->
            config.wallets
                .firstOrNull { it.isActive }
                ?.let { activeWallet ->
                    val mutableTokens =
                        (activeWallet.platformTokens[platformId] ?: emptyList()).toMutableList()

                    var needsUpdate = false

                    tokens.forEach {
                        if (!mutableTokens.contains(it)) {
                            mutableTokens.add(it)
                            needsUpdate = true
                        }
                    }

                    if (needsUpdate) {
                        activeWallet.platformTokens[platformId] = mutableTokens

                        updateConfig(config)
                    }
                }
        }
    }

    override fun setTokenVisibility(platformId: String, tokenAddress: String, isVisible: Boolean) {
        configSubject.value?.let { config ->
            config.wallets
                .firstOrNull { it.isActive }
                ?.platformTokens?.get(platformId)
                ?.firstOrNull { it.tokenAddress == tokenAddress }
                ?.let { it as? ERCToken.ERC20 }
                ?.let {
                    it.isVisible = isVisible
                    updateConfig(config)
                }
        }
    }

    override fun getActiveWalletTokenList(platformId: String): Observable<List<ERCToken>> {
        return getWalletsObservable()
            .map {
                it.first { it.isActive }
            }
            .map {
                it.platformTokens[platformId] ?: emptyList()
            }
    }
}
