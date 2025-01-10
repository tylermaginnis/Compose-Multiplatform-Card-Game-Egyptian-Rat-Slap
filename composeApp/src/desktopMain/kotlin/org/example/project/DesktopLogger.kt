package org.example.project

import java.util.logging.Level
import java.util.logging.Logger

class DesktopLogger : ILogger {
    private val logger = Logger.getLogger("KotlinProject")

    override fun debug(context: String, message: String) {
        logger.log(Level.FINE, context + " | " + message)
    }

    override fun info(context: String, message: String) {
        logger.log(Level.INFO, context + " | " + message)
    }

    override fun warn(context: String, message: String) {
        logger.log(Level.WARNING, context + " | " + message)
    }

    override fun error(context: String, message: String) {
        logger.log(Level.SEVERE, context + " | " + message)
    }
}