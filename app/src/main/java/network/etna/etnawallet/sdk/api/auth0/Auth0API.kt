package network.etna.etnawallet.sdk.api.auth0

import io.reactivex.Single
import retrofit2.http.*

interface Auth0API {

    @GET("userinfo")
    fun getUserInfo(): Single<UserInfo>

    @POST("oauth/token")
    fun refreshToken(
        @Body request: RefreshTokenRequest
    ): Single<RefreshTokenResponse>
}

data class RefreshTokenRequest(
    val grant_type: String = "refresh_token",
    val client_id: String = "WcU1RYmisYe6cvEOCi5RaamlaNvICddy",
    val client_secret: String = "G87P6AIjAjBa2frPRLgyNrTmN3lyjrUbE_-9aaVCtyb3D-HZKOFHrb-xmV_k429B",
    val refresh_token: String
)

data class RefreshTokenResponse(
    val access_token: String,
    val token_type: String,
    val expires_in: Int
)

data class GetTokenResponse(
    val access_token: String,
    val token_type: String,
    val expires_in: Int
)

data class UserInfo(
    val email: String,
    val email_verified: Boolean
)