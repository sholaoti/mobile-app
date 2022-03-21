package network.etna.etnawallet

import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import network.etna.etnawallet.repository.pincode.PinCodeRepository
import network.etna.etnawallet.repository.repositoryModule
import network.etna.etnawallet.sdk.api.apiModule
import network.etna.etnawallet.ui.viewModelsModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin

class EtnaWalletApplication: Application(), KoinComponent, LifecycleObserver {

    private val pinCodeRepository: PinCodeRepository by inject()

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@EtnaWalletApplication)
            modules(apiModule)
            modules(repositoryModule)
            modules(viewModelsModule)
        }

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onMoveToBackground() {
        pinCodeRepository.setNeedsPinCode(true)
    }
}