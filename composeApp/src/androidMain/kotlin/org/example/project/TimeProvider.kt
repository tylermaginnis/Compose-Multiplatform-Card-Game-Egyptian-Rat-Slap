package org.example.project

import java.util.Date

class AndroidTimeProvider : ITimeProvider {
    override fun getCurrentTimeMillis(): Long = Date().time
}