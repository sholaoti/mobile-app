package network.etna.etnawallet.extensions

import android.content.Context
import android.text.format.DateUtils
import network.etna.etnawallet.R
import java.text.SimpleDateFormat
import java.util.*

fun Date.prettyPrint(context: Context): String {
    return when {
        isToday() -> {
            val simpleDateFormat = SimpleDateFormat("HH:mm:ss", Locale("en", "US"))
            context.resources.getString(R.string.date_format_today, simpleDateFormat.format(this))
        }
        isYesterday() -> {
            val simpleDateFormat = SimpleDateFormat("HH:mm:ss", Locale("en", "US"))
            context.resources.getString(R.string.date_format_yesterday, simpleDateFormat.format(this))
        }
        else -> {
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("en", "US"))
            simpleDateFormat.format(this)
        }
    }
}

fun Date.isYesterday(): Boolean {
    val now = Calendar.getInstance()
    val cdate = Calendar.getInstance()
    cdate.time = this
    now.add(Calendar.DATE, -1)
    return now.get(Calendar.YEAR) == cdate.get(Calendar.YEAR) &&
            now.get(Calendar.MONTH) == cdate.get(Calendar.MONTH) &&
            now.get(Calendar.DATE) == cdate.get(Calendar.DATE)
}

fun Date.isToday(): Boolean {
    return DateUtils.isToday(time)
}

fun Date.isSameDay(date: Date): Boolean {
    val calendar1 = Calendar.getInstance()
    calendar1.time = this
    val calendar2 = Calendar.getInstance()
    calendar2.time = date
    return calendar1[Calendar.YEAR] == calendar2[Calendar.YEAR]
            && calendar1[Calendar.MONTH] == calendar2[Calendar.MONTH]
            && calendar1[Calendar.DAY_OF_MONTH] == calendar2[Calendar.DAY_OF_MONTH]
}
