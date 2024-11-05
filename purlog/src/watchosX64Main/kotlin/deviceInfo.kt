package io.metashark.purlog.utils

import platform.Foundation.NSProcessInfo

internal actual fun deviceInfo(context: Any?): Map<String, String> {
    val osVersion = NSProcessInfo.processInfo().operatingSystemVersionString()

    return mapOf(
        "osName" to "iOS",
        "osVersion" to osVersion
    )
}