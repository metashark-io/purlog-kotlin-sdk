package com.metashark.purlog.utils

import com.metashark.purlog.enums.PurLogLevel

internal actual fun logMessage(level: PurLogLevel, message: String) {
    println(message)
}