package com.metashark.purlog.utils

import com.metashark.purlog.enums.PurLogLevel
import platform.Foundation.NSLog

internal actual fun logMessage(level: PurLogLevel, message: String) {
    NSLog(message)
}