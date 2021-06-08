package de.mathism.opensky.services

import de.mathism.abstractions.logging.ILogger
import de.mathism.leaflet.dto.MapBounds
import de.mathism.opensky.api.AllStateVectorsClient
import de.mathism.opensky.extensions.toOpenskyBoundingBox
import org.json.JSONArray
import java.util.*
import java.util.concurrent.LinkedBlockingQueue


class LiveAircraftService(
    private val fetchDelay: Long,
    private val allStateVectorsClient: AllStateVectorsClient,
    private val logger: ILogger
) {
    private var currentMapBounds: MapBounds? = null

    private var timer: Timer? = null
    private val jsonQ = LinkedBlockingQueue<JSONArray>()

    fun run() {
        logger.logDebug("Starting fetching aircraft sets from opensky-api.")
        timer = Timer()
        timer?.scheduleAtFixedRate(FetchAndBroadcastTask(), 0, fetchDelay)
    }

    fun stop() {
        logger.logDebug("Stopping fetching aircraft sets.")
        timer?.cancel()
    }

    fun updateMapBounds(bounds: MapBounds) {
        currentMapBounds = bounds
    }

    fun getPlaneArray(): JSONArray? {
        try {
            return jsonQ.take()
        } catch (e: InterruptedException) {
            logger.logError(e.stackTraceToString())
        }
        return null
    }

    open inner class FetchAndBroadcastTask : TimerTask() {
        override fun run() {
            val bounds = currentMapBounds ?: return

            allStateVectorsClient
                .getStateVectorsByBounds(bounds.toOpenskyBoundingBox())
                .thenAccept {
                    jsonQ.add(it)
                }
        }
    }
}