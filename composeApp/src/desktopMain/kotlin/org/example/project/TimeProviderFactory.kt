package org.example.project

actual fun createTimeProvider(): ITimeProvider = JvmTimeProvider()