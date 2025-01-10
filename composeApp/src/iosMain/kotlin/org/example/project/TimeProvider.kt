package org.example.project

import platform.Foundation.NSDate

class iosTimeProvider : ITimeProvider {
    override fun getCurrentTimeMillis(): Long = NSDate().timeIntervalSince1970 * 1000
}