package com.metashark.purlog.core

import com.metashark.purlog.enums.PurLogEnv
import com.metashark.purlog.enums.PurLogLevel
import com.metashark.purlog.models.PurLogConfig
import com.metashark.purlog.utils.currentTimestamp
import com.metashark.purlog.utils.shouldLog

internal class SdkLogger private constructor() {

    private var env: PurLogEnv = PurLogEnv.DEV
    private var configLevel: PurLogLevel = PurLogLevel.VERBOSE

    companion object {
        val shared = SdkLogger()
    }

    fun initialize(config: PurLogConfig) {
        this.env = config.env
        this.configLevel = config.level
    }

    fun log(level: PurLogLevel, message: String, metadata: Map<String, String> = emptyMap()) {
        if (env != PurLogEnv.DEV) return
        if (!shouldLog(level, configLevel)) return
        consoleLog(env, level, message, metadata, isInternal = true)
    }

    internal fun consoleLog(env: PurLogEnv, logLevel: PurLogLevel, message: String, metadata: Map<String, String>, isInternal: Boolean) {
        if (env != PurLogEnv.DEV) return

        val formattedMessage = "[${currentTimestamp}] [${logLevel.name}]${if (isInternal) " [PurLog] " else " "}$message"
        val formattedMessageWithMetaData = if (metadata.isNotEmpty()) "$formattedMessage\n\nmetadata: $metadata" else formattedMessage
        when (logLevel) {
            PurLogLevel.VERBOSE -> com.metashark.purlog.utils.logMessage(logLevel, "⚪️ $formattedMessageWithMetaData")
            PurLogLevel.DEBUG -> com.metashark.purlog.utils.logMessage(logLevel, "🔵 $formattedMessageWithMetaData")
            PurLogLevel.INFO -> com.metashark.purlog.utils.logMessage(logLevel, "🟢 $formattedMessageWithMetaData")
            PurLogLevel.WARN -> com.metashark.purlog.utils.logMessage(logLevel, "🟡 $formattedMessageWithMetaData")
            PurLogLevel.ERROR -> com.metashark.purlog.utils.logMessage(logLevel, "🔴 $formattedMessageWithMetaData")
            PurLogLevel.FATAL -> com.metashark.purlog.utils.logMessage(logLevel, "🔴🔴🔴 $formattedMessageWithMetaData") 
        }
    }
}