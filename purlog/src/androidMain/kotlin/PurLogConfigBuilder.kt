package com.metashark.purlog.models

import com.metashark.purlog.models.PurLogConfig
import com.metashark.purlog.utils.deviceInfo
import com.metashark.purlog.utils.getClientVersion
import android.content.Context

fun PurLogConfig.Builder.setAndroidContext(context: Context): PurLogConfig.Builder {
    this.runTimeDeviceInfo = deviceInfo(context)
    this.appVersion = getClientVersion(context)
    return this
}