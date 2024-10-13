package com.metashark.purlog.utils

import com.metashark.purlog.core.PurLogError
import com.metashark.purlog.core.PurLogException
import com.metashark.purlog.enums.PurLogLevel
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.darwin.* // Darwin engine for Apple platforms
import io.ktor.client.plugins.contentnegotiation.* // Content Negotiation in Ktor 3.0.0
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

internal actual suspend fun postLogInternal(
    url: String,
    logData: String
): Result<Unit> {
    // Using the Darwin engine for Apple platforms
    val client = HttpClient(Darwin) {
        install(ContentNegotiation) {
            json() // Uses kotlinx.serialization for JSON handling
        }
    }

    return try {
        val response: HttpResponse = client.post(url) {
            contentType(ContentType.Application.Json)
            setBody(logData) // Use setBody in Ktor 2.x
        }

        if (response.status == HttpStatusCode.OK) {
            Result.success(Unit)
        } else {
            Result.failure(PurLogException(PurLogError.error(
                title = "Failed to create log",
                message = "Bad response: ${response.status}",
                logLevel = PurLogLevel.ERROR
            )))
        }
    } catch (e: Exception) {
        Result.failure(PurLogException(PurLogError.error(
            title = "Failed to create log",
            error = e
        )))
    } finally {
        client.close()
    }
}

internal actual suspend fun createTokenInternal(
    url: String,
    bodyData: String
): Result<String> {
    val client = HttpClient(Darwin) {
        install(ContentNegotiation) {
            json(Json { prettyPrint = true })
        }
    }

    return try {
        val response: HttpResponse = client.post(url) {
            contentType(ContentType.Application.Json)
            setBody(bodyData) // Use setBody in Ktor 3.x
        }

        if (response.status == HttpStatusCode.OK) {
            val responseBody = response.body<String>()
            val json = Json.decodeFromString<Map<String, String>>(responseBody)
            Result.success(json["jwt"] ?: "")
        } else {
            Result.failure(PurLogException(PurLogError.error("Failed to create session JWT", "Bad response: ${response.status}")))
        }
    } catch (e: Exception) {
        Result.failure(PurLogException(PurLogError.error("Failed to create session JWT", e)))
    } finally {
        client.close()
    }
}

internal actual suspend fun refreshTokenInternal(
    url: String,
    bodyData: String
): Result<String> {
    val client = HttpClient(Darwin) {
        install(ContentNegotiation) {
            json(Json { prettyPrint = true })
        }
    }
    return try {
        val response: HttpResponse = client.put(url) {
            contentType(ContentType.Application.Json)
            setBody(bodyData) // Use setBody in Ktor 3.x
        }

        if (response.status == HttpStatusCode.OK) {
            val responseBody = response.body<String>()
            val json = Json.decodeFromString<Map<String, String>>(responseBody)
            Result.success(json["jwt"] ?: "")
        } else {
            Result.failure(PurLogException(PurLogError.error("Failed to refresh session JWT", "Bad response: ${response.status}")))
        }
    } catch (e: Exception) {
        Result.failure(PurLogException(PurLogError.error("Failed to refresh session JWT", e)))
    } finally {
        client.close()
    }
}