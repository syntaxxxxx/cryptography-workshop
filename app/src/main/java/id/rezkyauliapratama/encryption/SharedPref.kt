package id.rezkyauliapratama.encryption

import android.content.SharedPreferences

class SharedPref (
    private val sharedPreferences: SharedPreferences
){

    
    @Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
    protected fun <T> SharedPreferences.get(key: String, clazz: Class<T>): T =
        when (clazz) {
            String::class.java -> getString((key), "")
            Boolean::class.java -> getBoolean(key, false)
            Float::class.java -> getFloat(key, -1f)
            Double::class.java -> getFloat(key, -1f)
            Int::class.java -> getInt(key, -1)
            Long::class.java -> getLong(key, -1L)
            else -> null
        } as T

    protected fun <T> SharedPreferences.put(key: String, data: T) {
        val editor = edit()
        when (data) {
            is String -> editor.putString(key, data)
            is Boolean -> editor.putBoolean(key, data)
            is Float -> editor.putFloat(key, data)
            is Double -> editor.putFloat(key, data.toFloat())
            is Int -> editor.putInt(key, data)
            is Long -> editor.putLong(key, data)
        }
        editor.apply()
    }
}