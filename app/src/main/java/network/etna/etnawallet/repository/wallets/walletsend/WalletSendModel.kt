package network.etna.etnawallet.repository.wallets.walletsend

import network.etna.etnawallet.sdk.api.etna.wssapi.preparetransaction.PrepareTransactionRequest

sealed class WalletSendModel(
    val platform: String,
    val receiverAddress: String
) {
    class SendCurrency(platform: String, receiverAddress: String, val value: String) : WalletSendModel(platform, receiverAddress)
    class SendERC20(platform: String, receiverAddress: String, val value: String, val tokenAddress: String) : WalletSendModel(platform, receiverAddress)
}

fun WalletSendModel.getPrepareTransactionRequest(walletAddress: String): PrepareTransactionRequest {
    return when (this) {
        is WalletSendModel.SendCurrency -> {
            PrepareTransactionRequest(
                platform,
                walletAddress,
                receiverAddress,
                value,
                null,
                null
            )
        }

        is WalletSendModel.SendERC20 -> {
            PrepareTransactionRequest(
                platform,
                walletAddress,
                receiverAddress,
                value,
                tokenAddress,
                null
            )
        }
    }
}