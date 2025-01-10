package org.example.project

import java.util.Date

class JvmTimeProvider : ITimeProvider {
    override fun getCurrentTimeMillis(): Long = Date().time
}