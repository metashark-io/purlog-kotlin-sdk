package com.metashark.purlog.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

// Actual implementation for iOS
actual val ioDispatcher: CoroutineDispatcher = Dispatchers.Default