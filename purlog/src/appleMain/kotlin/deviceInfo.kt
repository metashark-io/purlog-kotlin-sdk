package io.metashark.purlog.utils

import platform.Foundation.NSBundle

internal actual fun getClientVersion(context: Any?): String {
    val version = NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as? String
    return version ?: ""
}