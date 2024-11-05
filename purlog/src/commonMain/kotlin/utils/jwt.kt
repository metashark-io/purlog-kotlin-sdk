package io.metashark.purlog.utils

import io.metashark.purlog.core.PurLogException
import io.metashark.purlog.core.PurLogError
import io.metashark.purlog.core.SdkLogger
import io.metashark.purlog.enums.PurLogLevel
import io.metashark.purlog.enums.TokenStatus
import io.metashark.purlog.models.SessionToken
import core.api.SessionTokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.Result
import kotlin.Exception

internal suspend fun refreshTokenIfExpired(projectJWT: String, sessionJWT: String, projectId: String) {
    withContext(Dispatchers.Default) {
        val checkTokenExpResult = checkTokenExpiration(sessionJWT).getOrNull()
        if (checkTokenExpResult!= null) {
            if (checkTokenExpResult == TokenStatus.EXPIRED) {
                if (!SessionTokenManager.isRefreshing) {
                    SessionTokenManager.refreshToken(projectJWT, sessionJWT, projectId)
                } else {
                    // TODO: queue log and retry
                }
            }
        } else {
            // this shouldn't fail
            SdkLogger.shared.log(level = PurLogLevel.ERROR, message = "checkTokenExpiration failed")
        }
    }
}

private fun checkTokenExpiration(sessionJWT: String): Result<TokenStatus> {
    return try {
        val jwt = decodeJWT(sessionJWT)
        val expirationTimestamp = jwt?.expiration
        if (expirationTimestamp != null) {
            val expirationDate = expirationTimestamp * 1000 // Convert to milliseconds
            val isExpired = expirationDate < currentTimeMillis()
            Result.success(if (isExpired) TokenStatus.EXPIRED else TokenStatus.VALID)
        } else {
            Result.failure(PurLogException(PurLogError.error("Failed to decode JWT", "expiration field not found")))
        }
    } catch (e: Exception) {
        Result.failure(PurLogException(PurLogError.error("Failed to decode JWT", e.message ?: "Unknown error")))
    }
}

internal expect fun decodeJWT(jwt: String): SessionToken?