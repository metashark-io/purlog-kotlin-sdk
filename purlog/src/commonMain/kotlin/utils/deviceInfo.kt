package com.metashark.purlog.utils


internal expect fun deviceInfo(context: Any?): Map<String, String>

internal expect fun getClientVersion(context: Any?): String
