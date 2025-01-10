package org.example.project

import org.example.project.ILogger

actual fun createLogger(): ILogger = DesktopLogger()