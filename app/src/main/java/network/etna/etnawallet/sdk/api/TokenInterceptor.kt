package network.etna.etnawallet.sdk.api

import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor(
    private val token: String
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var original = chain.request()
        val url = original.url.newBuilder().addQueryParameter("apikey", token).build()
        original = original.newBuilder().url(url).build()
        return chain.proceed(original)
    }
}