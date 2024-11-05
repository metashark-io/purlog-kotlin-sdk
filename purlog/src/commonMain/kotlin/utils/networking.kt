package io.metashark.purlog.utils

// TODO: clean this up

internal expect suspend fun postLogInternal(
    url: String,
    logData: String
): Result<Unit>

internal expect suspend fun createTokenInternal(
    url: String,
    bodyData: String
): Result<String>

internal expect suspend fun refreshTokenInternal(
    url: String,
    bodyData: String,
): Result<String>