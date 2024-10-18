package com.metashark.purlog.utils


internal expect fun save(token: String, alias: String): Boolean
internal expect fun get(alias: String): String?
internal expect fun delete(alias: String): Boolean
internal expect fun createUUIDIfNotExists(): String?

internal expect fun registerBouncyCastle()