package com.metashark.purlog.utils

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale

private fun getCurrentTimestamp(): String {
    val formatter = NSDateFormatter().apply {
        dateFormat = "yyyy-MM-dd HH:mm:ss.SSS"
        locale = NSLocale()
    }
    return formatter.stringFromDate(NSDate())
}

actual val currentTimestamp: String = getCurrentTimestamp()