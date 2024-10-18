package com.metashark.purlog.models

import com.metashark.purlog.enums.PurLogEnv
import com.metashark.purlog.enums.PurLogLevel
import com.metashark.purlog.utils.deviceInfo
import com.metashark.purlog.utils.getClientVersion
import com.metashark.purlog.utils.registerBouncyCastle

data class PurLogConfig(
    val level: PurLogLevel,
    val env: PurLogEnv,
    val projectId: String? = null,
    val runTimeDeviceInfo: Map<String, String> = emptyMap(),
    val appVersion: String = ""
) {
    class Builder {
        private var level: PurLogLevel = PurLogLevel.VERBOSE
        private var env: PurLogEnv = PurLogEnv.DEV
        private var projectId: String? = null

        internal var runTimeDeviceInfo: Map<String, String> = emptyMap()
        internal var appVersion: String = ""

        fun setLevel(level: PurLogLevel) = apply {
            this.level = level
        }

        fun setEnv(env: PurLogEnv) = apply {
            this.env = env
        }

        fun setProject(projectId: String, projectJWT: String) = apply {
            registerBouncyCastle()
            PurLogProject.create(projectId, projectJWT)
            this.projectId = projectId
        }

        fun build(): PurLogConfig {
            if (runTimeDeviceInfo.isEmpty()) {
                runTimeDeviceInfo = deviceInfo(null)
                appVersion = getClientVersion(null)
            }
            return PurLogConfig(level, env, projectId, runTimeDeviceInfo, appVersion)
        }
    }
}