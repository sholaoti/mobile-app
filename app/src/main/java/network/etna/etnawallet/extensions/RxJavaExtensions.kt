package network.etna.etnawallet.extensions

import io.reactivex.Observable
import io.reactivex.Scheduler
import java.util.concurrent.TimeUnit

fun <T> Observable<T>.debounceIf(
    predicate: (T) -> Boolean,
    timeout: Long,
    unit: TimeUnit,
    scheduler: Scheduler
): Observable<T> {
    return this.publish { sharedSrc ->
        Observable.merge(
            sharedSrc.debounce(timeout, unit, scheduler)
                .filter { predicate(it) },
            sharedSrc.filter { !predicate(it) }
        )
    }
}