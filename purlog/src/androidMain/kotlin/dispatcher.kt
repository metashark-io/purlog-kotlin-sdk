package com.metashark.purlog.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

// Actual implementation for Android
actual val ioDispatcher: CoroutineDispatcher = Dispatchers.IO