package network.etna.etnawallet.extensions

import com.google.gson.GsonBuilder
import io.reactivex.Single
import java.lang.reflect.Type
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

typealias GsonBuilderOptions = GsonBuilder.() -> Unit

fun <T> String.convert(typeOfT: Type, gsonBuilderOptions: GsonBuilderOptions? = null): Single<T> {
    return Single.create { single ->
        try {

            val gsonBuilder = GsonBuilder()

            gsonBuilderOptions?.let {
                gsonBuilder.apply(it)
            }

            val obj = gsonBuilder.create().fromJson<T>(this, typeOfT)
            single.onSuccess(obj!!)
        } catch (e: Exception) {
            single.onError(e)
        }
    }
}

fun <T> String.convert(clazz: Class<T>, gsonBuilderOptions: GsonBuilderOptions? = null): Single<T> {
    return Single.create { single ->
        try {
            val gsonBuilder = GsonBuilder()

            gsonBuilderOptions?.let {
                gsonBuilder.apply(it)
            }

            val obj = gsonBuilder.create().fromJson(this, clazz)
            single.onSuccess(obj!!)
        } catch (e: Exception) {
            single.onError(e)
        }
    }
}

fun Number.prettyPrint(): String {

    if (this.toDouble() > 1000) {
        val suffix = arrayOf(' ', 'k', 'M', 'B', 'T', 'P', 'E')
        val numValue = this.toLong()
        val value = floor(log10(this.toDouble()))
        val base = (value / 3).toInt()
        return if (value >= 3 && base < suffix.size) {
            DecimalFormat("#0.#").format(numValue / 10.toDouble().pow(base * 3)) + suffix[base]
        } else {
            DecimalFormat("#.#").format(numValue)
        }
    } else if (this.toDouble() >= 10) {
        return this.toInt().toString()
    } else {
        val numberFormat = DecimalFormat.getInstance(Locale.GERMAN)
        numberFormat.minimumFractionDigits = 2
        numberFormat.maximumFractionDigits = 2
        numberFormat.roundingMode = RoundingMode.DOWN
        return numberFormat.format(this.toDouble())
    }
}
