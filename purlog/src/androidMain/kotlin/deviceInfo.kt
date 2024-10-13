package com.metashark.purlog.utils

import android.os.Build
import android.content.Context
import android.app.UiModeManager
import android.content.res.Configuration

private data class PurLogDeviceInfo(
    val osName: String,
    val osVersion: String
) {
    companion object {
        fun create(context: Context): PurLogDeviceInfo {
            val osName = when {
                isAndroidTV(context) -> "Android TV"
                isWearOS(context) -> "Wear OS"
                isAutomotive(context) -> "Android Automotive"
                isChromeOS() -> "Chrome OS"
                else -> "Android"
            }

            val osVersion = Build.VERSION.RELEASE ?: "Unknown Version"
            return PurLogDeviceInfo(osName, osVersion)
        }

        private fun isAndroidTV(context: Context): Boolean {
            val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
            return uiModeManager.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION
        }

        private fun isWearOS(context: Context): Boolean {
            val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
            return uiModeManager.currentModeType == Configuration.UI_MODE_TYPE_WATCH
        }

        private fun isAutomotive(context: Context): Boolean {
            val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
            return uiModeManager.currentModeType == Configuration.UI_MODE_TYPE_CAR
        }

        private fun isChromeOS(): Boolean {
            return Build.MANUFACTURER.contains("Chromebook", ignoreCase = true) ||
                    Build.DEVICE.contains("chrome", ignoreCase = true)
        }
    }

    private fun asMap(): Map<String, String> {
        return mapOf(
            "osName" to osName,
            "osVersion" to osVersion
        )
    }
}

actual fun deviceInfo(context: Any?): Map<String, String> {
    if (context is Context) {
        return PurLogDeviceInfo.create(context).asMap()
    } else {
        return emptyMap()
    }
}