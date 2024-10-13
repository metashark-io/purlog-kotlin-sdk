package com.metashark.purlog.utils

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.COpaquePointerVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CValuesRef
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toCValues
import kotlinx.cinterop.value
import platform.CoreFoundation.CFDictionaryCreate
import platform.CoreFoundation.CFDictionaryRef
import platform.CoreFoundation.CFStringRef
import platform.CoreFoundation.CFTypeRefVar
import platform.CoreFoundation.kCFBooleanTrue
import platform.Foundation.NSData
import platform.Foundation.NSDictionary
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Security.SecItemCopyMatching
import platform.Security.errSecSuccess
import platform.Security.kSecAttrAccount
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnData

@OptIn(BetaInteropApi::class, ExperimentalForeignApi::class)
internal actual fun get(alias: String): String? {
    // Create a query for retrieving the token from the keychain
    val query = mapOf(
        kSecClass to kSecClassGenericPassword,
        kSecAttrAccount to alias,
        kSecReturnData to kCFBooleanTrue,
        kSecMatchLimit to kSecMatchLimitOne
    )

    // Convert the query map to NSDictionary

    memScoped {
        val keys = query.keys.map { it }.toCValues()
        val values = query.values.map { it }.toCValues()
        val queryDict = CFDictionaryCreate(
            null, // Default allocator
            keys, // Keys as CValues<CFStringRef>
            values, // Values as CValues<CFTypeRef>
            query.size.toLong(), // Number of elements in the dictionary
            null, // Default key callbacks
            null  // Default value callbacks
        )
        val dataRef = alloc<CFTypeRefVar>()
        val status = SecItemCopyMatching(queryDict as CFDictionaryRef, dataRef.ptr)

        // Check if the retrieval was successful
        if (status == errSecSuccess) {
            val data = dataRef.value as? NSData // Safe cast to NSData
            if (data != null) {
                return NSString.create(data, NSUTF8StringEncoding) as String
            }
        }
        return null
    }
}