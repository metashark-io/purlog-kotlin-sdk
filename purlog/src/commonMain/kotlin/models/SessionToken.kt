package io.metashark.purlog.models

import kotlinx.serialization.Serializable

@Serializable
data class SessionToken(
    val tokenId: String,
    val token: String,
    val expiration: Float,
    val refreshToken: String,
    val uuid: String
)