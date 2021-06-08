package de.mathism.opensky.api

import com.google.inject.Inject
import de.mathism.abstractions.logging.ILogger
import de.mathism.opensky.api.dto.BoundingBox
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.CompletableFuture


class AllStateVectorsClient @Inject constructor(
    private val openskyApi: BaseOpenskyApiClient,
    private val logService: ILogger
) {
    fun getAllStateVectorsAsString(): CompletableFuture<String> {
        val url = "/states/all"

        return openskyApi
            .getAsync(url)
    }

    fun getAllStateVectors(): CompletableFuture<JSONArray> = getAllStateVectorsAsString()
        .thenApply { makeJsonArray(it) }

    private fun makeJsonArray(input: String) = JSONObject(input).getJSONArray("states")

    fun getStateVectorsByBoundsAsString(box: BoundingBox): CompletableFuture<String> {

        val queryString =
            "lamin=${box.minLatitude}&lomin=${box.minLongitude}&lamax=${box.maxLatitude}&lomax=${box.maxLongitude}"
        logService.logDebug("run request for box: $queryString")

        val url =
            "/states/all?$queryString"

        return openskyApi
            .getAsync(url)
    }

    fun getStateVectorsByBounds(box: BoundingBox): CompletableFuture<JSONArray> = getStateVectorsByBoundsAsString(box)
        .thenApply { makeJsonArray(it) }

    fun getStateVectorsByIcaoAsString(icao: String): CompletableFuture<String> {
        logService.logDebug("run request for icao: $icao")

        val queryString =
            "icao24=${icao.trim()}"
        val url =
            "/states/all?$queryString"

        return openskyApi
            .getAsync(url)
    }

    fun getStateVectorsByIcao(icao: String): CompletableFuture<JSONArray> = getStateVectorsByIcaoAsString(icao)
        .thenApply { makeJsonArray(it) }

}
