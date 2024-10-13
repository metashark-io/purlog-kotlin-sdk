package com.metashark.purlog.utils

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSProcessInfo

@OptIn(ExperimentalForeignApi::class)
actual fun deviceInfo(context: Any?): Map<String, String> {
    val processInfo = NSProcessInfo.processInfo
    val osVersion = processInfo.operatingSystemVersion.toString()

    return mapOf(
        "osName" to "tvOS",
        "osVersion" to osVersion
    )
}