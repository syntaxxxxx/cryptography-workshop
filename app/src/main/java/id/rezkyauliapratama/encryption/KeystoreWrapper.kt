package id.rezkyauliapratama.encryption

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.lang.IllegalStateException
import java.math.BigInteger
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.util.*
import javax.security.auth.x500.X500Principal

class KeystoreWrapper(private val context: Context) {

    companion object {
        private const val SIZE_KEY = 2048
        private const val AMOUNT_DURATION = 30
        private const val keystoreAliasName = "AndroidAlias"
        private const val KEYSTORE_TYPE = "AndroidKeyStore"
        private const val RSA_ALGORITHM = "RSA"
    }

    /**
     * Creates asymmetric RSA key with default [KeyProperties.BLOCK_MODE_ECB] and
     * [KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1] and saves it to Android Key Store.
     */
    fun createAndroidKeyStoreAsymmetricKey(): KeyPair? {
        val generator = KeyPairGenerator.getInstance(RSA_ALGORITHM, KEYSTORE_TYPE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            initGeneratorWithKeyGenParameterSpec(generator)
        } else {
            initGeneratorWithKeyPairGeneratorSpec(generator)
        }

        return try {
            generator.generateKeyPair()
        } catch (e: IllegalStateException) {
            null
        }
    }

    //used in createAndroidKeyStoreAsymmetricKey() method
    @TargetApi(Build.VERSION_CODES.M)
    private fun initGeneratorWithKeyGenParameterSpec(generator: KeyPairGenerator) {
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            keystoreAliasName,
            //purpose
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).run {
            setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP) //(padding) use RSA Optimal Asymmetric Encryption Padding (OAEP) scheme
            setDigests(KeyProperties.DIGEST_SHA256) //using hash SHA256
            setKeySize(SIZE_KEY)
            build()
        }
        generator.initialize(keyGenParameterSpec)
    }

    //used in createAndroidKeyStoreAsymmetricKey() method
    private fun initGeneratorWithKeyPairGeneratorSpec(generator: KeyPairGenerator) {
        val start = Calendar.getInstance()
        val end = Calendar.getInstance()
        end.add(Calendar.YEAR, AMOUNT_DURATION)
        val keyGenParameterSpec = KeyPairGeneratorSpec.Builder(context).run {
            setAlias(keystoreAliasName)
            setSubject(X500Principal("CN=$keystoreAliasName"))
            setSerialNumber(BigInteger.TEN)
            setStartDate(start.time) //when the key can be used
            setEndDate(end.time) //when the key will be expired
            build()
        }
        generator.initialize(keyGenParameterSpec)
    }

    private fun createAndroidKeyStore(): KeyStore {
        val keyStore = KeyStore.getInstance(KEYSTORE_TYPE)
        keyStore.load(null)
        return keyStore
    }

}