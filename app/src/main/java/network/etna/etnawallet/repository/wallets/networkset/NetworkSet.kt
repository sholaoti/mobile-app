package network.etna.etnawallet.repository.wallets.networkset

enum class NetworkSet {
    MAINNET,
    TESTNET
}

class NetworkSetModel(
    val network: NetworkSet,
    val isActive: Boolean
)