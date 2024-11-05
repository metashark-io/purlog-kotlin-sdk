package io.metashark.purlog.utils

internal actual fun deviceInfo(context: Any?): Map<String, String> {
    val osName = System.getProperty("os.name") ?: ""
    val osVersion = System.getProperty("os.version") ?: ""

    return mapOf(
        "osName" to osName,
        "osVersion" to osVersion
    )
}

internal actual fun getClientVersion(context: Any?): String {
    return "" // TODO
}