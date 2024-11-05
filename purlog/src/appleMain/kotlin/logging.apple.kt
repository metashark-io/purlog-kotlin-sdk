package io.metashark.purlog.utils

import io.metashark.purlog.enums.PurLogLevel

internal actual fun logMessage(level: PurLogLevel, message: String) {
    println(message)
}