package network.etna.etnawallet.ui.login.createaccount

import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import org.koin.core.component.KoinComponent

class LoginCreateAccountByEmailViewModel : ViewModel(), KoinComponent {
    var email: String = ""

    private val password: BehaviorSubject<String> = BehaviorSubject.createDefault("")
    private val confirmPassword: BehaviorSubject<String> = BehaviorSubject.createDefault("")

    fun passwordChanged(text: String) {
        password.onNext(text)
    }

    fun confirmPasswordChanged(text: String) {
        confirmPassword.onNext(text)
    }

    fun passwordValid(): Observable<Boolean> {
        return password
            .map {
                it.length >= 8 &&
                it.containsLowercase() &&
                it.containsUppercase() &&
                it.containsDigits()
            }
    }

    fun confirmPasswordValid(): Observable<Boolean> {
        return Observable.combineLatest(
            password,
            confirmPassword,
            passwordValid()
        ) { password, confirmPassword, passwordValid ->
            password == confirmPassword && passwordValid
        }
    }
}

private fun String.containsLowercase(): Boolean {
    return this.firstOrNull { it.isLowerCase() } != null
}

private fun String.containsUppercase(): Boolean {
    return this.firstOrNull { it.isUpperCase() } != null
}

private fun String.containsDigits(): Boolean {
    return this.firstOrNull { it.isDigit() } != null
}