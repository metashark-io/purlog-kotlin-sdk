package com.metashark.purlog.utils

import com.metashark.purlog.enums.PurLogLevel

internal fun shouldLog(logLevel: PurLogLevel, configLevel: PurLogLevel): Boolean {
    val levels = listOf(PurLogLevel.VERBOSE, PurLogLevel.DEBUG, PurLogLevel.INFO, PurLogLevel.WARN, PurLogLevel.ERROR, PurLogLevel.FATAL)
    val currentIndex = levels.indexOf(configLevel)
    val messageIndex = levels.indexOf(logLevel)
    return messageIndex >= currentIndex
}

internal expect fun logMessage(level: PurLogLevel, message: String)