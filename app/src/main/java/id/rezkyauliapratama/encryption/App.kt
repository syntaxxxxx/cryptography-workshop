package id.rezkyauliapratama.encryption

import android.app.Application
import android.content.Context

class App : Application() {

    private lateinit var cipherImpl: KeystoreCipherImpl
    private lateinit var sharedPref: SharedPref

    override fun onCreate() {
        super.onCreate()
        //initialize cipherImpl
        cipherImpl = KeystoreCipherImpl(this)
        val sharedPreferences = getSharedPreferences(SharedPref.SECURED_PREFS_NAME, Context.MODE_PRIVATE)
         sharedPref = SharedPref(sharedPreferences,cipherImpl)
    }

    //Singleton , only 1 instance exist
    fun getCipherImpl(): KeystoreCipherImpl {
        return cipherImpl
    }

    //Singleton , only 1 instance exist
    fun getSharedPref(): SharedPref {
        return sharedPref
    }

}