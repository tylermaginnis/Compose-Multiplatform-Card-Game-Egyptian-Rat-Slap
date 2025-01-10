package org.example.project

import android.util.Log

class AndroidLogger : ILogger {
    override fun debug(context: String, message: String) {
        Log.d(context, message)
    }

    override fun info(context: String, message: String) {
        Log.i(context, message)
    }

    override fun warn(context: String, message: String) {
        Log.w(context, message)
    }

    override fun error(context: String, message: String) {
        Log.e(context, message)
    }
}