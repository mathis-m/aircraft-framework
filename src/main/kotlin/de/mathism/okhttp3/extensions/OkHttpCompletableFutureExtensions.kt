package de.mathism.okhttp3.extensions

import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.CompletableFuture

fun Call.executeAsync(): CompletableFuture<Response> {
    val future = CompletableFuture<Response>()
    enqueue(object : Callback {
        override fun onResponse(call: Call, response: Response) {
            future.complete(response)
        }
        override fun onFailure(call: Call, e: IOException) {
            future.completeExceptionally(e)
        }
    })
    return future
}