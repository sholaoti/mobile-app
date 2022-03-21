package network.etna.etnawallet.repository.wallets.cryptowallet.model

class CryptoBlockchainWallet(
    val id: String,
    val blockchains: List<CryptoBlockchain>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CryptoBlockchainWallet

        if (id != other.id) return false
        if (blockchains != other.blockchains) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + blockchains.hashCode()
        return result
    }


}

class CryptoBlockchain(
    val id: String,
    val publicKey: String?,
    val address: String?,
    val tokens: List<ERCToken>
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CryptoBlockchain

        if (id != other.id) return false
        if (publicKey != other.publicKey) return false
        if (address != other.address) return false
        if (tokens != other.tokens) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (publicKey?.hashCode() ?: 0)
        result = 31 * result + (address?.hashCode() ?: 0)
        result = 31 * result + tokens.hashCode()
        return result
    }
}
