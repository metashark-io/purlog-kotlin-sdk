package com.metashark.purlog.utils

import platform.Foundation.NSProcessInfo

internal actual fun deviceInfo(context: Any?): Map<String, String> {
    val osVersion = NSProcessInfo.processInfo().operatingSystemVersionString()

    return mapOf(
        "osName" to "watchOS",
        "osVersion" to osVersion
    )
}