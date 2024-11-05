package io.metashark.purlog.core

import io.metashark.purlog.enums.PurLogLevel

data class PurLogError(
    val title: String,
    val message: String
) {
    companion object {
        fun error(title: String, message: String, logLevel: PurLogLevel = PurLogLevel.ERROR): PurLogError {
            SdkLogger.shared.log(level = logLevel, message = "$title. $message")
            return PurLogError(title = title, message = message)
        }

        fun error(title: String, error: Throwable, logLevel: PurLogLevel = PurLogLevel.ERROR): PurLogError {
            val message = error.message ?: "Unknown error"
            SdkLogger.shared.log(level = logLevel, message = "$title. $message")
            return PurLogError(title = title, message = message)
        }
    }
}