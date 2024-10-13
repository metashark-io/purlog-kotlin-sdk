package com.metashark.purlog.utils

actual fun deviceInfo(context: Any?): Map<String, String> {
    val osName = System.getProperty("os.name") ?: "Unknown OS"
    val osVersion = System.getProperty("os.version") ?: "Unknown Version"

    return mapOf(
        "osName" to osName,
        "osVersion" to osVersion
    )
}