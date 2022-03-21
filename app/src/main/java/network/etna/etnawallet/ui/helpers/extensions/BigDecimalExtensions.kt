package network.etna.etnawallet.ui.helpers.extensions

import network.etna.etnawallet.repository.wallets.CurrencyAmount
import network.etna.etnawallet.sdk.network.symbol
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.*
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.absoluteValue


fun BigDecimal.formatAmount(): String {

    val numberFormat = NumberFormat.getCurrencyInstance(Locale("en", "US"))
    var str: String = numberFormat.format(this.toDouble())
    numberFormat.currency?.symbol?.let {
        str = str.replace(it, "$it ")
    }

    return str
}

data class FormatAmountString(
    val isPositive: Boolean,
    val text: String
)

fun BigDecimal.format24Change(): FormatAmountString {

    val isPositive = this > BigDecimal.ZERO
    val abs = this.abs()

    var str = abs.formatAmount()

    str = if (isPositive) "+ $str" else "- $str"
    str = "$str / 24h"

    return FormatAmountString(
        isPositive,
        str
    )
}

fun CurrencyAmount.formatAmount(precision: Int? = null): String {

    val numberFormat = NumberFormat.getCurrencyInstance(Locale("en", "US"))

    precision?.let {
        numberFormat.minimumFractionDigits = it
        numberFormat.maximumFractionDigits = it
    }

    var str: String = numberFormat.format(amount.toDouble())
    numberFormat.currency?.symbol?.let {
        str = str.replace(it, "${currency.symbol} ")
    }

    return str
}

fun CurrencyAmount.format24Change(): FormatAmountString {

    val isPositive = amount > BigDecimal.ZERO

    var str = CurrencyAmount(amount.abs(), currency).formatAmount()

    str = if (isPositive) "+ $str" else "- $str"
    str = "$str / 24h"

    return FormatAmountString(
        isPositive,
        str
    )
}

fun Double.formatAmountRounded(addApproximator: Boolean = true, maximumFractionDigits: Int = 5): String {
    val numberFormat = NumberFormat.getCurrencyInstance(Locale("en", "US"))
    numberFormat.minimumFractionDigits = 0
    numberFormat.maximumFractionDigits = 20
    var str: String = numberFormat.format(this)
    numberFormat.currency?.symbol?.let {
        str = str.replace(it, "")
    }

    val numberFormatRounded = NumberFormat.getCurrencyInstance(Locale("en", "US"))
    numberFormatRounded.minimumFractionDigits = 0
    numberFormatRounded.maximumFractionDigits = maximumFractionDigits
    var strRounded: String = numberFormatRounded.format(this)
    numberFormatRounded.currency?.symbol?.let {
        strRounded = strRounded.replace(it, "")
    }

    if (addApproximator && str != strRounded) {
        strRounded = "≈$strRounded"
    }

    return strRounded
}

fun Double.formatAmount(): String {

    val numberFormat = NumberFormat.getCurrencyInstance(Locale("en", "US"))
    numberFormat.minimumFractionDigits = 0
    numberFormat.maximumFractionDigits = 20
    var str: String = numberFormat.format(this)
    numberFormat.currency?.symbol?.let {
        str = str.replace(it, "")
    }

    return str
}

fun Double.format24Change(addSign: Boolean = false, maximumFractionDigits: Int = 4): String {

    val sign = when {
        addSign && this > 0 -> "+ "
        addSign && this < 0 -> "- "
        else -> ""
    }

    val numberFormat = DecimalFormat.getInstance(Locale.GERMAN)
    numberFormat.minimumFractionDigits = 0
    numberFormat.maximumFractionDigits = maximumFractionDigits
    numberFormat.roundingMode = RoundingMode.DOWN

    val value = numberFormat.format(this.absoluteValue)

    return "$sign$value%"
}
