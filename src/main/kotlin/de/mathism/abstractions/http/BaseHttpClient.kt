package de.mathism.abstractions.http

import de.mathism.abstractions.http.exception.HttpClientException
import java.io.*
import java.lang.reflect.Type
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.*
import java.util.concurrent.CompletableFuture


abstract class BaseHttpClient {
    companion object {
        const val GENERIC_ERROR: String = "Could not parse the response from server."
    }

    open var baseUri: String = ""
    private val client: HttpClient = HttpClient.newHttpClient()

    private fun httpGetRequest(url: String): HttpRequest = HttpRequest.newBuilder()
        .uri(URI.create(baseUri + url))
        .header("Content-Type", "application/json")
        .GET()
        .build()

    fun getAsync(uri: String): CompletableFuture<String> {

        val getRequest = httpGetRequest(uri)
        return client
            .sendAsync(getRequest, HttpResponse.BodyHandlers.ofString())
            .thenApply(HttpResponse<String>::body)
    }
}

