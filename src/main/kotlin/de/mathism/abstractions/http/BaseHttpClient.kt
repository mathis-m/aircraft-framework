package de.mathism.abstractions.http

import de.mathism.okhttp3.extensions.executeAsync
import okhttp3.*
import java.util.concurrent.CompletableFuture



abstract class BaseHttpClient {
    val client = OkHttpClient()

    open var baseUri: String = ""

    open fun httpGetRequest(url: String): Request = Request.Builder()
        .url(baseUri + url)
        .addHeader("Content-Type", "application/json")
        .get()
        .build()

    open fun getAsync(uri: String): CompletableFuture<String> {
        val getRequest = httpGetRequest(uri)
        return client
            .newCall(getRequest)
            .executeAsync()
            .thenApply { x -> x.body?.string() }
    }
}

