package network.etna.etnawallet.ui.login.pincode.otp

import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.authentication.PasswordlessType
import com.auth0.android.callback.Callback
import com.auth0.android.result.Credentials
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import network.etna.etnawallet.repository.auth0.Auth0Repository
import network.etna.etnawallet.repository.warningservice.WarningService
import network.etna.etnawallet.ui.login.pincode.PinCodeFragmentState
import network.etna.etnawallet.ui.login.pincode.PinCodeViewModel
import org.koin.core.component.inject

class OTPViewModel(auth0: Auth0): PinCodeViewModel() {

    private val authentication: AuthenticationAPIClient
    private val auth0Repository: Auth0Repository by inject()

    private val warningService: WarningService by inject()

    private var issueId: String? = null

    var userEmail: String?
        private set

    override val pinCodeMaxLength: Int = 6

    private val triesLeftSubject: BehaviorSubject<Int> = BehaviorSubject.createDefault(3)

    init {
        authentication = AuthenticationAPIClient(auth0)

        userEmail = auth0Repository.getUserInfo()?.email

        userEmail?.let { email ->
            authentication
                .passwordlessWithEmail(email, PasswordlessType.CODE, "email")
                .start(object: Callback<Void?, AuthenticationException> {
                    override fun onFailure(error: AuthenticationException) {
                        val x = 1
                    }

                    override fun onSuccess(result: Void?) {
                        val x = 1
                    }
                })
        }
    }

    override fun pinCodeEntered(pinCode: String) {
        userEmail?.let { email ->
            authentication
                .loginWithEmail(email, pinCode, "email")
                .start(object : Callback<Credentials, AuthenticationException> {
                    override fun onSuccess(result: Credentials) {
                        fragmentState.onNext(PinCodeFragmentState.SUCCESS)
                    }

                    override fun onFailure(error: AuthenticationException) {
                        triesLeftSubject.value?.let {
                            triesLeftSubject.onNext(it - 1)
                        }
                        fragmentState.onNext(PinCodeFragmentState.ERROR)
                        issueId = warningService.handleIssue(OTPError)
                    }
                })
        }
    }

    private fun cancelWarning() {
        if (issueId != null) {
            warningService.cancelIssue(issueId!!)
            issueId = null
        }
    }

    override fun digitPressedEvent() {
        cancelWarning()
    }

    override fun clearPressed() {
        super.clearPressed()
        cancelWarning()
    }

    fun getTriesLeftObservable(): Observable<Int> {
        return triesLeftSubject
    }

}