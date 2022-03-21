package network.etna.etnawallet.sdk.api.etherscan

import network.etna.etnawallet.sdk.api.models.TransactionModel
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query
import java.math.BigDecimal

interface EtherScanAPI {

    @GET("api?module=account&action=txlist")
    fun listTransactions(
        @Query("address") address: String,
        @Query("startblock") startblock: Int,
        @Query("endblock") endblock: Int,
        @Query("sort") sort: String
    ): Single<BaseResponse<List<TransactionModel>>>

    @GET("api?module=account&action=tokentx")
    fun listERC20Transactions(
        @Query("address") address: String,
        @Query("startblock") startblock: Int,
        @Query("endblock") endblock: Int,
        @Query("sort") sort: String
    ): Single<BaseResponse<List<TransactionModel>>>

    @GET("api?module=account&action=tokenbalance&tag=latest")
    fun getTokenBalance(
        @Query("contractaddress") contractaddress: String,
        @Query("address") address: String
    ): Single<BaseResponse<BigDecimal>>
}

class BaseResponse<T>(
    val result: T
)
