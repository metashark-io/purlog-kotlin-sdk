package com.metashark.purlog.core.api

import com.metashark.purlog.core.PurLogError
import com.metashark.purlog.core.PurLogException
import com.metashark.purlog.enums.PurLogEnv
import com.metashark.purlog.enums.PurLogLevel
import com.metashark.purlog.models.LogData
import com.metashark.purlog.utils.get
import com.metashark.purlog.utils.postLogInternal
import com.metashark.purlog.utils.refreshTokenIfExpired
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal suspend fun postLog(
    projectId: String,
    env: PurLogEnv,
    logLevel: PurLogLevel,
    message: String,
    metadata: Map<String, String>,
    deviceInfo: Map<String, String>,
    appVersion: String
): Result<Unit> {
    var projectJWT: String?
    var sessionJWT: String?

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

    // Prepare the log data
    val logData = LogData(
        projectJWT = projectJWT,
        sessionJWT = sessionJWT,
        projectId = projectId,
        message = message,
        level = logLevel.toString(),
        env = env.toString(),
        deviceInfo = deviceInfo,
        metadata = metadata,
        appVersion = appVersion,
        sdk = "kotlin"
    )

    // Serialize the LogData object to JSON
    val logDataJson = Json.encodeToString(logData)

    // Call the `postLog` function (it will use the platform-specific actual implementation)
    val url = "https://us-central1-purlog-45f7f.cloudfunctions.net/api/logs"
    return postLogInternal(url, logDataJson)
}