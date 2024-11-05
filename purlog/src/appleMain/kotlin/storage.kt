@file:OptIn(ExperimentalForeignApi::class, UnsafeNumber::class)

package com.metashark.purlog.utils

import platform.Foundation.*
import platform.Security.*
import kotlinx.cinterop.*
import platform.CoreFoundation.*

internal actual fun delete(alias: String): Boolean {
    memScoped {
        // Create a mutable dictionary for the query
        val query = CFDictionaryCreateMutable(null, 2, null, null)

        // Add values to the query dictionary
        CFDictionaryAddValue(query, kSecClass, kSecClassGenericPassword)
        CFDictionaryAddValue(query, kSecAttrAccount, CFBridgingRetain(alias))

        // Attempt to delete the keychain item
        val status = SecItemDelete(query)

        // Return true if deletion was successful
        return status == errSecSuccess
    }
}

internal actual fun createUUIDIfNotExists(): String? {
    // Try to get an existing UUID from the keychain
    val existingUUID = get("PurLogSessionUUID")
    if (existingUUID != null) {
        return existingUUID
    }

    // Generate a new UUID and save it to the keychain
    val uuid = NSUUID().UUIDString()
    return if (save(uuid, "PurLogSessionUUID")) {
        uuid
    } else {
        null
    }
}

internal actual fun didInitializeContext(didInitContext: Boolean): Boolean {
    return true
}
internal actual fun initializeAndroidSecureStorageManager(context: Any?): Boolean {
    return true
}