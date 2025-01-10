package org.example.project

import platform.Foundation.NSLog

class iosLogger : ILogger {
    override fun debug(context: String, message: String) {
        NSLog("DEBUG: $context | $message")
    }

    override fun info(context: String, message: String) {
        NSLog("INFO: $context | $message")
    }

    override fun warn(context: String, message: String) {
        NSLog("WARN: $context | $message")
    }

    override fun error(context: String, message: String) {
        NSLog("ERROR: $context | $message")
    }
}