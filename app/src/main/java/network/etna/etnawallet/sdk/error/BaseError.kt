package network.etna.etnawallet.sdk.error

sealed class BaseError: Exception() {
    abstract class Warning: BaseError()
    abstract class Error: BaseError()

    abstract val resourceId: Int
}
