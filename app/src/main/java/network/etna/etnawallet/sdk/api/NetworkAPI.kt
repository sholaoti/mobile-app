package network.etna.etnawallet.sdk.api

import network.etna.etnawallet.sdk.api.etherscan.BaseResponse
import network.etna.etnawallet.sdk.api.models.TransactionModel
import io.reactivex.Single
import java.math.BigDecimal

interface NetworkAPI {

    fun listTransactions(
        address: String,
        startblock: Int,
        endblock: Int,
        sort: String
    ): Single<List<TransactionModel>>

    fun listERC20Transactions(
        address: String,
        startblock: Int,
        endblock: Int,
        sort: String
    ): Single<List<TransactionModel>>

    fun getTokenBalance(
        contractaddress: String,
        address: String
    ): Single<BaseResponse<BigDecimal>>

    fun getBalanceSingle(address: String): Single<BigDecimal>
}