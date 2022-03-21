package network.etna.etnawallet.sdk.api.auth0

import okhttp3.Interceptor
import okhttp3.Response

class Auth0TokenInterceptor(
    var token: String? = null
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        if (token != null) {
            builder.addHeader("Authorization", "Bearer $token")
        }
        return chain.proceed(builder.build())
    }
}