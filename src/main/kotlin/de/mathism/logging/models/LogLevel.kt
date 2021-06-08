package de.mathism.logging.models

import javafx.util.StringConverter
import java.util.*

enum class LogLevel(private val level: String) {
    DEBUG("DEBUG"),
    INFO("INFO"),
    WARN("WARN"),
    ERROR("ERROR");

    class LogLevelStringConverter : StringConverter<LogLevel>() {
        override fun toString(obj: LogLevel?): String {
            if (obj == null) {
                return "ERROR";
            }
            return obj.level
        }

        override fun fromString(level: String?): LogLevel = Arrays.stream(values())
            .filter { x -> x.level == level }
            .findFirst()
            .get()
    }

    override fun toString(): String {
        return level
    }
}

