package network.etna.etnawallet.sdk.api.models

data class TransactionModel(
    val hash: String,
    val blockNumber: String,
    val transactionIndex: String,
    val from: String,
    val to: String,
    val value: String,
    val gas: String,
    val gasPrice: String,
    val gasUsed: String,
    val confirmations: String,
    val timeStamp: String,
    val contractAddress: String
)