package network.etna.etnawallet.repository.loadingIndicatorService

import io.reactivex.Observable

enum class LoadingIndicatorState {
    NORMAL, LOADING;

    operator fun plus(other: LoadingIndicatorState): LoadingIndicatorState {
        return if (this == NORMAL && other == NORMAL) {
            NORMAL
        } else {
            LOADING
        }
    }
}

interface LoadingIndicatorService {
    fun updateState(id: String, loadingIndicatorState: LoadingIndicatorState)
    fun getLoadingIndicatorStateObservable(id: String): Observable<LoadingIndicatorState>
}