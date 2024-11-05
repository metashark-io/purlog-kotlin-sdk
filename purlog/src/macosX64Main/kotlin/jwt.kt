package io.metashark.purlog.utils

import io.metashark.purlog.models.SessionToken
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.value
import kotlinx.serialization.json.Json
import platform.CoreFoundation.CFDictionaryAddValue
import platform.CoreFoundation.CFDictionaryCreateMutable
import platform.CoreFoundation.CFTypeRefVar
import platform.CoreFoundation.kCFBooleanTrue
import platform.Foundation.CFBridgingRelease
import platform.Foundation.CFBridgingRetain
import platform.Foundation.NSData
import platform.Foundation.NSMakeRange
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.getBytes
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.errSecSuccess
import platform.Security.kSecAttrAccount
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnData
import platform.Security.kSecValueData

@OptIn(BetaInteropApi::class, ExperimentalForeignApi::class)
private fun ByteArray.toNSData(): NSData {
    this.usePinned { pinned ->
        return NSData.create(bytes = pinned.addressOf(0), length = this.size.toULong())
    }
}

@OptIn(ExperimentalForeignApi::class)
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
        delete(alias)

        // Add the new item
        val status = SecItemAdd(query, null)

        return status == errSecSuccess
    }
}

@OptIn(ExperimentalForeignApi::class)
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

@OptIn(ExperimentalForeignApi::class, UnsafeNumber::class)
private fun NSData.toByteArray(): ByteArray {
    val length = this.length.toInt()
    val byteArray = ByteArray(length)

    // Use pinned memory to get the pointer to the byte array
    byteArray.usePinned { pinned ->
        // Create an NSRange for the full length of the data
        val range = NSMakeRange(0u, length.toULong())
        this.getBytes(pinned.addressOf(0), range)
    }
    return byteArray
}