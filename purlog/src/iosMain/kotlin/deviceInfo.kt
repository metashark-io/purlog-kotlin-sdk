package com.metashark.purlog.utils

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSProcessInfo
import platform.UIKit.UIDevice
import platform.UIKit.UIUserInterfaceIdiomPad

@OptIn(ExperimentalForeignApi::class)
internal actual fun deviceInfo(context: Any?): Map<String, String> {
    val processInfo = NSProcessInfo.processInfo
    val osVersion = processInfo.operatingSystemVersion.toString()

    var osName = "iOS"
    if (UIDevice.currentDevice.userInterfaceIdiom == UIUserInterfaceIdiomPad) {
        osName = "iPadOS"
    }
    return mapOf(
        "osName" to osName,
        "osVersion" to osVersion
    )
}