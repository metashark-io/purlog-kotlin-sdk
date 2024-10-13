package com.metashark.purlog.utils

import platform.Foundation.*

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.serialization.json.Json

@OptIn(BetaInteropApi::class, ExperimentalForeignApi::class, UnsafeNumber::class)
internal actual fun decodeJWT(jwt: String): Map<String, Any> {
    fun base64Decode(value: String): ByteArray {
        val normalizedBase64 = value.replace("-", "+").replace("_", "/")
        val decodedData = NSData.create(normalizedBase64, 0u)
        return decodedData?.toByteArray() ?: throw IllegalArgumentException("Invalid Base64")
    }

    fun decodeJWTPart(value: String): Map<String, Any> {
        val bodyDataByteArray = base64Decode(value)
        val bodyData = bodyDataByteArray.usePinned { pinned ->
            NSData.create(bytes = pinned.addressOf(0), length = bodyDataByteArray.size.toUInt())
        }
        // Convert ByteArray to NSString using init with data and encoding
        val jsonString = NSString.create(bodyData, NSUTF8StringEncoding) ?: throw IllegalArgumentException("Failed to create string")
        return Json.decodeFromString(jsonString.toString())
    }

    val segments = jwt.split(".")
    if (segments.size < 2) {
        throw IllegalArgumentException("Invalid JWT token")
    }
    return decodeJWTPart(segments[1])
}

@OptIn(ExperimentalForeignApi::class, UnsafeNumber::class)
internal fun NSData.toByteArray(): ByteArray {
    val length = this.length.toInt()
    val byteArray = ByteArray(length)

    // Use pinned memory to get the pointer to the byte array
    byteArray.usePinned { pinned ->
        // Create an NSRange for the full length of the data
        val range = NSMakeRange(0u, length.toUInt())
        this.getBytes(pinned.addressOf(0), range)
    }
    return byteArray
}

internal actual fun currentTimeMillis(): Long {
    return (NSDate().timeIntervalSince1970 * 1000).toLong()
}