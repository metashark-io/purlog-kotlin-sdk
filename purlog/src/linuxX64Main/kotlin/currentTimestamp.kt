package io.metashark.purlog.utils

import kotlinx.cinterop.ExperimentalForeignApi
import platform.posix.strftime
import platform.posix.time
import platform.posix.localtime
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.refTo
import kotlinx.cinterop.toKString
import platform.posix.time_tVar

@OptIn(ExperimentalForeignApi::class)
actual val currentTimestamp: String
    get() {
        memScoped {
            val buffer = ByteArray(100)
            val timeValue = alloc<time_tVar>()
            time(timeValue.ptr)
            val localTime = localtime(timeValue.ptr)

            strftime(buffer.refTo(0), buffer.size.toULong(), "%Y-%m-%d %H:%M:%S", localTime)
            return buffer.toKString()
        }
    }