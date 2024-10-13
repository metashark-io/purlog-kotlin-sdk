@file:OptIn(ExperimentalForeignApi::class, UnsafeNumber::class)

package com.metashark.purlog.utils

import platform.Foundation.*
import platform.Security.*
import kotlinx.cinterop.*
import platform.CoreFoundation.CFTypeRefVar
import kotlin.ByteArray
import platform.CoreFoundation.*

@OptIn(BetaInteropApi::class)
private fun ByteArray.toNSData(): NSData {
    return NSData.create(bytes = this.refTo(0) as COpaquePointer?, length = this.size.toUInt()) // Use toULong() for NSUInteger
}

internal actual fun save(token: String, alias: String): Boolean {
    memScoped {
        // Convert token to NSData
        val data = token.encodeToByteArray().toNSData()

        // Create a mutable dictionary for the query
        val query = CFDictionaryCreateMutable(null, 3, null, null)

        // Add values to the query dictionary
        CFDictionaryAddValue(query, kSecClass, kSecClassGenericPassword)
        CFDictionaryAddValue(query, kSecAttrAccount, CFBridgingRetain(alias))
        CFDictionaryAddValue(query, kSecValueData, CFBridgingRetain(data))

        // First delete any existing item for the alias
        SecItemDelete(query)

        // Add the new item
        val status = SecItemAdd(query, null)

        return status == errSecSuccess
    }
}

internal actual fun get(alias: String): String? {
    memScoped {
        // Create the query as a mutable CFDictionary
        val query = CFDictionaryCreateMutable(null, 4, null, null)

        // Add values to the query dictionary
        CFDictionaryAddValue(query, kSecClass, kSecClassGenericPassword)
        CFDictionaryAddValue(query, kSecAttrAccount, CFBridgingRetain(alias))
        CFDictionaryAddValue(query, kSecReturnData, kCFBooleanTrue)
        CFDictionaryAddValue(query, kSecMatchLimit, kSecMatchLimitOne)

        // Allocate a pointer to store the result
        val result = alloc<CFTypeRefVar>()

        // Perform the query
        val status = SecItemCopyMatching(query, result.ptr)

        // If the query was successful, process the result
        if (status == errSecSuccess && result.value != null) {
            // Use CFBridgingRelease to cast the result to NSData
            val data = CFBridgingRelease(result.value) as? NSData ?: return null
            val byteArray = data.toByteArray()
            return byteArray.decodeToString()
        }
        return null
    }
}

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
        return uuid
    } else {
        return null
    }
}