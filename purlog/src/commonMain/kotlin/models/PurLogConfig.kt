package com.metashark.purlog.models

import com.metashark.purlog.enums.PurLogEnv
import com.metashark.purlog.enums.PurLogLevel

data class PurLogConfig(
    val level: PurLogLevel,
    val env: PurLogEnv,
    val projectId: String? = null
) {
    class Builder {
        private var level: PurLogLevel = PurLogLevel.VERBOSE
        private var env: PurLogEnv = PurLogEnv.DEV
        private var projectId: String? = null

        fun setLevel(level: PurLogLevel) = apply {
            this.level = level
        }

        fun setEnv(env: PurLogEnv) = apply {
            this.env = env
        }

        fun setProject(projectId: String, projectJWT: String) = apply {
            val project = PurLogProject(projectId, projectJWT)
            this.projectId = project.id
        }

        fun build(): PurLogConfig {
            return PurLogConfig(level, env, projectId)
        }
    }
}