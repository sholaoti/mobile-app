package network.etna.etnawallet.repository.refresh

import io.reactivex.Observable

interface RefreshRepository {
    fun getRefreshingObservable(): Observable<Boolean>
    fun startRefresh()
    fun register(instance: RefreshInterface)
    fun unregister(instance: RefreshInterface)
}