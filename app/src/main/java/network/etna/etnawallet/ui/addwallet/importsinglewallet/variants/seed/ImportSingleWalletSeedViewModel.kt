package network.etna.etnawallet.ui.addwallet.importsinglewallet.variants.seed

import androidx.lifecycle.ViewModel
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import network.etna.etnawallet.repository.wallets.cryptowallet.CryptoWalletsRepository
import network.etna.etnawallet.ui.addwallet.importmultiwallet.AddWordStatus
import network.etna.etnawallet.ui.addwallet.importmultiwallet.WordModel
import network.etna.etnawallet.ui.addwallet.importmultiwallet.WordStatus
import network.etna.etnawallet.ui.addwallet.importsinglewallet.ImportSingleWalletScenarioModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.web3j.crypto.MnemonicUtils

class ImportSingleWalletSeedViewModel: ViewModel(), KoinComponent {

    companion object {
        private const val ALPHA_CORRECT_TEST_PHRASE = "candy annual wealth student jazz wreck feed mail wonder birth arrest tackle"
        private const val TRUST_CORRECT_TEST_PHRASE = "weird raven empty keep hotel topic lake garlic order tail must sting"
        private const val ETNA_CORRECT_TEST_PHRASE = "again body return laptop jar pig abandon grain brush original segment piano"
        private const val CORRECT_TEST_PHRASE = ALPHA_CORRECT_TEST_PHRASE
    }

    private val dataSubjectTest: BehaviorSubject<List<WordModel>> =
        BehaviorSubject.createDefault(CORRECT_TEST_PHRASE.split(" ").map { WordModel(it, WordStatus.NORMAL) })

    private val scenarioModel: ImportSingleWalletScenarioModel by inject()
    private val cryptoWalletsRepository: CryptoWalletsRepository by inject()

    private val dataSubject: BehaviorSubject<List<WordModel>> = BehaviorSubject.createDefault(emptyList())

    private val walletNameSubject: BehaviorSubject<String> = BehaviorSubject.createDefault("")

    private val allWords = MnemonicUtils.getWords()

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
        return dataSubject
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
                return cryptoWalletsRepository.importFlatWalletBySeed(walletName, scenarioModel.platform.id, seedPhrase)
            }
        }
        return Completable.error(Exception("bad words"))
    }
}