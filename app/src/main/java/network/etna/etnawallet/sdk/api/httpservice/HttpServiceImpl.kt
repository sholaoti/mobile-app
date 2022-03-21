package network.etna.etnawallet.sdk.api.httpservice

import io.reactivex.Single
import network.etna.etnawallet.extensions.GsonBuilderOptions
import network.etna.etnawallet.extensions.convert
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException
import java.util.concurrent.TimeUnit

class HttpServiceImpl: HttpService {

    private val client: OkHttpClient

    init {

        val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        client = OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .addInterceptor(httpLoggingInterceptor)
            .build()

    }

    override fun <T> request(url: String, clazz: Class<T>, gsonBuilderOptions: GsonBuilderOptions?): Single<T> {
        return Single.create<String> { single ->
            try {
                val request = Request.Builder().url(url).build()
                client.newCall(request).enqueue(
                    object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            single.onError(e)
                        }

                        override fun onResponse(cal: Call, response: Response) {
                            try {
                                val body = response.body!!.string()
                                single.onSuccess(body)
                            } catch (e: Exception) {
                                single.onError(e)
                            }
                        }
                    }
                )
            } catch (e: Exception) {
                single.onError(e)
            }
        }.flatMap {
            it.convert(clazz, gsonBuilderOptions)
        }
    }

}