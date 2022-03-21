package network.etna.etnawallet.ui.addwallet.importmultiwallet

import androidx.lifecycle.ViewModel
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import network.etna.etnawallet.repository.wallets.cryptowallet.CryptoWalletsRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.web3j.crypto.MnemonicUtils

enum class AddWordStatus {
    SUCCESS, FAILED, FINISHED
}

class ImportMultiWalletViewModel: ViewModel(), KoinComponent {

    companion object {
        private const val ALPHA_CORRECT_TEST_PHRASE = "candy annual wealth student jazz wreck feed mail wonder birth arrest tackle"
        private const val TRUST_CORRECT_TEST_PHRASE = "weird raven empty keep hotel topic lake garlic order tail must sting"
        private const val ETNA_CORRECT_TEST_PHRASE = "again body return laptop jar pig abandon grain brush original segment piano"
        private const val INCORRECT_TEST_PHRASE = "again body return laptop jar pig abandon grain brush original segment must"
        private const val CORRECT_TEST_PHRASE = ETNA_CORRECT_TEST_PHRASE
    }

    private val dataSubjectEmpty: BehaviorSubject<List<WordModel>> = BehaviorSubject.createDefault(emptyList())

    private val dataSubjectTest: BehaviorSubject<List<WordModel>> =
        BehaviorSubject.createDefault(CORRECT_TEST_PHRASE.split(" ").map { WordModel(it, WordStatus.NORMAL) })

    private val dataSubject: BehaviorSubject<List<WordModel>> = dataSubjectEmpty

    private val walletNameSubject: BehaviorSubject<String> = BehaviorSubject.createDefault("")

    private val allWords = MnemonicUtils.getWords()

    private val isEyeActivatedSubject: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(true)

    private val walletsRepository: CryptoWalletsRepository by inject()

    fun walletNameChanged(text: String) {
        walletNameSubject.onNext(text)
    }

    fun getImportAvailableObservable(): Observable<Boolean> {
        return Observable.combineLatest(
            dataSubject,
            walletNameSubject)
            { data, walletName ->
                walletName.isNotEmpty() && data.size == 12
            }
    }

    fun getDataObservable(): Observable<List<WordModel>> {
        return Observable.combineLatest(
            dataSubject,
            isEyeActivatedSubject)
            { data, eye ->
                if (eye) {
                    data
                } else {
                    data.map { WordModel("\u2022".repeat(it.word.length), it.status, it.isDeletable) }
                }
            }
    }

    fun isEyeActivatedObservable(): Observable<Boolean> {
        return isEyeActivatedSubject
    }

    fun invertEye() {
        isEyeActivatedSubject.value?.let {
            isEyeActivatedSubject.onNext(!it)
        }
    }

    fun addWord(word: String): AddWordStatus {
        if (allWords.contains(word)) {
            dataSubject.value?.let {
                if (it.size >= 12) {
                    return AddWordStatus.FINISHED
                }
                val newList = it.toMutableList()
                newList.add(WordModel(word, WordStatus.NORMAL))
                dataSubject.onNext(newList)

                if (newList.size == 12) {
                    return AddWordStatus.FINISHED
                }

            }
            return AddWordStatus.SUCCESS
        }
        return AddWordStatus.FAILED
    }

    fun removeWord(index: Int) {
        dataSubject.value?.let {
            val newList = it.toMutableList()
            if (index < newList.size) {
                newList.removeAt(index)
            }
            dataSubject.onNext(newList)
        }
    }

    fun importPressed(): Completable {
        dataSubject.value?.let { wordModelsList ->
            walletNameSubject.value?.let { walletName ->
                val seedPhrase = wordModelsList.joinToString(" ") { it.word }
                return walletsRepository.importMultiWallet(walletName, seedPhrase)
            }
        }
        return Completable.error(Exception("bad words"))
    }
}


