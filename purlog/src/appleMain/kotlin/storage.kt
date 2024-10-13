@file:OptIn(ExperimentalForeignApi::class, UnsafeNumber::class)

package com.metashark.purlog.utils

import platform.Foundation.*
import platform.Security.*
import kotlinx.cinterop.*
import platform.CoreFoundation.CFDictionaryRef
import platform.CoreFoundation.CFTypeRefVar
import platform.CoreFoundation.kCFBooleanTrue
import kotlin.ByteArray

@OptIn(BetaInteropApi::class)
private fun ByteArray.toNSData(): NSData {
    return NSData.create(bytes = this.refTo(0) as COpaquePointer?, length = this.size.toUInt()) // Use toULong() for NSUInteger
}

internal actual fun save(token: String, alias: String): Boolean {
    // Convert token to NSData
    val data = token.encodeToByteArray().toNSData()

    // Create a query for saving the token in the keychain
    val query = mapOf(
        kSecClass to kSecClassGenericPassword,
        kSecAttrAccount to alias,
        kSecValueData to data
    )

    // First delete any existing item
    SecItemDelete(query as CFDictionaryRef)

    // Add the new item
    val status = SecItemAdd(query as CFDictionaryRef, null)

    return status == errSecSuccess
}

@OptIn(BetaInteropApi::class)
internal actual fun get(alias: String): String? {
    // Create a query for retrieving the token from the keychain
    val query = mapOf(
        kSecClass to kSecClassGenericPassword,
        kSecAttrAccount to alias,
        kSecReturnData to kCFBooleanTrue!!,
        kSecMatchLimit to kSecMatchLimitOne
    )

    memScoped {
        val dataRef = alloc<CFTypeRefVar>()
        val status = SecItemCopyMatching(query as CFDictionaryRef, dataRef.ptr)

        // Check if the retrieval was successful
        if (status == errSecSuccess) {
            val data = dataRef.value as NSData
            return NSString.create(data, NSUTF8StringEncoding) as String
        } else {
            return null
        }
    }
}

internal actual fun delete(alias: String): Boolean {
    // Create a query to delete the token from the keychain
    val query = mapOf(
        kSecClass to kSecClassGenericPassword,
        kSecAttrAccount to alias
    )

    val status = SecItemDelete(query as CFDictionaryRef)
    return status == errSecSuccess
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