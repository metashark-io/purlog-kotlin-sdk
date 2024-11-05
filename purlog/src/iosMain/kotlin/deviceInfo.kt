package io.metashark.purlog.utils

import platform.Foundation.NSProcessInfo
import platform.UIKit.UIDevice
import platform.UIKit.UIUserInterfaceIdiomPad

internal actual fun deviceInfo(context: Any?): Map<String, String> {
    val osVersion = NSProcessInfo.processInfo().operatingSystemVersionString()

    var osName = "iOS"
    if (UIDevice.currentDevice.userInterfaceIdiom == UIUserInterfaceIdiomPad) {
        osName = "iPadOS"
    }
    return mapOf(
        "osName" to osName,
        "osVersion" to osVersion
    )
}