package com.metashark.purlog.utils

import com.metashark.purlog.models.SessionToken
import kotlinx.serialization.json.Json
import java.util.Base64

internal actual fun decodeJWT(jwt: String): SessionToken? {
    /*fun base64Decode(value: String): ByteArray {
        val normalizedBase64 = value.replace("-", "+").replace("_", "/")
        return Base64.getDecoder().decode(normalizedBase64)
    }

    fun decodeJWTPart(value: String): Map<String, Any> {
        val bodyData = base64Decode(value)
        val json = String(bodyData)
        return Json.decodeFromString<Map<String, Any>>(json)
    }

    val segments = jwt.split(".")
    if (segments.size < 2) {
        throw IllegalArgumentException("Invalid JWT token")
    }
    return decodeJWTPart(segments[1])*/
    return null
}