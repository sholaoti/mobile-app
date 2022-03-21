package network.etna.etnawallet.repository.network

import network.etna.etnawallet.R
import network.etna.etnawallet.sdk.error.BaseError

class NetworkServiceError {
    object NoInternetConnection: BaseError.Warning() {
        override val resourceId: Int
            get() = R.string.error_no_internet_connection
    }
    object WaitingInternetConnection: BaseError.Warning() {
        override val resourceId: Int
            get() = R.string.error_waiting_internet_connection
    }
}