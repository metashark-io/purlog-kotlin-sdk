package io.metashark.purlog.utils

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.timeIntervalSince1970

private fun getCurrentTimestamp(): String {
    val formatter = NSDateFormatter().apply {
        dateFormat = "yyyy-MM-dd HH:mm:ss.SSS"
    }
    return formatter.stringFromDate(NSDate())
}

internal actual val currentTimestamp: String = getCurrentTimestamp()

internal actual fun currentTimeMillis(): Long {
    return (NSDate().timeIntervalSince1970 * 1000).toLong()
}
