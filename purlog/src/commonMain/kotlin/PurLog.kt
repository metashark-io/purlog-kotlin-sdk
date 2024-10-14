package com.metashark.purlog

import com.metashark.purlog.core.PurLogError
import com.metashark.purlog.core.PurLogException
import com.metashark.purlog.core.SdkLogger
import com.metashark.purlog.core.api.postLog
import com.metashark.purlog.enums.PurLogEnv
import com.metashark.purlog.enums.PurLogLevel
import com.metashark.purlog.models.PurLogConfig
import com.metashark.purlog.utils.createUUIDIfNotExists
import com.metashark.purlog.utils.deviceInfo
import com.metashark.purlog.utils.get
import com.metashark.purlog.utils.ioDispatcher
import com.metashark.purlog.utils.refreshTokenIfExpired
import com.metashark.purlog.utils.shouldLog
import core.api.SessionTokenManager.createToken
import kotlinx.coroutines.withContext

object PurLog {

    private var config: PurLogConfig = PurLogConfig(level = PurLogLevel.VERBOSE, env = PurLogEnv.DEV)
    private var isInitialized = false
    private val appVersion: String = "Unknown"  // Update this to fetch from Android's build config if needed
    private var runTimeDeviceInfo: Map<String, String> = emptyMap()

    suspend fun initialize(config: PurLogConfig, androidApplicationContext: Any? = null): Result<Unit> = withContext(ioDispatcher) {
        if (isInitialized) {
            return@withContext Result.failure(
                PurLogException(PurLogError.error("Initialization failed", "Already initialized", PurLogLevel.WARN))
            )
        }
        runTimeDeviceInfo = deviceInfo(androidApplicationContext)

        SdkLogger.shared.initialize(config)
        this@PurLog.config = config
        SdkLogger.shared.log(PurLogLevel.VERBOSE, "Initializing PurLog...")
        SdkLogger.shared.log(PurLogLevel.DEBUG, "config: $config")

        val projectId = config.projectId
        if (projectId == null) {
            SdkLogger.shared.log(PurLogLevel.INFO, "PurLog Initialized without projectId")
            isInitialized = true
            return@withContext Result.success(Unit)
        }

        // Handle key retrieval and token management logic
        val projectJWT = get("PurLogProjectJWT")
        if (projectJWT.isNullOrEmpty()) {
            return@withContext Result.failure(
                PurLogException(PurLogError.error("Failed to initialize PurLog", "Invalid project JWT", PurLogLevel.ERROR))
            )
        }

        var uuid: String? = get("PurLogSessionUUID")
        if (uuid.isNullOrEmpty()) {
            SdkLogger.shared.log(PurLogLevel.VERBOSE, "PurLogSessionUUID not found in Keychain. Creating a new one...")
            uuid = createUUIDIfNotExists()
            if (uuid.isNullOrEmpty()) {
                return@withContext Result.failure(
                    PurLogException(PurLogError.error("Failed to initialize PurLog", "Invalid UUID", PurLogLevel.ERROR))
                )
            }
            SdkLogger.shared.log(PurLogLevel.VERBOSE, "PurLogSessionUUID created.")
        }

        var sessionJWT = get("PurLogSessionJWT")
        if (sessionJWT.isNullOrEmpty()) {
            val tokenResult = createToken(projectJWT, uuid, projectId)
            sessionJWT = tokenResult.getOrNull()
            if (sessionJWT.isNullOrEmpty()) {
                return@withContext Result.failure(
                    PurLogException(PurLogError.error("Failed to initialize PurLog", "Unable to create session token", PurLogLevel.ERROR))
                )
            }
        }

        refreshTokenIfExpired(projectJWT, sessionJWT, projectId)
        SdkLogger.shared.log(PurLogLevel.INFO, "PurLog initialized with projectId $projectId!")
        isInitialized = true
        Result.success(Unit)
    }

    suspend fun verbose(message: String, metadata: Map<String, String> = emptyMap()) {
        log(message, metadata, PurLogLevel.VERBOSE)
    }

    suspend fun debug(message: String, metadata: Map<String, String> = emptyMap()) {
        log(message, metadata, PurLogLevel.DEBUG)
    }

    suspend fun info(message: String, metadata: Map<String, String> = emptyMap()) {
        log(message, metadata, PurLogLevel.INFO)
    }

    suspend fun warn(message: String, metadata: Map<String, String> = emptyMap()) {
        log(message, metadata, PurLogLevel.WARN)
    }

    suspend fun error(message: String, metadata: Map<String, String> = emptyMap()) {
        log(message, metadata, PurLogLevel.ERROR)
    }

    suspend fun fatal(message: String, metadata: Map<String, String> = emptyMap()) {
        log(message, metadata, PurLogLevel.FATAL)
    }

    private suspend fun log(message: String, metadata: Map<String, String>, level: PurLogLevel) {
        if (!isInitialized) {
            SdkLogger.shared.log(PurLogLevel.ERROR, "Log failed. PurLog must be initialized")
            return
        }
        if (!shouldLog(level, config.level)) return

        SdkLogger.shared.consoleLog(config.env, level, message, metadata, false)

        val projectId = config.projectId ?: return
        postLog(
            projectId = projectId,
            env = config.env,
            logLevel = level,
            message = message,
            metadata = metadata,
            deviceInfo = runTimeDeviceInfo,
            appVersion = appVersion
        )
    }
}