package network.etna.etnawallet.sdk.api.images

class ImagesAPIImpl(
    val baseURL: String
) {

    //baseUrl/images/tokens/:platformId/:contractAddress/:width/:height/icon.png
    fun getTokenIconURL(): String {
        return ""
    }
}