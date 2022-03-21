package network.etna.etnawallet.ui.recoveryphrase.verify

import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import network.etna.etnawallet.repository.wallets.cryptowalletcredentials.CryptoWalletCredentialsRepository
import network.etna.etnawallet.ui.addwallet.importmultiwallet.WordModel
import network.etna.etnawallet.ui.addwallet.importmultiwallet.WordStatus
import network.etna.etnawallet.ui.recoveryphrase.RecoveryPhraseScenarioModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

enum class RecoveryPhraseVerifyState {
    NORMAL, FAILED, SUCCESS
}

class RecoveryPhraseVerifyViewModel: ViewModel(), KoinComponent {

    private val credentialsRepository: CryptoWalletCredentialsRepository by inject()
    private val scenarioModel: RecoveryPhraseScenarioModel by inject()

    private val initialList = credentialsRepository
        .getRecoveryPhrase(scenarioModel.walletId)

    private var testList = initialList.toMutableList()

    private val wordModelsSubject: BehaviorSubject<List<WordModel>> =
        BehaviorSubject.createDefault(
            initialList
                .shuffled()
                .map {
                    WordModel(it, WordStatus.NORMAL, false)
                }
        )

    fun getDataObservable(): Observable<List<Int>> {
        return Observable.just((0..11).toList())
    }

    fun getRecoveryPhraseVerifyStateObservable(): Observable<RecoveryPhraseVerifyState> {
        return wordModelsSubject
            .map {
                when {
                    it.any { it.status == WordStatus.ERROR } -> RecoveryPhraseVerifyState.FAILED
                    it.all { it.status == WordStatus.SUCCESS } -> RecoveryPhraseVerifyState.SUCCESS
                    else -> RecoveryPhraseVerifyState.NORMAL
                }
            }
    }

    fun getWordStatusObservable(position: Int): Observable<WordModel> {
        return wordModelsSubject
            .map {
                it[position]
            }
            //.distinctUntilChanged()
    }

    fun selectWord(position: Int) {
        wordModelsSubject.value?.let { wordModels ->

            if (wordModels.any { it.status == WordStatus.ERROR }) {
                return
            }
            if (wordModels[position].status == WordStatus.SUCCESS) {
                return
            }

            wordModels[position].status =
                if (testList.first() == wordModels[position].word)
                    WordStatus.SUCCESS
                else
                    WordStatus.ERROR

            testList.removeAt(0)

            wordModelsSubject.onNext(wordModels)
        }
    }

    fun tryAgain() {
        wordModelsSubject.value?.let { wordModels ->
            wordModelsSubject.onNext(
                wordModels
                    .onEach { it.status = WordStatus.NORMAL }
            )
            testList = initialList.toMutableList()
        }
    }
}