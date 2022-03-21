package network.etna.etnawallet.repository.wallets.cryptowalletcredentials

import com.google.gson.*
import java.lang.reflect.Type

sealed class PublicCredential {
    class PublicKey(val value: String) : PublicCredential()
    class Address(val value: String) : PublicCredential()
}

sealed class CryptoWalletCredentials {
    class Seed(val value: String) : CryptoWalletCredentials()
    class Address(val value: String) : CryptoWalletCredentials()
    class PrivateKey(val value: String) : CryptoWalletCredentials()
}

class CryptoWalletCredentialsJsonSerializer: JsonSerializer<CryptoWalletCredentials> {
    override fun serialize(
        src: CryptoWalletCredentials,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        val obj = JsonObject()
        val element = JsonObject()
        when (src) {
            is CryptoWalletCredentials.Seed -> {
                obj.addProperty("value", src.value)
                element.add("Seed", obj)
            }
            is CryptoWalletCredentials.Address -> {
                obj.addProperty("value", src.value)
                element.add("Address", obj)
            }
            is CryptoWalletCredentials.PrivateKey -> {
                obj.addProperty("value", src.value)
                element.add("PrivateKey", obj)
            }
        }
        return element
    }
}

class CryptoWalletCredentialsJsonDeserializer: JsonDeserializer<CryptoWalletCredentials> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): CryptoWalletCredentials {
        val jsonObject = json.asJsonObject
        if (jsonObject.has("Seed")) {
            return CryptoWalletCredentials.Seed(jsonObject.get("Seed").asJsonObject.get("value").asString)
        }
        if (jsonObject.has("Address")) {
            return CryptoWalletCredentials.Address(jsonObject.get("Address").asJsonObject.get("value").asString)
        }
        if (jsonObject.has("PrivateKey")) {
            return CryptoWalletCredentials.PrivateKey(jsonObject.get("PrivateKey").asJsonObject.get("value").asString)
        }
        throw Exception("Unsupported CryptoWalletCredentials type")
    }
}