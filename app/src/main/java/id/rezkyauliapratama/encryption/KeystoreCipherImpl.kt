package id.rezkyauliapratama.encryption

import android.content.Context
import java.security.KeyPair

class KeystoreCipherImpl(private val context: Context) {

    //KeyPair that contains Public key and Private key
    private var masterKeyAsymmetric: KeyPair?

    val keystoreWrapper: KeystoreWrapper by lazy {
        KeystoreWrapper(context)
    }

    init {
        if (!keystoreWrapper.isAndroidKeyStoreAsymmetricKeyExist()) {
            keystoreWrapper.createAndroidKeyStoreAsymmetricKey()
        }

        masterKeyAsymmetric = keystoreWrapper.androidKeyStoreAsymmetricKeyPair()
    }
}