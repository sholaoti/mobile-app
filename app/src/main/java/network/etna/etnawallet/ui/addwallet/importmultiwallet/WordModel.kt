package network.etna.etnawallet.ui.addwallet.importmultiwallet

data class WordModel(
    val word: String,
    var status: WordStatus,
    val isDeletable: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WordModel

        if (word != other.word) return false
        if (status != other.status) return false
        if (isDeletable != other.isDeletable) return false

        return true
    }
}

enum class WordStatus {
    NORMAL, ERROR, SUCCESS
}