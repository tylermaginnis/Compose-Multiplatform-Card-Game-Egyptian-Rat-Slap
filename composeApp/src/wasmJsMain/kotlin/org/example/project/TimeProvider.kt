package org.example.project

import kotlinx.browser.window

class WebTimeProvider : ITimeProvider {
    override fun getCurrentTimeMillis(): Long {
        // Use the window object from kotlinx-browser to get the current time
        return window.performance.now().toLong()
    }
}