package io.metashark.purlog.models

import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(
    val success: Boolean,
    val jwt: String?
)
