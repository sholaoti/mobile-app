package network.etna.etnawallet.repository.loadingIndicatorService

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class LoadingIndicatorServiceImpl: LoadingIndicatorService {

    private val loadingIndicatorStateSubject: BehaviorSubject<Map<String, LoadingIndicatorState>> =
        BehaviorSubject.createDefault(HashMap())

    override fun getLoadingIndicatorStateObservable(id: String): Observable<LoadingIndicatorState> {
        return loadingIndicatorStateSubject
            .filter { it[id] != null }
            .map { it[id] }
    }

    override fun updateState(id: String, loadingIndicatorState: LoadingIndicatorState) {
        loadingIndicatorStateSubject.value?.let {
            if (loadingIndicatorState != it[id]) {
                val newMap = it.toMutableMap()
                newMap[id] = loadingIndicatorState
                loadingIndicatorStateSubject.onNext(newMap)
            }
        }
    }
}