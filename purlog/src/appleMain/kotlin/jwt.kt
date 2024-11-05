package io.metashark.purlog.utils

import io.metashark.purlog.models.SessionToken
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.UnsafeNumber
import kotlinx.serialization.json.Json
import platform.Foundation.NSData
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create

@OptIn(UnsafeNumber::class, BetaInteropApi::class)
internal actual fun decodeJWT(jwt: String): SessionToken? {
    // Helper function to handle Base64 decoding
    fun base64Decode(base64: String): NSData {
        // Normalize Base64 encoding and add padding if needed
        val normalizedBase64 = base64
            .replace("-", "+")
            .replace("_", "/")
            .padEnd((base64.length + 3) / 4 * 4, '=') // Add padding if necessary

        // Decode Base64 into NSData
        return NSData.create(base64EncodedString = normalizedBase64, options = 0u)
            ?: throw IllegalArgumentException("Invalid Base64 token")
    }

    // Helper function to decode a JWT part
    fun decodeJWTPart(value: String): SessionToken {
        val bodyData = base64Decode(value)

        // Convert NSData to NSString and then to Kotlin String
        val jsonString = NSString.create(bodyData, NSUTF8StringEncoding)?.toString()
            ?: throw IllegalArgumentException("Failed to convert data to string")

        // Configure JSON with ignoreUnknownKeys
        val json = Json { ignoreUnknownKeys = true }

        // Deserialize JSON string to SessionToken using Kotlinx Serialization
        return json.decodeFromString<SessionToken>(jsonString)
    }

    // Split JWT and decode the payload
    val segments = jwt.split(".")
    if (segments.size < 2) throw IllegalArgumentException("Invalid JWT token")
    return decodeJWTPart(segments[1])
}