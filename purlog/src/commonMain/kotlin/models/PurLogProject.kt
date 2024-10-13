package com.metashark.purlog.models

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