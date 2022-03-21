package network.etna.etnawallet.repository.warningservice

import java.util.*

enum class IssueItemType {
    WARNING, ERROR
}

class IssueItem(
    val id: String,
    val type: IssueItemType,
    val text: String,
    val expiryDate: Date?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IssueItem

        if (id != other.id) return false
        if (type != other.type) return false
        if (text != other.text) return false
        if (expiryDate != other.expiryDate) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + text.hashCode()
        result = 31 * result + expiryDate.hashCode()
        return result
    }
}