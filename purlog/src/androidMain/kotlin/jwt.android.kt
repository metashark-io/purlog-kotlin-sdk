package com.metashark.purlog.utils

import com.metashark.purlog.models.SessionToken
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import android.util.Base64

internal actual fun decodeJWT(jwt: String): SessionToken? {
    fun base64Decode(value: String): ByteArray {
        val normalizedBase64 = value.replace("-", "+").replace("_", "/")
        return Base64.decode(normalizedBase64, Base64.DEFAULT)
    }

    fun decodeJWTPart(value: String): SessionToken {
        val bodyData = base64Decode(value)
        val jsonData = String(bodyData)
        val json = Json { ignoreUnknownKeys = true }

        // Deserialize JSON string to SessionToken using Kotlinx Serialization
        return json.decodeFromString<SessionToken>(jsonData)
    }

    val segments = jwt.split(".")
    if (segments.size < 2) {
        throw IllegalArgumentException("Invalid JWT token")
    }
    return decodeJWTPart(segments[1])
}