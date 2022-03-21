package network.etna.etnawallet.sdk.api.etna

import io.reactivex.Single
import network.etna.etnawallet.sdk.api.etna.model.BlockchainPlatformModel
import retrofit2.http.GET

interface EtnaAPI {
    @GET("wallet/platforms")
    fun getPlatforms(): Single<List<BlockchainPlatformModel>>
}
