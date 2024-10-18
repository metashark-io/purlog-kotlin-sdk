package com.metashark.purlog.utils

import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.KeyStore
import java.security.Security
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import java.util.Base64
import java.util.UUID

// Initialize the Java Keystore
private val keyStore: KeyStore = KeyStore.getInstance("BKS", "BC").apply {
    load(null, null) // Load or create a new keystore in memory
}

// Create or get an existing SecretKey (256-bit AES key)
private fun getOrCreateSecretKey(alias: String): SecretKey {
    return getSecretKey(alias) ?: generateSecretKey(alias)
}

// Generate a new SecretKey using BouncyCastle's KeyGenerator
private fun generateSecretKey(alias: String): SecretKey {
    val keyGenerator = KeyGenerator.getInstance("AES", "BC") // Use BouncyCastle as the provider
    keyGenerator.init(256) // Use 256-bit AES encryption
    val secretKey = keyGenerator.generateKey()

    // Store the secret key in the Java Keystore
    val keyStoreEntry = KeyStore.SecretKeyEntry(secretKey)
    keyStore.setEntry(alias, keyStoreEntry, null)
    return secretKey
}

// Retrieve the SecretKey from the Java Keystore
private fun getSecretKey(alias: String): SecretKey? {
    return keyStore.getEntry(alias, null)?.let { it as KeyStore.SecretKeyEntry }?.secretKey
}

// Save encrypted data and IV (you can replace this with file or other storage mechanisms)
private fun saveToSecureStorage(alias: String, encryptedData: ByteArray, iv: ByteArray) {
    val base64EncryptedData = Base64.getEncoder().encodeToString(encryptedData)
    val base64Iv = Base64.getEncoder().encodeToString(iv)
    storageMap[alias] = Pair(base64EncryptedData, base64Iv) // Replace with file or secure storage
}

// Retrieve encrypted data and IV from storage
private fun getFromSecureStorage(alias: String): Pair<ByteArray, ByteArray>? {
    val storedValues = storageMap[alias] ?: return null
    val encryptedData = Base64.getDecoder().decode(storedValues.first)
    val iv = Base64.getDecoder().decode(storedValues.second)
    return Pair(encryptedData, iv)
}

// Simulated storage map for demonstration purposes (replace with secure storage mechanism)
private val storageMap = mutableMapOf<String, Pair<String, String>>()

// Save the token securely by encrypting it using BouncyCastle
internal actual fun save(token: String, alias: String): Boolean {
    return try {
        val secretKey = getOrCreateSecretKey(alias)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding", "BC") // Use BouncyCastle
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)

        val encryptedData = cipher.doFinal(token.toByteArray(Charsets.UTF_8))
        val iv = cipher.iv

        // Save the encrypted data and IV to secure storage
        saveToSecureStorage(alias, encryptedData, iv)
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

// Retrieve and decrypt the token securely using BouncyCastle
internal actual fun get(alias: String): String? {
    return try {
        val (encryptedData, iv) = getFromSecureStorage(alias) ?: return null
        val secretKey = getSecretKey(alias) ?: return null

        val cipher = Cipher.getInstance("AES/GCM/NoPadding", "BC") // Use BouncyCastle
        cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(128, iv))

        val decryptedData = cipher.doFinal(encryptedData)
        String(decryptedData, Charsets.UTF_8)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

// Delete the key from the Keystore and the associated data from storage
internal actual fun delete(alias: String): Boolean {
    return try {
        keyStore.deleteEntry(alias)
        storageMap.remove(alias) // You can also remove from file or other storage
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
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

internal actual fun registerBouncyCastle() {
    Security.insertProviderAt(BouncyCastleProvider(), 1)
}