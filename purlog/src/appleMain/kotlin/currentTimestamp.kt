package com.metashark.purlog.utils

import platform.Foundation.*

private fun getCurrentTimestamp(): String {
    val formatter = NSDateFormatter().apply {
        dateFormat = "yyyy-MM-dd HH:mm:ss.SSS"
        locale = NSLocale()
    }
    return formatter.stringFromDate(NSDate())
}

internal actual val currentTimestamp: String = getCurrentTimestamp()

internal actual fun currentTimeMillis(): Long {
    return (NSDate().timeIntervalSince1970 * 1000).toLong()
}
