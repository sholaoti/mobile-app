package network.etna.etnawallet.extensions

import com.google.gson.Gson
import io.reactivex.Single
import org.json.JSONObject
import java.lang.reflect.Type

fun <T> JSONObject.convert(typeOfT: Type): Single<T> {
    return Single.create { single ->
        try {
            val obj = Gson().fromJson<T>(this.toString(), typeOfT)
            single.onSuccess(obj!!)
        } catch (e: Exception) {
            single.onError(e)
        }
    }
}

fun <T> JSONObject.convert(clazz: Class<T>): Single<T> {
    return Single.create { single ->
        try {
            val obj = Gson().fromJson(this.toString(), clazz)
            single.onSuccess(obj!!)
        } catch (e: Exception) {
            single.onError(e)
        }
    }
}