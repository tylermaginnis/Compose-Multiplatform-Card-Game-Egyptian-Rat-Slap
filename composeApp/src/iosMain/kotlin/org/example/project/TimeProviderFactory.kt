package org.example.project

actual fun createTimeProvider(): TimeProvider = IosTimeProvider()