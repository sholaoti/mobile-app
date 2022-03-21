package network.etna.etnawallet.repository.wallets.etnawallet

import com.google.gson.Gson

class AssetIdHolder(
    val isCurrency: Boolean,
    val platform: String,
    val walletAddress: String,
    val assetId: String,
    val referenceAssetId: String
) {

    fun getMarketPlatform(): String {
        return if (isCurrency) {
            referenceAssetId
        } else {
            platform
        }
    }
    fun getContractAddress(): String? {
        if (isCurrency) {
            return null
        }
        return referenceAssetId
    }
}

fun Any.toJsonString(): String {
    return Gson().toJson(this)
}

fun <T> String.fromJsonString(clazz: Class<T>): T? {
    return Gson().fromJson(this, clazz)
}