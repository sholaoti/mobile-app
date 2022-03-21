package network.etna.etnawallet.repository.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import network.etna.etnawallet.extensions.GsonBuilderOptions

class SecureDataStorageImpl(context: Context): DataStorage {

    private val sharedPreferences: SharedPreferences

    init {

        val masterKeyAlias: String = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        sharedPreferences = EncryptedSharedPreferences.create(
            "encrypted_prefs",
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    override fun put(obj: Any?, key: String, gsonBuilderOptions: GsonBuilderOptions?) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        if (obj != null) {
            val gsonBuilder = GsonBuilder()

            gsonBuilderOptions?.let {
                gsonBuilder.apply(it)
            }

            val jsonString = gsonBuilder.create().toJson(obj)
            editor.putString(key, jsonString)
        } else {
            editor.remove(key)
        }
        editor.apply()
    }

    override fun <T> get(clazz: Class<T>, key: String, gsonBuilderOptions: GsonBuilderOptions?): T? {
        sharedPreferences
            .getString(key, null)?.let {
                try {

                    val gsonBuilder = GsonBuilder()

                    gsonBuilderOptions?.let {
                        gsonBuilder.apply(it)
                    }

                    return gsonBuilder.create().fromJson(it, clazz)
                } catch (e: Exception) {
                }
            }
        return null
    }
}