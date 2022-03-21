package network.etna.etnawallet.sdk.api

import java.util.*
import kotlin.collections.HashMap

object ImageProvider {

    private val imageURLs: MutableMap<String, String> = HashMap()

    fun getImageURL(id: String, size: Int): String {
        val key = "$id$size"
        var imageURL = imageURLs[key]

        if (imageURL == null) {
            imageURL = "https://wlt-api.etna.network/images/${id}/currency/$size/icon.png?clear=${UUID.randomUUID()}"
            imageURLs[key] = imageURL
        }
        return imageURL
    }

    fun getTokenURL(id: String, platformId: String, size: Int): String {
        val key = "$id$size"
        var imageURL = imageURLs[key]

        if (imageURL == null) {
            ///images/:platformId/contract/:contractAddress/:size/icon.png
            imageURL = "https://wlt-api.etna.network/images/$platformId/contract/$id/$size/icon.png?clear=${UUID.randomUUID()}"
            imageURLs[key] = imageURL
        }
        return imageURL
    }

    fun getPlatformImageURL(id: String, size: Int): String {
        val key = "$id$size"
        var imageURL = imageURLs[key]

        if (imageURL == null) {
            imageURL = "https://wlt-api.etna.network/images/${id}/$size/icon.png?clear=${UUID.randomUUID()}"
            imageURLs[key] = imageURL
        }
        return imageURL
    }
}