package org.example.project

import kotlinx.browser.window

class WebLogger : ILogger {
    override fun debug(context: String, message: String) {
        logToConsole("DEBUG", context, message)
    }

    override fun info(context: String, message: String) {
        logToConsole("INFO", context, message)
    }

    override fun warn(context: String, message: String) {
        logToConsole("WARN", context, message)
    }

    override fun error(context: String, message: String) {
        logToConsole("ERROR", context, message)
    }

    private fun logToConsole(level: String, context: String, message: String) {
        consoleLog("$level: $context | $message")
    }
}

// External declaration for console.log
external fun consoleLog(message: String)
