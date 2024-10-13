package core.api

import com.metashark.purlog.core.PurLogError
import com.metashark.purlog.core.PurLogException
import com.metashark.purlog.core.SdkLogger
import com.metashark.purlog.enums.PurLogLevel
import com.metashark.purlog.utils.ioDispatcher
import com.metashark.purlog.utils.save
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

internal object SessionTokenManager {

    var isRefreshing = false

    suspend fun createToken(projectJWT: String, uuid: String, projectId: String): Result<String> {
        SdkLogger.shared.log(PurLogLevel.VERBOSE, "calling createToken")
        isRefreshing = true

        val url = URL("https://us-central1-purlog-45f7f.cloudfunctions.net/api/session_tokens")
        val requestBody = JSONObject().apply {
            put("projectJWT", projectJWT)
            put("uuid", uuid)
            put("projectId", projectId)
        }.toString()

        return withContext(ioDispatcher) {
            try {
                val connection = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    setRequestProperty("Content-Type", "application/json")
                    doOutput = true
                    outputStream.write(requestBody.toByteArray())
                }

                val responseCode = connection.responseCode
                if (responseCode != 200) {
                    isRefreshing = false
                    return@withContext Result.failure(PurLogException(PurLogError.error("Failed to create session JWT", "Non-200 status code")))
                }

                val responseBody = connection.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(responseBody)
                val newJwt = json.getString("jwt")

                val saveResult = save(newJwt, "PurLogSessionJWT")
                if (saveResult) {
                    SdkLogger.shared.log(SdkLogger.Level.INFO, "Session JWT created!")
                    isRefreshing = false
                    Result.success(newJwt)
                } else {
                    isRefreshing = false
                    Result.failure(
                        PurLogException(
                            PurLogError.error("Failed to create session JWT", "Unable to save session JWT to keystore.", PurLogLevel.ERROR)
                        )
                    )
                }
            } catch (e: Exception) {
                isRefreshing = false
                Result.failure(PurLogException(PurLogError.error("Failed to create session JWT", e)))
            }
        }
    }

    suspend fun refreshToken(projectJWT: String, sessionJWT: String, projectId: String): Result<String> {
        SdkLogger.shared.log(PurLogLevel.VERBOSE, "refreshing session JWT...")
        isRefreshing = true

        val url = URL("https://us-central1-purlog-45f7f.cloudfunctions.net/api/session_tokens/refresh")
        val requestBody = JSONObject().apply {
            put("projectJWT", projectJWT)
            put("sessionJWT", sessionJWT)
            put("projectId", projectId)
        }.toString()

        return withContext(ioDispatcher) {
            try {
                val connection = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "PUT"
                    setRequestProperty("Content-Type", "application/json")
                    doOutput = true
                    outputStream.write(requestBody.toByteArray())
                }

                val responseCode = connection.responseCode
                if (responseCode != 200) {
                    isRefreshing = false
                    return@withContext Result.failure(PurLogException(PurLogError.error("Failed to refresh session JWT", "Non-200 status code")))
                }

                val responseBody = connection.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(responseBody)
                val newJwt = json.getString("jwt")

                val saveResult = save(newJwt, "PurLogSessionJWT")
                if (saveResult) {
                    SdkLogger.shared.log(SdkLogger.Level.INFO, "Session JWT refreshed!")
                    isRefreshing = false
                    Result.success(newJwt)
                } else {
                    isRefreshing = false
                    Result.failure(PurLogException(PurLogError.error("Failed to refresh session JWT", "Unable to save session JWT to keystore.")))
                }
            } catch (e: Exception) {
                isRefreshing = false
                Result.failure(PurLogException(PurLogError.error("Failed to refresh session JWT", e)))
            }
        }
    }
}