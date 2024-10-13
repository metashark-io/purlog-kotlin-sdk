package com.metashark.purlog.utils

import com.metashark.purlog.core.PurLogError
import com.metashark.purlog.core.PurLogException
import com.metashark.purlog.enums.PurLogLevel
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

internal actual suspend fun postLogInternal(
    url: String,
    logData: String
): Result<Unit> {
    // Use CIO engine for Android/JVM platforms
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
    }

    return try {
        val response: HttpResponse = client.post(url) {
            contentType(ContentType.Application.Json)
            setBody(logData) // Use setBody in Ktor 3.x
        }

        if (response.status == HttpStatusCode.OK) {
            Result.success(Unit)
        } else {
            Result.failure(
                PurLogException(
                    PurLogError.error(
                        title = "Failed to create log",
                        message = "Bad response: ${response.status}",
                        logLevel = PurLogLevel.ERROR
                    )
                )
            )
        }
    } catch (e: Exception) {
        Result.failure(
            PurLogException(
                PurLogError.error(
                    title = "Failed to create log",
                    error = e
                )
            )
        )
    } finally {
        client.close()
    }
}