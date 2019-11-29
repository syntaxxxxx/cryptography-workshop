package id.rezkyauliapratama.encryption

import android.content.Context
import android.util.Base64
import java.security.KeyPair
import java.security.spec.MGF1ParameterSpec
import javax.crypto.Cipher
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource

class KeystoreCipherImpl(private val context: Context) {

    companion object {
        private const val SHA_256 = "SHA-256"
        private const val SHA_1 = "SHA-1"
        private const val MGF = "MGF1"
        private const val RSA_TRANSFORMATION = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding"
    }

    //KeyPair that contains Public key and Private key
    private var masterKeyAsymmetric: KeyPair?

    val keystoreWrapper: KeystoreWrapper by lazy {
        KeystoreWrapper(context)
    }

    init {
        // check if key with alias AndroidAlias exist or not it keystore (we only need to generate 1 time)
        if (!keystoreWrapper.isAndroidKeyStoreAsymmetricKeyExist()) {
            keystoreWrapper.createAndroidKeyStoreAsymmetricKey()
        }

        masterKeyAsymmetric = keystoreWrapper.androidKeyStoreAsymmetricKeyPair()
    }

    //method to encrypt plain text
    private fun encryptRSA(value: ByteArray?): String? {
        if (value == null || masterKeyAsymmetric?.public == null) {
            return null
        }
        val rsaCipher = Cipher.getInstance(RSA_TRANSFORMATION)
        val publicKey = masterKeyAsymmetric?.public

        val encryptedByteArray = rsaCipher.apply {
            init(Cipher.ENCRYPT_MODE, publicKey, sp)
        }.doFinal(value)


        return encryptedByteArray.encodeToString()
    }

    //method to decrypt cipher text
    private fun decryptRSA(value: String): ByteArray? {
        if (masterKeyAsymmetric?.private == null) {
            return null
        }
        val rsaCipher = Cipher.getInstance(RSA_TRANSFORMATION)
        val privateKey = masterKeyAsymmetric?.private

        val byteArray = value.decodeToByteArray()

        return rsaCipher.apply {
            init(Cipher.DECRYPT_MODE, privateKey, sp)
        }.doFinal(byteArray)
    }

    private val sp by lazy {
        OAEPParameterSpec(SHA_256, MGF, MGF1ParameterSpec(SHA_1), PSource.PSpecified.DEFAULT)
    }

    private fun String.decodeToByteArray(): ByteArray = Base64.decode(this, Base64.DEFAULT)

    private fun ByteArray.encodeToString(): String = Base64.encodeToString(this, Base64.DEFAULT)
}