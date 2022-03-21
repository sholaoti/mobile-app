package network.etna.etnawallet.repository.auth0

import com.auth0.android.result.Credentials
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import network.etna.etnawallet.sdk.api.auth0.Auth0API
import network.etna.etnawallet.sdk.api.auth0.Auth0TokenInterceptor
import network.etna.etnawallet.sdk.api.auth0.RefreshTokenRequest
import network.etna.etnawallet.sdk.api.auth0.UserInfo
import network.etna.etnawallet.sdk.core.AccessTokenProvider
import network.etna.etnawallet.repository.storage.DataStorage
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

class Auth0RepositoryImpl : Auth0Repository, KoinComponent, AccessTokenProvider {

    private val dataStorage: DataStorage by inject()
    private val auth0TokenInterceptor: Auth0TokenInterceptor by inject()
    private val auth0API: Auth0API by inject()

    companion object {
        private const val AUTH0_CREDENTIALS = "AUTH0_CREDENTIALS"
    }

    private val credentialsSubject: BehaviorSubject<Optional<Credentials>> =
        BehaviorSubject.createDefault(
            Optional.ofNullable(dataStorage.get(Credentials::class.java, AUTH0_CREDENTIALS))
        )

    private val userInfoSubject: BehaviorSubject<Optional<UserInfo>> =
        BehaviorSubject.createDefault(Optional.empty())

    private var credentialsSubjectDisposable: Disposable? = null
    private var refreshUserInfoDisposable: Disposable? = null
    private var refreshTokenDisposable: Disposable? = null

    init {
        credentialsSubjectDisposable = credentialsSubject
            .subscribe { credentials ->
                if (credentials.isPresent) {
                    if (Date() > credentials.get().expiresAt) {
                        credentials.get().refreshToken?.let {
                            refreshToken(it)
                        }
                    } else {
                        auth0TokenInterceptor.token = credentials.get().accessToken
                        refreshUserInfo()
                    }
                } else {
                    auth0TokenInterceptor.token = null
                }
            }
    }

    override fun getUserInfoObservable(): Observable<UserInfo> {
        return userInfoSubject
            .filter { it.isPresent }
            .map { it.get() }
    }

    override fun getUserInfo(): UserInfo? {
        return userInfoSubject.value?.get()
    }

    private fun refreshToken(refreshToken: String) {
        refreshTokenDisposable = auth0API
            .refreshToken(RefreshTokenRequest(refresh_token = refreshToken))
            .subscribeOn(Schedulers.io())
            .subscribe(
                { resp ->
                    credentialsSubject.value?.let {
                        if (it.isPresent) {
                            val credentials = it.get()

                            val calendar = Calendar.getInstance()
                            calendar.add(Calendar.SECOND, resp.expires_in)
                            val date = calendar.time

                            val newCredentials = Credentials(
                                credentials.idToken,
                                resp.access_token,
                                resp.token_type,
                                credentials.refreshToken,
                                date,
                                credentials.scope
                            )
                            updateCredentials(newCredentials)
                        }
                    }
                },
                {
                    val x = 1
                }
            )
    }

    override fun refreshUserInfo() {
        refreshUserInfoDisposable?.dispose()

        refreshUserInfoDisposable = auth0API
            .getUserInfo()
            .subscribeOn(Schedulers.io())
            .subscribe(
                {
                    userInfoSubject.onNext(Optional.of(it))
                },
                {
                    credentialsSubject.value?.let {
                        if (it.isPresent) {
                            val credentials = it.get()

                            credentials.refreshToken?.let {
                                refreshToken(it)
                            }
                        }
                    }
                }
            )
    }

    override fun loginNeeded(): Boolean {
        return credentialsSubject.value?.isPresent?.not() ?: true
    }

    override fun updateCredentials(credentials: Credentials?) {
        userInfoSubject.onNext(Optional.empty())
        dataStorage.put(credentials, AUTH0_CREDENTIALS)
        credentialsSubject.onNext(Optional.ofNullable(credentials))
    }

    override fun getAccessTokenObservable(): Observable<Optional<String>> {
        return credentialsSubject
            .map {
                if (it.isPresent) {
                    Optional.of(it.get().accessToken)
                } else {
                    Optional.empty()
                }
            }
            .distinctUntilChanged()
    }
}