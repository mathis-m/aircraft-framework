package de.mathism.opensky.services

import de.mathism.abstractions.logging.ILogger
import de.mathism.leaflet.dto.MapBounds
import de.mathism.opensky.api.AllStateVectorsClient
import de.mathism.opensky.extensions.toOpenskyBoundingBox
import org.json.JSONArray
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit


class LiveAircraftService(
    private val fetchDelay: Long,
    private val allStateVectorsClient: AllStateVectorsClient,
    private val logger: ILogger
) {
    constructor(
        fetchDelay: Long,
        allStateVectorsClient: AllStateVectorsClient,
        logger: ILogger,
        useStorage: Boolean
    ) : this(fetchDelay, allStateVectorsClient, logger) {
        this.useStorage = useStorage
    }

    var useStorage: Boolean = true
    private var currentMapBounds: MapBounds? = null

    private var threadPool = ScheduledThreadPoolExecutor(2)
    private val jsonQ = LinkedBlockingQueue<JSONArray>()

    fun run() {
        logger.logDebug("Starting fetching aircraft sets from opensky-api.")
        threadPool.scheduleAtFixedRate(FetchAndBroadcastTask(), 0, fetchDelay, TimeUnit.MILLISECONDS)
    }

    fun stop() {
        logger.logDebug("Stopping fetching aircraft sets.")
        threadPool.shutdown()
        threadPool = ScheduledThreadPoolExecutor(2)
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

    private val subs: MutableCollection<(JSONArray) -> Unit> = mutableListOf()
    private val latestVal: JSONArray? = null

    interface JSONArrayDelegate {
        fun accept(i: JSONArray)
    }

    fun subscribeToAircraftFeed(delegate: JSONArrayDelegate) {
        subs.add(delegate::accept)
        if (latestVal != null) {
            delegate.accept(latestVal)
        }
    }

    private fun notifyAllSubs(planes: JSONArray) {
        subs.forEach {
            it(planes)
        }
    }

    open inner class FetchAndBroadcastTask : Runnable {
        override fun run() {
            val bounds = currentMapBounds ?: return

            allStateVectorsClient
                .getStateVectorsByBounds(bounds.toOpenskyBoundingBox())
                .thenAccept {
                    if (useStorage) {
                        jsonQ.add(it)
                    }
                    notifyAllSubs(it)
                }
        }
    }
}