package network.etna.etnawallet.repository.wallets.cryptowallet.model

import com.google.gson.*
import java.lang.reflect.Type

sealed class ERCToken(
    val tokenAddress: String,
    val type: ERCTokenType,
    val name: String,
    val symbol: String,
    var isVisible: Boolean
) {

    class ERC20(
        tokenAddress: String,
        type: ERCTokenType,
        name: String,
        symbol: String,
        isVisible: Boolean,
        val decimals: Int
    ) : ERCToken(tokenAddress, type, name, symbol, isVisible) {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ERC20

            if (tokenAddress != other.tokenAddress) return false

            return true
        }

        override fun hashCode(): Int {
            return tokenAddress.hashCode()
        }

    }

    class ERC721(
        tokenAddress: String,
        type: ERCTokenType,
        name: String,
        symbol: String,
        isVisible: Boolean,
        val tokenId: String,
        val tokenURI: String,
        val owner: String
    ) : ERCToken(tokenAddress, type, name, symbol, isVisible) {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ERC721

            if (tokenAddress != other.tokenAddress) return false

            if (tokenId != other.tokenId) return false

            return true
        }

        override fun hashCode(): Int {
            return (tokenAddress+tokenId).hashCode()
        }

        val uniqueId: String
            get() = tokenAddress + tokenId

    }
}

enum class ERCTokenType {
    erc20, erc721
}

class ERCTokenJsonSerializer: JsonSerializer<ERCToken> {
    override fun serialize(
        src: ERCToken,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        val obj = JsonObject()

        obj.addProperty("tokenAddress", src.tokenAddress)
        obj.addProperty("type", src.type.name)
        obj.addProperty("name", src.name)
        obj.addProperty("symbol", src.symbol)
        obj.addProperty("isVisible", src.isVisible)

        when (src) {
            is ERCToken.ERC20 -> {
                obj.addProperty("decimals", src.decimals)
            }
            is ERCToken.ERC721 -> {
                obj.addProperty("tokenId", src.tokenId)
                obj.addProperty("tokenURI", src.tokenURI)
                obj.addProperty("owner", src.owner)
            }
        }
        return obj
    }
}

class ERCTokenJsonDeserializer: JsonDeserializer<ERCToken> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): ERCToken {
        val jsonObject = json.asJsonObject

        val isVisible: Boolean =
            if (jsonObject.has("isVisible"))
                jsonObject.get("isVisible").asBoolean
            else
                true

        val tokenAddress = jsonObject.get("tokenAddress").asString
        val type = ERCTokenType.valueOf(jsonObject.get("type").asString)
        val name = jsonObject.get("name").asString
        val symbol = jsonObject.get("symbol").asString

        return when (jsonObject.get("type").asString) {
            "erc20" -> {
                ERCToken.ERC20(
                    tokenAddress,
                    type,
                    name,
                    symbol,
                    isVisible,
                    jsonObject.get("decimals").asInt
                )
            }
            "erc721" -> {
                ERCToken.ERC721(
                    tokenAddress,
                    type,
                    name,
                    symbol,
                    isVisible,
                    jsonObject.get("tokenId").asString,
                    jsonObject.get("tokenURI").asString,
                    jsonObject.get("owner").asString
                )
            }

            else -> throw Exception("Unsupported ERCToken type")
        }
    }
}