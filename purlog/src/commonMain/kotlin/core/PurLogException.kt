package io.metashark.purlog.core

class PurLogException(error: PurLogError) : Throwable(error.message)