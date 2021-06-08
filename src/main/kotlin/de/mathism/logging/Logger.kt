package de.mathism.logging

import de.mathism.abstractions.logging.ILogger
import de.mathism.logging.models.LogLevel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Logger() : ILogger {
    override fun log(message: String) = log(message, LogLevel.INFO)
    override fun logDebug(message: String) = log(message, LogLevel.DEBUG)
    override fun logWarn(message: String) = log(message, LogLevel.WARN)
    override fun logError(message: String) = log(message, LogLevel.ERROR)
    private fun log(message: String, level: LogLevel) {
        val time = LocalDateTime.now().withNano(0)
        val timestamp = getTimestamp(time)
        println("$timestamp [$level] $message")
    }
    private fun getTimestamp(time: LocalDateTime): String {
        val dtf = DateTimeFormatter.ofPattern("[HH:mm:ss]")
        return dtf.format(time)
    }
}
