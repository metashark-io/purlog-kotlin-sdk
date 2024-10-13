package com.metashark.purlog.core.api

import com.metashark.purlog.core.PurLogError
import com.metashark.purlog.core.PurLogException
import com.metashark.purlog.enums.PurLogEnv
import com.metashark.purlog.enums.PurLogLevel
import com.metashark.purlog.utils.get

internal suspend fun postLog(
    projectId: String,
    env: PurLogEnv,
    logLevel: PurLogLevel,
    message: String,
    metadata: Map<String, String>,
    deviceInfo: Map<String, String>,
    appVersion: String
): Result<Unit> {
    var projectJWT: String? = null
    var sessionJWT: String? = null

    // Retrieve projectJWT from KeystoreWrapper
    projectJWT = get("PurLogProjectJWT")
    if (projectJWT.isNullOrBlank()) {
        return Result.failure(
            PurLogException(PurLogError.error(
                title = "Failed to create log",
                message = "Unable to retrieve project jwt from keystore.",
                logLevel = PurLogLevel.ERROR
            ))
        )
    }

    // Retrieve sessionJWT from KeystoreWrapper
    sessionJWT = get("PurLogSessionJWT")
    if (sessionJWT.isNullOrBlank()) {
        return Result.failure(PurLogException(PurLogError.error(
            title = "Failed to create log",
            message = "Unable to retrieve session jwt from keystore",
            logLevel = PurLogLevel.ERROR
        )))
    }

    // Refresh token if expired
    refreshTokenIfExpired(projectJWT, sessionJWT, projectId)

    val url = URL("https://us-central1-purlog-45f7f.cloudfunctions.net/api/logs")

    // Prepare the log data
    val logData = mapOf(
        "projectJWT" to projectJWT,
        "sessionJWT" to sessionJWT,
        "projectId" to projectId,
        "message" to message,
        "level" to logLevel.toString(),
        "env" to env.toString(),
        "deviceInfo" to deviceInfo,
        "metadata" to metadata,
        "appVersion" to appVersion
    )

    val requestBody = JSONObject(logData).toString().toByteArray()

    // Create HTTP request
    val request = Request.Builder()
        .url(url)
        .post(requestBody.toRequestBody("application/json".toMediaType()))
        .build()

    return try {
        val response = httpClient.newCall(request).await()

        if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(
                PurLogException(PurLogError.error(
                    title = "Failed to create log",
                    message = "Bad response.",
                    logLevel = PurLogLevel.ERROR
                ))
            )
        }
    } catch (e: Exception) {
        Result.failure(
            PurLogException(PurLogError.error(
                title = "Failed to create log",
                error = e
            ))
        )
    }
}