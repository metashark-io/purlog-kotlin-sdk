package com.metashark.purlog.core

class PurLogException(error: PurLogError) : Throwable(error.message)