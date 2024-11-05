package io.metashark.purlog.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

internal object SecureStorageManager {
    private const val PREF_NAME = "PurLogSecurePrefs"
    private lateinit var sharedPreferences: SharedPreferences

    // Internal initialization function to set up EncryptedSharedPreferences
    fun initialize(context: Context) {
        if (!::sharedPreferences.isInitialized) {
            val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
            sharedPreferences = EncryptedSharedPreferences.create(
                PREF_NAME,
                masterKeyAlias,
                context.applicationContext,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }
    }

    fun saveToken(token: String, alias: String) {
        if (!::sharedPreferences.isInitialized) {
            throw IllegalStateException("SecureStorageManager is not initialized. Call initialize(context) first.")
        }
        sharedPreferences.edit().putString(alias, token).apply()
    }

    fun getToken(alias: String): String? {
        if (!::sharedPreferences.isInitialized) {
            throw IllegalStateException("SecureStorageManager is not initialized. Call initialize(context) first.")
        }
        return sharedPreferences.getString(alias, null)
    }

    fun deleteToken(alias: String): Boolean {
        if (!::sharedPreferences.isInitialized) {
            throw IllegalStateException("SecureStorageManager is not initialized. Call initialize(context) first.")
        }
        sharedPreferences.edit().remove(alias).apply()
        return true
    }
}