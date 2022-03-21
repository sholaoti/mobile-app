package network.etna.etnawallet.ui.helpers.extensions

import android.annotation.SuppressLint
import android.widget.TextView
import io.reactivex.BackpressureStrategy
import io.reactivex.Emitter
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit

@SuppressLint("SetTextI18n")
fun TextView.animateDots(): Disposable {
    val initialText = text.toString()

    return Flowable.generate(
        Callable { 0 },
        BiFunction { s: Int, emitter: Emitter<Int> ->
            val nextValue = if (s == 3) 0 else s + 1
            emitter.onNext(nextValue)
            nextValue
        })
        .concatMap { s ->
            Observable.just(s)
                .delay(300, TimeUnit.MILLISECONDS)
                .toFlowable(BackpressureStrategy.BUFFER)
        }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { dotsCount ->
            this.text = "$initialText${".".repeat(dotsCount)}"
        }
}