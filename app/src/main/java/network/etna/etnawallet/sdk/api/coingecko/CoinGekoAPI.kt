package network.etna.etnawallet.sdk.api.coingecko

import com.google.gson.JsonObject
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CoinGekoAPI {

    @GET("api/v3/simple/price?include_24hr_change=true")
    fun getPrices(
        @Query("ids") ids: String,
        @Query("vs_currencies") vs_currencies: String
    ): Single<JsonObject>

    @GET("api/v3/simple/token_price/{id}?include_24hr_change=true")
    fun getTokenPrices(
        @Path("id") platform_id: String,
        @Query("contract_addresses") contract_addresses: String,
        @Query("vs_currencies") vs_currencies: String
    ): Single<JsonObject>

}