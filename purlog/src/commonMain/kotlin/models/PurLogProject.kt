package com.metashark.purlog.models

import com.metashark.purlog.utils.save

internal data class PurLogProject(
    val id: String // project id
) {
    companion object {
        fun create(id: String, projectJWT: String) {
            save(token = projectJWT, alias = "PurLogProjectJWT")
        }
    }
}