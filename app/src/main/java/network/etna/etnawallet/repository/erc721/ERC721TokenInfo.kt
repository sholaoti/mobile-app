package network.etna.etnawallet.repository.erc721

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class ERC721TokenInfo(
    val name: String?,
    val description: String?,
    val image: String?
)


class ERC721TokenInfoJsonDeserializer: JsonDeserializer<ERC721TokenInfo> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): ERC721TokenInfo {
        val jsonObject = json.asJsonObject

        var name: String? = null
        var description: String? = null
        var image: String? = null

        for (key in jsonObject.keySet()) {
            when (key.lowercase()) {
                "name" -> name = jsonObject.get(key).asString
                "description" -> description = jsonObject.get(key).asString
                "image" -> image = jsonObject.get(key).asString
            }
        }
        return ERC721TokenInfo(
            name,
            description,
            image
        )
    }
}