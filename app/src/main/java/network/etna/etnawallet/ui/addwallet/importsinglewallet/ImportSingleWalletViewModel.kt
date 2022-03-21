package network.etna.etnawallet.ui.addwallet.importsinglewallet

import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import network.etna.etnawallet.sdk.api.etna.model.BlockchainPlatformModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ImportSingleWalletViewModel: ViewModel(), KoinComponent {

    private val scenarioModel: ImportSingleWalletScenarioModel by inject()

    val platform: BlockchainPlatformModel
        get() = scenarioModel.platform

    private val importVariantsSubject: BehaviorSubject<List<ImportVariant>> =
        BehaviorSubject.createDefault(ImportVariant.values().toList())

    fun getImportVariantsObservable(): Observable<List<ImportVariant>> {
        return importVariantsSubject
    }

    fun setActive(model: ImportVariant) {
        importVariantsSubject.value?.let { variants ->
            variants.forEach { it.isActive = it == model }
            importVariantsSubject.onNext(variants)
        }
    }
}