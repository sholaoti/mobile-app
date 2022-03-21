package network.etna.etnawallet.sdk.core.model

class LoginReq {
    class LoginRequest(
        val token: String
    )

    class LoginResponse(
        val success: Boolean
    )
}