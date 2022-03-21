package network.etna.etnawallet.sdk.core

import io.reactivex.Observable
import java.util.*

interface AccessTokenProvider {
    fun getAccessTokenObservable(): Observable<Optional<String>>
}