package network.etna.etnawallet.ui.base.loading

import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import network.etna.etnawallet.repository.loadingIndicatorService.LoadingIndicatorState
import network.etna.etnawallet.repository.refresh.RefreshInterface
import network.etna.etnawallet.repository.refresh.RefreshRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class LoadingViewModel: ViewModel(), KoinComponent {
    protected val loadingSubject: BehaviorSubject<LoadingIndicatorState> =
        BehaviorSubject.createDefault(LoadingIndicatorState.LOADING)

    protected val composite by lazy { CompositeDisposable() }
    private val refreshRepository: RefreshRepository by inject()

    init {
        if (this is RefreshInterface) {
            refreshRepository.register(this)
        }
    }

    fun getLoadingObservable(): Observable<LoadingIndicatorState> {
        return loadingSubject
            .distinctUntilChanged()
    }

    override fun onCleared() {
        super.onCleared()
        composite.dispose()
        if (this is RefreshInterface) {
            refreshRepository.unregister(this)
        }
    }
}