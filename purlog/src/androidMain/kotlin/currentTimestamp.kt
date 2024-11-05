package io.metashark.purlog.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal actual val currentTimestamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(Date())

internal actual fun currentTimeMillis(): Long {
    return System.currentTimeMillis()
}