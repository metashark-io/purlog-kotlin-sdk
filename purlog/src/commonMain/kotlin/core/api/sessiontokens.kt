package core.api


import io.metashark.purlog.core.SdkLogger
import io.metashark.purlog.enums.PurLogLevel
import io.metashark.purlog.utils.createTokenInternal
import io.metashark.purlog.utils.refreshTokenInternal
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal object SessionTokenManager {

    var isRefreshing = false

    suspend fun createToken(projectJWT: String, uuid: String, projectId: String): Result<String> {
        SdkLogger.shared.log(PurLogLevel.VERBOSE, "calling createToken")
        isRefreshing = true

        val url = "https://us-central1-purlog-45f7f.cloudfunctions.net/api/session_tokens"
        val requestBody = mapOf(
            "projectJWT" to projectJWT,
            "uuid" to uuid,
            "projectId" to projectId
        )

        // Serialize the LogData object to JSON
        val tokenDataJSON = Json.encodeToString(requestBody)

        // Call the `postLog` function (it will use the platform-specific actual implementation)
        return createTokenInternal(url, tokenDataJSON)
    }

    suspend fun refreshToken(projectJWT: String, sessionJWT: String, projectId: String): Result<String> {
        SdkLogger.shared.log(PurLogLevel.VERBOSE, "refreshing session JWT...")
        isRefreshing = true

        val url = "https://us-central1-purlog-45f7f.cloudfunctions.net/api/session_tokens/refresh"
        val requestBody = mapOf(
            "projectJWT" to projectJWT,
            "sessionJWT" to sessionJWT,
            "projectId" to projectId
        )

        // Serialize the LogData object to JSON
        val tokenDataJSON = Json.encodeToString(requestBody)

        // Call the `postLog` function (it will use the platform-specific actual implementation)
        return refreshTokenInternal(url, tokenDataJSON)
    }
}