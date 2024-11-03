package com.metashark.purlog.models

import kotlinx.serialization.Serializable

@Serializable
data class LogData(
    val projectJWT: String,
    val sessionJWT: String,
    val projectId: String,
    val message: String,
    val level: String,
    val env: String,
    val deviceInfo: Map<String, String>,
    val metadata: Map<String, String>,
    val appVersion: String,
    val sdk: String = "kotlin"
)