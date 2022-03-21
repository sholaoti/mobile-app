package network.etna.etnawallet.sdk.api

import network.etna.etnawallet.sdk.api.auth0.Auth0API
import network.etna.etnawallet.sdk.api.auth0.Auth0TokenInterceptor
import network.etna.etnawallet.sdk.api.coingecko.CoinGekoAPI
import network.etna.etnawallet.sdk.api.etherscan.EtherScanAPI
import network.etna.etnawallet.sdk.api.etna.EtnaAPI
import network.etna.etnawallet.sdk.api.etna.wssapi.EtnaWSSAPI
import network.etna.etnawallet.sdk.api.httpservice.HttpServiceImpl
import network.etna.etnawallet.sdk.core.APICore
import network.etna.etnawallet.sdk.core.APICoreImpl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val apiModule = module {

    single<network.etna.etnawallet.sdk.api.httpservice.HttpService> { HttpServiceImpl() }

    single { Auth0TokenInterceptor() }
    single {

        val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(get<Auth0TokenInterceptor>())
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://login-wallet.us.auth0.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        retrofit.create(Auth0API::class.java)
    }

    single {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.coingecko.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        retrofit.create(CoinGekoAPI::class.java)
    }

    single {
        val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .addInterceptor(httpLoggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://wlt-api.etna.network/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        retrofit.create(EtnaAPI::class.java)
    }

    single<APICore> { APICoreImpl("wss://wlt-api.etna.network/") }

    single {
        EtnaWSSAPI()
    }

    single { (url: String, token: String) ->

        val client = OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .addInterceptor(TokenInterceptor(token))
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        retrofit.create(EtherScanAPI::class.java)
    }

    single { (url: String) ->
        val builder = OkHttpClient.Builder()
        builder.connectTimeout(20, TimeUnit.SECONDS)
        builder.readTimeout(20, TimeUnit.SECONDS)
        //
        //val service = HttpService("https://data-seed-prebsc-1-s1.binance.org:8545/", builder.build(), false)
        val service = HttpService(url, builder.build(), false)
        Web3j.build(service)
    }

}