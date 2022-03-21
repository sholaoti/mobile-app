package network.etna.etnawallet.repository.warningservice

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import org.koin.core.context.GlobalContext

fun Completable.subscribeWithErrorHandler(
    onComplete: Action,
    onError: Consumer<in Throwable>
): Disposable {
    return this.subscribe(
        {
            onComplete.run()
        },
        {
            try {
                val warningService = GlobalContext.get().get<WarningService>()
                warningService.handleIssue(it)
            } catch (e: Exception) {}

            onError.accept(it)
        }
    )
}

fun Completable.subscribeWithErrorHandler(
    onComplete: Action
): Disposable {
    return this.subscribe(
        {
            onComplete.run()
        },
        {
            try {
                val warningService = GlobalContext.get().get<WarningService>()
                warningService.handleIssue(it)
            } catch (e: Exception) {}
        }
    )
}

fun <T> Observable<T>.subscribeWithErrorHandler(onNext: Consumer<in T>): Disposable {
    return this.subscribe(
        {
            onNext.accept(it)
        },
        {
            try {
                val warningService = GlobalContext.get().get<WarningService>()
                warningService.handleIssue(it)
            } catch (e: Exception) {}
        }
    )
}
