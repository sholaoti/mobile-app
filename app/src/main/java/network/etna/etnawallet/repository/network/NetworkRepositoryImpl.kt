package network.etna.etnawallet.repository.network

import android.annotation.SuppressLint
import android.content.Context
import android.net.NetworkInfo
import android.util.Log
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import network.etna.etnawallet.extensions.debounceIf
import network.etna.etnawallet.repository.warningservice.WarningService
import network.etna.etnawallet.sdk.error.BaseError
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit

enum class NetworkStatus {
    DISCONNECTED, CONNECTING, CONNECTED
}

val NetworkStatus.baseError: BaseError?
    get() {
        return when (this) {
            NetworkStatus.DISCONNECTED -> NetworkServiceError.NoInternetConnection
            NetworkStatus.CONNECTING -> NetworkServiceError.WaitingInternetConnection
            NetworkStatus.CONNECTED -> null
        }
    }

@SuppressLint("CheckResult")
class NetworkRepositoryImpl(context: Context): NetworkRepository, KoinComponent {

    private val warningService: WarningService by inject()
    private var warningId: String? = null

    init {

        Observable
            .combineLatest(
                ReactiveNetwork
                    .observeNetworkConnectivity(context),
                ReactiveNetwork
                    .observeInternetConnectivity()
                    .startWith(false)
            ) { connectivity, hasInternet ->
                val state = connectivity.state()
                when {
                    state == NetworkInfo.State.CONNECTED && hasInternet -> NetworkStatus.CONNECTED
                    state == NetworkInfo.State.CONNECTED && !hasInternet -> NetworkStatus.CONNECTING
                    else -> NetworkStatus.DISCONNECTED
                }
            }
            .distinctUntilChanged()
            .debounceIf({ it != NetworkStatus.CONNECTED }, 3, TimeUnit.SECONDS, Schedulers.io())
            .retryWhen { throwables ->
                throwables.delay(1, TimeUnit.SECONDS)
            }
            .subscribe { networkStatus ->

                Log.d("NetworkRepositoryImpl", networkStatus.name)

                if (warningId != null) {
                    warningService.cancelIssue(warningId!!)
                    warningId = null
                }

                networkStatus.baseError?.let {
                    warningId = warningService.handleIssue(it, true)
                }
            }
    }
}