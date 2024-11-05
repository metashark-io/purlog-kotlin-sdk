package io.metashark.purlog.models

import io.metashark.purlog.enums.PurLogEnv
import io.metashark.purlog.enums.PurLogLevel
import io.metashark.purlog.utils.deviceInfo
import io.metashark.purlog.utils.didInitializeContext
import io.metashark.purlog.utils.getClientVersion
import io.metashark.purlog.utils.initializeAndroidSecureStorageManager

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
        private lateinit var projectId: String
        private lateinit var projectJWT: String

        private var didSetContext: Boolean = false

        internal var runTimeDeviceInfo: Map<String, String> = emptyMap()
        internal var appVersion: String? = null

        fun setLevel(level: PurLogLevel) = apply {
            this.level = level
        }

        fun setEnv(env: PurLogEnv) = apply {
            this.env = env
        }

        // android only: should be of type `import android.content.Context`
        fun setContext(context: Any?) = apply {
            didSetContext = initializeAndroidSecureStorageManager(context)
            runTimeDeviceInfo = deviceInfo(context)
            appVersion = getClientVersion(context)
        }

        fun setProject(projectId: String, projectJWT: String) = apply {
            this.projectId = projectId
            this.projectJWT = projectJWT
        }

        fun build(): PurLogConfig {
            if (!didInitializeContext(didSetContext)) {
                throw IllegalArgumentException("Invalid context. Please pass in android.content.Context into the setContext() builder method")
            }
            if (::projectId.isInitialized && ::projectJWT.isInitialized) {
                PurLogProject.create(projectId, projectJWT)
            }
            if (runTimeDeviceInfo.isEmpty()) {
                runTimeDeviceInfo = deviceInfo(null)
            }
            if (appVersion == null) {
                appVersion = getClientVersion(null)
            }
            return PurLogConfig(level, env, if (::projectId.isInitialized) projectId else null, runTimeDeviceInfo, appVersion ?: "")
        }
    }
}