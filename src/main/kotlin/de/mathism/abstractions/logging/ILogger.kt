package de.mathism.abstractions.logging

interface ILogger {
    fun log(message: String)
    fun logDebug(message: String)
    fun logWarn(message: String)
    fun logError(message: String)
}