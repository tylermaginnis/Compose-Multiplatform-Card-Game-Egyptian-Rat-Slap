package org.example.project

interface ILogger {
    fun debug(context: String, message: String)
    fun info(context: String, message: String)
    fun warn(context: String, message: String)
    fun error(context: String, message: String)
}