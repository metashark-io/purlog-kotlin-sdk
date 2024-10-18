package com.metashark.purlog.utils

import java.util.UUID
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.io.File
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import android.util.Base64

private const val ANDROID_KEYSTORE = "AndroidKeyStore"
private const val AES_MODE = "AES/GCM/NoPadding"
private const val GCM_TAG_LENGTH = 128
private val baseDir = File(System.getProperty("user.dir"))

actual fun save(token: String, alias: String): Boolean {
    return try {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }

        if (!keyStore.containsAlias(alias)) {
            generateKey(alias)
        }

        val cipher = Cipher.getInstance(AES_MODE)
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(alias))

        val encryptionIv = cipher.iv
        val encryptionData = cipher.doFinal(token.toByteArray(Charsets.UTF_8))

        // Concatenate IV and encrypted data
        val combinedIvData = encryptionIv + encryptionData
        val encryptedData = Base64.encodeToString(combinedIvData, Base64.DEFAULT)

        // Save to file
        saveToFile(alias, encryptedData)
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

actual fun get(alias: String): String? {
    return try {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }

        if (!keyStore.containsAlias(alias)) return null

        val encryptedData = getFromFile(alias) ?: return null
        val combinedIvData = Base64.decode(encryptedData, Base64.DEFAULT)

        // Split IV and data
        val encryptionIv = combinedIvData.copyOfRange(0, 12)
        val encryptionData = combinedIvData.copyOfRange(12, combinedIvData.size)

        val cipher = Cipher.getInstance(AES_MODE)
        val spec = GCMParameterSpec(GCM_TAG_LENGTH, encryptionIv)
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(alias), spec)

        val decryptedData = cipher.doFinal(encryptionData)
        String(decryptedData, Charsets.UTF_8)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

actual fun delete(alias: String): Boolean {
    return try {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        keyStore.deleteEntry(alias)
        deleteFile(alias)
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

// Helper methods
private fun generateKey(alias: String) {
    val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
    val keyGenParameterSpec = KeyGenParameterSpec.Builder(
        alias,
        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
    )
        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
        .build()
    keyGenerator.init(keyGenParameterSpec)
    keyGenerator.generateKey()
}

private fun getSecretKey(alias: String): SecretKey {
    val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
    return keyStore.getKey(alias, null) as SecretKey
}

// File-based methods
private fun saveToFile(alias: String, value: String) {
    val file = File(baseDir, alias)
    file.writeText(value)
}

private fun getFromFile(alias: String): String? {
    val file = File(baseDir, alias)
    return if (file.exists()) file.readText() else null
}

private fun deleteFile(alias: String) {
    val file = File(baseDir, alias)
    if (file.exists()) file.delete()
}

internal actual fun registerBouncyCastle() {
}

internal actual fun createUUIDIfNotExists(): String? {
    val sessionUUID = get("PurLogSessionUUID")

    return if (sessionUUID != null) {
        // UUID exists, return it as a success
        return sessionUUID
    } else {
        // UUID does not exist, generate a new one
        val newUUID = UUID.randomUUID().toString()

        // Attempt to save the new UUID to the KeyStore
        val saveResult = save(newUUID, "PurLogSessionUUID")

        return if (saveResult) {
            // Successfully saved, return the new UUID
            newUUID
        } else {
            // Failed to save the UUID, throw an exception
            null
        }
    }
}