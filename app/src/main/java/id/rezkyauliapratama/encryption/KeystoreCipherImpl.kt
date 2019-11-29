package id.rezkyauliapratama.encryption

import android.content.Context
import android.util.Base64
import java.security.KeyPair
import java.security.spec.MGF1ParameterSpec
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource
import javax.crypto.spec.SecretKeySpec

class KeystoreCipherImpl(private val context: Context) {

    companion object {
        private const val AES_ALGORITHM = "AES"
        private const val AES_TRANSFORMATION = "AES/CBC/PKCS5Padding"
        private const val SHA_256 = "SHA-256"
        private const val SHA_1 = "SHA-1"
        private const val MGF = "MGF1"
        private const val RSA_TRANSFORMATION = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding"
    }

    //KeyPair that contains Public key and Private key
    private var masterKeyAsymmetric: KeyPair?
    private var aesKey: ByteArray? = null
    private var aesVectorSpecs: ByteArray? = null

    val keystoreWrapper: KeystoreWrapper by lazy {
        KeystoreWrapper(context)
    }

    init {
        // check if key with alias AndroidAlias exist or not it keystore (we only need to generate 1 time)
        if (!keystoreWrapper.isAndroidKeyStoreAsymmetricKeyExist()) {
            keystoreWrapper.createAndroidKeyStoreAsymmetricKey()
        }

        masterKeyAsymmetric = keystoreWrapper.androidKeyStoreAsymmetricKeyPair()

        //assign value for var aesKey and aesVectorSpecs
        val aesSpec = keystoreWrapper.createDefaultSymmetricKey()
        aesKey = aesSpec[KeystoreWrapper.AES_MASTER_KEY]
        aesVectorSpecs = aesSpec[KeystoreWrapper.AES_VECTOR_KEY]
    }

    fun decrypt(encrypted: String): String {
        if (encrypted.isEmpty()) return ""

        return try {
            val aesCipher = Cipher.getInstance(AES_TRANSFORMATION)
            val iv = IvParameterSpec(aesVectorSpecs)
            val skeySpec = SecretKeySpec(aesKey, AES_ALGORITHM)

            aesCipher.init(Cipher.DECRYPT_MODE, skeySpec, iv)

            val original = aesCipher.doFinal(encrypted.decodeToByteArray())
            String(original, Charsets.UTF_8)
        } catch (e: Exception) {
            ""
        }

    }

    fun encrypt(value: String): String {
        if (value.isEmpty()) return ""

        return try {
            val aesCipher = Cipher.getInstance(AES_TRANSFORMATION)
            val iv = IvParameterSpec(aesVectorSpecs)
            val skeySpec = SecretKeySpec(aesKey, AES_ALGORITHM)
            aesCipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv)

            val encrypted = aesCipher.doFinal(value.toByteArray(Charsets.UTF_8))
            encrypted.encodeToString()
        } catch (e: Exception) {
            ""
        }

    }

    //method to encrypt plain text
    fun encryptRSA(value: ByteArray?): String? {
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
    fun decryptRSA(value: String): ByteArray? {
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
}

fun String.decodeToByteArray(): ByteArray = Base64.decode(this, Base64.NO_WRAP)

fun ByteArray.encodeToString(): String = Base64.encodeToString(this, Base64.NO_WRAP)