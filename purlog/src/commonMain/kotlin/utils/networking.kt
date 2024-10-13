package com.metashark.purlog.utils

internal expect suspend fun postLogInternal(
    url: String,
    logData: String
): Result<Unit>