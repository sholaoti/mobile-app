package network.etna.etnawallet.repository.blockchainnetworks

import io.reactivex.Completable
import io.reactivex.Observable
import network.etna.etnawallet.repository.wallets.networkset.NetworkSet
import network.etna.etnawallet.repository.wallets.networkset.NetworkSetModel
import network.etna.etnawallet.sdk.api.etna.model.BlockchainPlatformModel

interface BlockchainNetworksRepository {
    fun getPlatformsObservable(): Observable<List<BlockchainPlatformModel>>
    fun getPlatformObservable(platform: String): Observable<BlockchainPlatformModel>
    fun getNetworkSetModelsObservable(): Observable<List<NetworkSetModel>>
    fun getActiveNetworkObservable(): Observable<NetworkSet>
    fun selectActiveNetwork(networkSet: NetworkSet): Completable
    fun showTestnetWarningObservable(): Observable<Boolean>
    fun showTestnetWarning(): Boolean
    fun setShowTestnetWarning(value: Boolean)
}