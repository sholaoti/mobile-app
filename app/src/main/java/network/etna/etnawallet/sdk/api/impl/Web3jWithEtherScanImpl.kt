package network.etna.etnawallet.sdk.api.impl

import network.etna.etnawallet.sdk.api.NetworkAPI
import network.etna.etnawallet.sdk.api.etherscan.BaseResponse
import network.etna.etnawallet.sdk.api.etherscan.EtherScanAPI
import network.etna.etnawallet.sdk.api.models.TransactionModel
import io.reactivex.Single
import org.koin.core.component.KoinComponent
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import java.math.BigDecimal

class Web3jWithEtherScanImpl(
    private val web3j: Web3j,
    private val etherScanAPI: EtherScanAPI
): KoinComponent, NetworkAPI {

    override fun listTransactions(
        address: String,
        startblock: Int,
        endblock: Int,
        sort: String
    ): Single<List<TransactionModel>> {
        return etherScanAPI
            .listTransactions(address, startblock, endblock, sort)
            .map { it.result }
    }

    override fun listERC20Transactions(
        address: String,
        startblock: Int,
        endblock: Int,
        sort: String
    ): Single<List<TransactionModel>> {
        return etherScanAPI
            .listERC20Transactions(address, startblock, endblock, sort)
            .map { it.result }
    }

    override fun getBalanceSingle(address: String): Single<BigDecimal> {
        return Single.create { single ->
            try {
                val balance = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send().balance
                val decimal = BigDecimal(balance)
                single.onSuccess(decimal)
            } catch (e: Exception) {
                single.onError(e)
            }
        }
    }

    override fun getTokenBalance(
        contractaddress: String,
        address: String
    ): Single<BaseResponse<BigDecimal>> {
        return etherScanAPI
            .getTokenBalance(
                contractaddress,
                address
            )
    }
}