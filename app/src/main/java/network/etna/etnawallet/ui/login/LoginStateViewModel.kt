package network.etna.etnawallet.ui.login

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import network.etna.etnawallet.R
import network.etna.etnawallet.repository.auth0.Auth0Repository
import network.etna.etnawallet.repository.biometry.BiometryRepository
import network.etna.etnawallet.repository.biometry.BiometryState
import network.etna.etnawallet.repository.pincode.PinCodeRepository
import network.etna.etnawallet.repository.wallets.cryptowallet.CryptoWalletsRepository
import network.etna.etnawallet.ui.helpers.extensions.navigate
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

sealed class LoginState {
    object LOGIN : LoginState()
    object EMAIL_VERIFICATION : LoginState()
    object CREATE_PIN_CODE : LoginState()
    object CHECK_PIN_CODE : LoginState()
    object ACTIVATE_BIOMETRY : LoginState()
    object IMPORT_WALLET : LoginState()
    object DONE : LoginState()
}

fun LoginState.getDestinationFragmentId(): Int {
    return when (this) {
        LoginState.LOGIN -> R.id.loginStartFragment
        LoginState.EMAIL_VERIFICATION -> R.id.loginCheckEmailFragment
        LoginState.CREATE_PIN_CODE -> R.id.loginCreatePinCodeFragment
        LoginState.CHECK_PIN_CODE -> R.id.loginCheckPinCodeFragment
        LoginState.ACTIVATE_BIOMETRY -> R.id.activateBiometryFragment
        LoginState.IMPORT_WALLET -> R.id.importWalletActivity
        LoginState.DONE -> R.id.mainActivity
    }
}

class LoginStateViewModel: ViewModel(), KoinComponent {

    private val auth0Repository: Auth0Repository by inject()
    private val biometryRepository: BiometryRepository by inject()
    private val pinCodeRepository: PinCodeRepository by inject()
    private val cryptoWalletsRepository: CryptoWalletsRepository by inject()

    fun goToNextState(fragment: Fragment): Disposable {
        return getNextState()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    when (it) {
                        LoginState.DONE -> {
                            fragment.navigate(it.getDestinationFragmentId(), null)
                            fragment.requireActivity().finish()
                        }
                        LoginState.IMPORT_WALLET -> {
                            fragment.navigate(it.getDestinationFragmentId(), null)
                            fragment.requireActivity().finish()
                        }
                        else -> {
                            fragment.navigate(it.getDestinationFragmentId(), null) {
                                popUpTo(R.id.nav_graph_login) {
                                    inclusive = true
                                }
                            }
                        }
                    }
                },
                {}
            )
    }

    private fun getNextState(): Single<LoginState> {

        if (loginNeeded()) {
            return Single.just(LoginState.LOGIN)
        } else {
            return isEmailVerified()
                .flatMap { emailVerified ->
                    if (emailVerified) {
                        if (pinCodeRepository.hasPinCode()) {
                            if (pinCodeRepository.isPinCodeVerified()) {
                                if (needActivateBiometry()) {
                                    Single.just(LoginState.ACTIVATE_BIOMETRY)
                                } else {
                                    if (cryptoWalletsRepository.hasWallets()) {
                                        Single.just(LoginState.DONE)
                                    } else {
                                        Single.just(LoginState.IMPORT_WALLET)
                                    }
                                }
                            } else {
                                Single.just(LoginState.CHECK_PIN_CODE)
                                //Single.just(LoginState.DONE)
                            }
                        } else {
                            Single.just(LoginState.CREATE_PIN_CODE)
                        }
                    } else {
                        Single.just(LoginState.EMAIL_VERIFICATION)
                    }
                }
        }
    }

    private fun loginNeeded(): Boolean {
        return auth0Repository.loginNeeded()
    }

    private fun isEmailVerified(): Single<Boolean> {
        return auth0Repository
            .getUserInfoObservable()
            .take(1)
            .firstOrError()
            .map {
                it.email_verified
            }
    }

    private fun needActivateBiometry(): Boolean {
        return biometryRepository.getBiometryState() == BiometryState.ALLOWED
    }
}