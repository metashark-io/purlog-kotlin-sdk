package com.metashark.purlog.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun getCurrentTimestamp(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
    return dateFormat.format(Date())
}

actual val currentTimestamp: String = getCurrentTimestamp()