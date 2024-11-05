package io.metashark.purlog.utils

import android.util.Log
import io.metashark.purlog.enums.PurLogLevel

internal actual fun logMessage(level: PurLogLevel, message: String) {
    when (level) {
        PurLogLevel.VERBOSE -> Log.v(null, message)
        PurLogLevel.DEBUG -> Log.d(null, message)
        PurLogLevel.INFO -> Log.i(null, message)
        PurLogLevel.WARN -> Log.w(null, message)
        PurLogLevel.ERROR -> Log.e(null, message, null)
        PurLogLevel.FATAL -> Log.wtf(null, message, null)
    }
}