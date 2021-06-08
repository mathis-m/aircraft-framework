package de.mathism.abstractions.http.exception

class HttpClientException(message: String, e: Throwable?) : RuntimeException(message, e) {
    constructor(message: String) : this(message, null)
}