package network.etna.etnawallet.repository.auth0

import com.auth0.android.result.Credentials
import io.reactivex.Observable
import network.etna.etnawallet.sdk.api.auth0.UserInfo

interface Auth0Repository {
    fun updateCredentials(credentials: Credentials?)
    fun getUserInfoObservable(): Observable<UserInfo>
    fun getUserInfo(): UserInfo?
    fun loginNeeded(): Boolean
    fun refreshUserInfo()
}