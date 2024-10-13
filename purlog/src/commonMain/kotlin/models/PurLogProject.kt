package com.metashark.purlog.models

import com.metashark.purlog.core.KeyStoreWrapper

internal data class PurLogProject(
    val id: String // project id
) {
    init {
        val saveResult = KeyStoreWrapper.save(token = projectJWT, forKey = "PurLogProjectJWT")
        when (saveResult) {
            is Result.Success -> {
                // success
            }
            is Result.Failure -> {
                return
            }
        }
    }
}