package network.etna.etnawallet.ui.recoveryphrase.initial

import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import org.koin.core.component.KoinComponent

class RecoveryPhraseInitialViewModel: ViewModel(), KoinComponent {

    private val recoveryPhraseDescAcceptedSubject: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false)

    fun getRecoveryPhraseDescAcceptedObservable(): Observable<Boolean> {
        return recoveryPhraseDescAcceptedSubject
    }

    fun acceptRecoveryPhraseDesc() {
        recoveryPhraseDescAcceptedSubject.onNext(true)
    }
}