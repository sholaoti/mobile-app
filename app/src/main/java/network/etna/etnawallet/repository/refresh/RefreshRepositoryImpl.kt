package network.etna.etnawallet.repository.refresh

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.util.*

class RefreshRepositoryImpl: RefreshRepository {

    private val instances: MutableList<RefreshInterface> = Collections.synchronizedList(mutableListOf())

    private val isRefreshingSubject: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false)

    private var refreshDisposable: Disposable? = null

    override fun getRefreshingObservable(): Observable<Boolean> {
        return isRefreshingSubject
    }

    override fun startRefresh() {
        isRefreshingSubject.value?.let {
            if (!it) {
                isRefreshingSubject.onNext(true)

                refreshDisposable?.dispose()

                refreshDisposable = Completable
                    .merge(
                        instances.map { it.refresh() }
                    )
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                            isRefreshingSubject.onNext(false)
                        },
                        {
                            isRefreshingSubject.onNext(false)
                        }
                    )
            }
        }
    }

    override fun register(instance: RefreshInterface) {
        instances.add(instance)
    }

    override fun unregister(instance: RefreshInterface) {
        instances.remove(instance)
    }

}