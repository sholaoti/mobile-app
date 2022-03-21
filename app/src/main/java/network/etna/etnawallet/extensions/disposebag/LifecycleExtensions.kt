package com.kizitonwose.android.disposebag

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.DefaultLifecycleObserver
import io.reactivex.disposables.Disposable

/**
 * Created by Kizito Nwose
 */

fun Disposable.disposedWith(owner: LifecycleOwner,
                            event: Lifecycle.Event = DisposeBagPlugins.defaultLifecycleDisposeEvent) {
    disposedWith(owner.lifecycle, event)
}

fun Disposable.disposedWith(lifecycle: Lifecycle,
                            event: Lifecycle.Event = DisposeBagPlugins.defaultLifecycleDisposeEvent) {

    lifecycle.addObserver(object : DefaultLifecycleObserver {

        override fun onPause(owner: LifecycleOwner) {
            if (event == Lifecycle.Event.ON_PAUSE) {
                removeObserverAndDispose(owner)
            }
        }

        override fun onStop(owner: LifecycleOwner) {
            if (event == Lifecycle.Event.ON_STOP) {
                removeObserverAndDispose(owner)
            }
        }

        override fun onDestroy(owner: LifecycleOwner) {
            if (event == Lifecycle.Event.ON_DESTROY) {
                removeObserverAndDispose(owner)
            }
        }

        fun removeObserverAndDispose(owner: LifecycleOwner) {
            owner.lifecycle.removeObserver(this)
            dispose()
        }
    })
}