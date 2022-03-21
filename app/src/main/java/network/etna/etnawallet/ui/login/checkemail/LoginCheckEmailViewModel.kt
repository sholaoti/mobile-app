package network.etna.etnawallet.ui.login.checkemail

import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import network.etna.etnawallet.repository.auth0.Auth0Repository
import network.etna.etnawallet.sdk.api.auth0.UserInfo
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LoginCheckEmailViewModel: ViewModel(), KoinComponent {

    private val auth0Repository: Auth0Repository by inject()

    fun getUserInfoObservable(): Observable<UserInfo> {
        return auth0Repository
            .getUserInfoObservable()
    }

    fun refreshUserInfo() {
        auth0Repository.refreshUserInfo()
    }
}