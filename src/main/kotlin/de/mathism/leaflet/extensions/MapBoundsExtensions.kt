package de.mathism.leaflet.extensions

import de.saring.leafletmap.LeafletMapView
import de.mathism.leaflet.dto.MapBounds
import de.mathism.leaflet.events.ViewportChangeEventListener
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

fun LeafletMapView.emitMapBounds() {
    val webEngine = getWebEngineWithViewportChangeEmitter()

    webEngine.executeScript("var bounds = myMap.getBounds(); document.viewportChangeEmitter.emit(bounds.getNorthWest().lat, bounds.getNorthWest().lng, bounds.getNorthEast().lat, bounds.getNorthEast().lng, bounds.getSouthEast().lat, bounds.getSouthEast().lng, bounds.getSouthWest().lat, bounds.getSouthWest().lng);")
}
