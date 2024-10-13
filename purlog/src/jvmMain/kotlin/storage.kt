package com.metashark.purlog.utils

import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import java.util.Base64
import java.util.UUID

private val keyStore: KeyStore = KeyStore.getInstance("JCEKS").apply {
    load(null, null) // Load the default keystore (or create a new one in memory)
}

private fun getOrCreateSecretKey(alias: String): SecretKey {
    return getSecretKey(alias) ?: generateSecretKey(alias)
}

private fun generateSecretKey(alias: String): SecretKey {
    val keyGenerator = KeyGenerator.getInstance("AES")
    keyGenerator.init(256) // Key size
    val secretKey = keyGenerator.generateKey()

    // Store the secret key in the Java Keystore
    val keyStoreEntry = KeyStore.SecretKeyEntry(secretKey)
    keyStore.setEntry(alias, keyStoreEntry, null)
    return secretKey
}

private fun getSecretKey(alias: String): SecretKey? {
    return keyStore.getEntry(alias, null)?.let { it as KeyStore.SecretKeyEntry }?.secretKey
}

private fun saveToSecureStorage(alias: String, encryptedData: ByteArray, iv: ByteArray) {
    // Example implementation using Base64 encoding and storing in a map (replace with secure storage logic)
    val base64EncryptedData = Base64.getEncoder().encodeToString(encryptedData)
    val base64Iv = Base64.getEncoder().encodeToString(iv)
    storageMap[alias] = Pair(base64EncryptedData, base64Iv)
}

private fun getFromSecureStorage(alias: String): Pair<ByteArray, ByteArray>? {
    // Retrieve the encrypted data and IV from storage
    val storedValues = storageMap[alias] ?: return null
    val encryptedData = Base64.getDecoder().decode(storedValues.first)
    val iv = Base64.getDecoder().decode(storedValues.second)
    return Pair(encryptedData, iv)
}

// Simulated storage map for demonstration purposes (replace with secure storage mechanism)
private val storageMap = mutableMapOf<String, Pair<String, String>>()

internal actual fun save(token: String, alias: String): Boolean {
    try {
        val secretKey = getOrCreateSecretKey(alias)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)

        val encryptedData = cipher.doFinal(token.toByteArray(Charsets.UTF_8))
        val iv = cipher.iv

        // Save the encrypted data and IV to secure storage
        saveToSecureStorage(alias, encryptedData, iv)
        return true
    } catch (e: Exception) {
        e.printStackTrace()
        return false
    }
}

internal actual fun get(alias: String): String? {
    try {
        val (encryptedData, iv) = getFromSecureStorage(alias) ?: return null

        val secretKey = getSecretKey(alias) ?: return null
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(128, iv))

        val decryptedData = cipher.doFinal(encryptedData)
        return String(decryptedData, Charsets.UTF_8)
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}

internal actual fun delete(alias: String): Boolean {
    try {
        keyStore.deleteEntry(alias)
        return true
    } catch (e: Exception) {
        e.printStackTrace()
        return false
    }
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