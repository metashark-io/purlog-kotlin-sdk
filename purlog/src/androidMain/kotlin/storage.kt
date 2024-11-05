package com.metashark.purlog.utils

import android.content.Context
import java.util.UUID

internal actual fun save(token: String, alias: String): Boolean {
    try {
        SecureStorageManager.saveToken(token, alias)
        return true
    } catch (e: Exception) {
        return false
    }
}

internal actual fun get(alias: String): String? {
    try {
        val token = SecureStorageManager.getToken(alias)
        return token
    } catch (e: Exception) {
        return null
    }
}

internal actual fun delete(alias: String): Boolean {
    try {
        SecureStorageManager.deleteToken(alias)
        return true
    } catch (e: Exception) {
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

internal actual fun didInitializeContext(didInitContext: Boolean): Boolean {
    return didInitContext
}

internal actual fun initializeAndroidSecureStorageManager(context: Any?): Boolean {
    try {
        if (context == null || !(context is Context)) {
            throw IllegalArgumentException("Invalid context. Please pass in android.content.Context into the setContext() builder method")
        }
        val androidContext = (context as Context)
        SecureStorageManager.initialize(androidContext)
        return true
    } catch (e: Exception) {
        e.printStackTrace()
        return false
    }
}