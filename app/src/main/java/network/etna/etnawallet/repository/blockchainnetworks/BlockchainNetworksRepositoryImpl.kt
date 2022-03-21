package network.etna.etnawallet.repository.blockchainnetworks

import android.annotation.SuppressLint
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import network.etna.etnawallet.repository.storage.DataStorage
import network.etna.etnawallet.repository.wallets.cryptowallet.CryptoWalletsRepository
import network.etna.etnawallet.repository.wallets.networkset.NetworkSet
import network.etna.etnawallet.repository.wallets.networkset.NetworkSetModel
import network.etna.etnawallet.sdk.api.etna.EtnaAPI
import network.etna.etnawallet.sdk.api.etna.model.BlockchainPlatformModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*
import java.util.concurrent.TimeUnit

@SuppressLint("CheckResult")
class BlockchainNetworksRepositoryImpl: BlockchainNetworksRepository, KoinComponent {

    private val etnaAPI: EtnaAPI by inject()
    private val dataStorage: DataStorage by inject()
    private val cryptoWalletsRepository: CryptoWalletsRepository by inject()

    companion object {
        private const val ACTIVE_NETWORK = "ACTIVE_NETWORK"
        private const val SHOW_TESTNET_WARNING = "SHOW_TESTNET_WARNING"
    }

    private val activeNetworkSubject: BehaviorSubject<NetworkSet> =
        BehaviorSubject.createDefault(dataStorage.get(NetworkSet::class.java, ACTIVE_NETWORK) ?: NetworkSet.MAINNET)

    private val showTestnetWarningSubject: BehaviorSubject<Boolean> =
        BehaviorSubject.createDefault(dataStorage.get(Boolean::class.java, SHOW_TESTNET_WARNING) ?: true)

    private val platformsSubject: BehaviorSubject<Optional<List<BlockchainPlatformModel>>> =
        BehaviorSubject.createDefault(Optional.empty())

    init {
        etnaAPI
            .getPlatforms()
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .retryWhen { throwables ->
                throwables.delay(3, TimeUnit.SECONDS)
            }
            .subscribe { platforms ->
                platformsSubject.onNext(Optional.of(platforms))
            }
    }

    override fun getPlatformsObservable(): Observable<List<BlockchainPlatformModel>> {
        return Observable.combineLatest(
            platformsSubject
                .filter { it.isPresent }
                .map { it.get() },
            getActiveNetworkObservable())
            { platforms, activeNetwork ->
                val isMainNet = activeNetwork == NetworkSet.MAINNET
                platforms.filter { it.mainNet == isMainNet }
            }
    }

    override fun getPlatformObservable(platform: String): Observable<BlockchainPlatformModel> {
        return getPlatformsObservable()
            .map { Optional.ofNullable(it.firstOrNull { it.id == platform }) }
            .filter { it.isPresent }
            .map { it.get() }
    }

    override fun getNetworkSetModelsObservable(): Observable<List<NetworkSetModel>> {
        return activeNetworkSubject
            .map { activeNetwork ->
                NetworkSet.values().map {
                    NetworkSetModel(
                        it,
                        activeNetwork == it
                    )
                }
            }
    }

    override fun getActiveNetworkObservable(): Observable<NetworkSet> {
        return activeNetworkSubject
    }

    override fun selectActiveNetwork(networkSet: NetworkSet): Completable {
        return platformsSubject
            .filter { it.isPresent }
            .map {
                val isMainNet = networkSet == NetworkSet.MAINNET
                it.get().filter { it.mainNet == isMainNet }.map { it.id }
            }
            .take(1)
            .flatMapCompletable {
                cryptoWalletsRepository.selectWalletByPlatforms(it)
            }
            .doOnComplete {
                dataStorage.put(networkSet, ACTIVE_NETWORK)
                activeNetworkSubject.onNext(networkSet)
            }
    }

    override fun showTestnetWarning(): Boolean {
        return showTestnetWarningSubject.value ?: true
    }

    override fun showTestnetWarningObservable(): Observable<Boolean> {
        return showTestnetWarningSubject
    }

    override fun setShowTestnetWarning(value: Boolean) {
        dataStorage.put(value, SHOW_TESTNET_WARNING)
        showTestnetWarningSubject.onNext(value)
    }
}