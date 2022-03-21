package network.etna.etnawallet.repository.wallets.cryptowalletcredentials

import com.google.gson.JsonObject

class SignResult(
    val toSign: String,
    val signature: Signature,
    val publicKey: String
)

class Signature(
    val r: String,
    val s: String
)

fun Signature.toJsonObject(): JsonObject {
    val result = JsonObject()
    result.addProperty("r", r)
    result.addProperty("s", s)
    return result
}