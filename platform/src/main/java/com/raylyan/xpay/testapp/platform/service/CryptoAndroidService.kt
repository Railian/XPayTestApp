package com.raylyan.xpay.testapp.platform.service

import android.annotation.TargetApi
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.raylyan.xpay.testapp.domain.service.CryptoSystemService
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.GCMParameterSpec

class CryptoAndroidService : CryptoSystemService {

    companion object {
        const val KEY_ALIAS = "XPay"
        const val AES_MODE = "AES/GCM/NoPadding"
        const val KEY_STORE_PROVIDER = "AndroidKeyStore"
        val FIXED_IV = "fixed_direct".toByteArray()
    }

    private val keyStore: KeyStore = createKeyStore()

    override fun encrypt(string: String): String {
        val bytes = string.toByteArray(Charsets.UTF_8)
        val cipher = getCipher(Cipher.ENCRYPT_MODE)
        val encodedBytes = cipher.doFinal(bytes)
        return Base64.encodeToString(encodedBytes, Base64.DEFAULT)
    }

    override fun decrypt(string: String): String {
        val decode = Base64.decode(string, Base64.DEFAULT)
        val cipher = getCipher(Cipher.DECRYPT_MODE)
        val decodedBytes = cipher.doFinal(decode)
        return String(decodedBytes, Charsets.UTF_8)
    }

    private fun getCipher(operationMode: Int): Cipher {
        val cipher = Cipher.getInstance(AES_MODE)
        val key = keyStore.getKey(KEY_ALIAS, null)
        cipher.init(operationMode, key, GCMParameterSpec(128, FIXED_IV))
        return cipher
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun createKeyStore(): KeyStore {
        val keyStore = KeyStore.getInstance(KEY_STORE_PROVIDER)
        keyStore.load(null) // null is ok for AndroidKeyStore

        if (!keyStore.containsAlias(KEY_ALIAS)) {
            val keyGenerator: KeyGenerator = KeyGenerator
                    .getInstance(KeyProperties.KEY_ALGORITHM_AES, KEY_STORE_PROVIDER)

            val purposes = KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            val keygenParameterSpec = KeyGenParameterSpec.Builder(KEY_ALIAS, purposes)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setRandomizedEncryptionRequired(false)
                    .build()
            keyGenerator.init(keygenParameterSpec)
            keyGenerator.generateKey()
        }
        return keyStore
    }
}