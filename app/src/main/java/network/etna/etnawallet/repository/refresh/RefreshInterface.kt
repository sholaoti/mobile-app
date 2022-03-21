package network.etna.etnawallet.repository.refresh

import io.reactivex.Completable

interface RefreshInterface {
    fun refresh(): Completable
}