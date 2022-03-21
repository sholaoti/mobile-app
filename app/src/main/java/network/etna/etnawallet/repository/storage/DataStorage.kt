package network.etna.etnawallet.repository.storage

import network.etna.etnawallet.extensions.GsonBuilderOptions

interface DataStorage {
    fun put(obj: Any?, key: String, gsonBuilderOptions: GsonBuilderOptions? = null)
    fun <T> get(clazz: Class<T>, key: String, gsonBuilderOptions: GsonBuilderOptions? = null): T?
}
