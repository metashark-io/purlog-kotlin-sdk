package com.metashark.purlog.enums

// PurLogLevel enum
enum class PurLogLevel {
    VERBOSE, // Typically records a myriad of events throughout the application.
    DEBUG,   // Information useful to developers for debugging the application.
    INFO,    // General operational entries about what's happening in the application; typically used for recording successful API executions.
    WARN,    // Indications that something unexpected happened, or indicative of some problem in the near future. The application is still working as expected.
    ERROR,   // Due to a more serious problem, the software has not been able to perform some function.
    FATAL    // Severe errors that cause premature termination. Expect these to be immediately followed by application exit.
}