package com.metashark.purlog.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

// Actual implementation for JVM
actual val ioDispatcher: CoroutineDispatcher = Dispatchers.Default