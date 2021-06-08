package de.mathism.leaflet.extensions

import de.saring.leafletmap.LatLong
import de.saring.leafletmap.LeafletMapView
import javafx.scene.web.WebEngine
import de.mathism.leaflet.dto.MapBounds
import de.mathism.leaflet.events.ViewportChangeEventListener
import de.mathism.leaflet.events.ViewportChangeEventMaker
import netscape.javascript.JSObject
import java.util.*

internal val kotlinViewportChangeEmitter = ViewportChangeEventMaker()

val LeafletMapView.viewPortChangeEvent: ViewportChangeEventMaker
    get() = kotlinViewportChangeEmitter

internal class FxViewportChangeEmitter {
    fun emit(d0: Double, d1: Double, d2: Double, d3: Double, d4: Double, d5: Double, d6: Double, d7: Double) {
        kotlinViewportChangeEmitter.notifyListeners(
            MapBounds(
                LatLong(d0, d1),
                LatLong(d2, d3),
                LatLong(d4, d5),
                LatLong(d6, d7),
            )
        )
    }
}

internal fun LeafletMapView.getWebEngineWithViewportChangeEmitter(): WebEngine {
    val webEngine = webEngine()
    val win = webEngine.executeScript("document") as JSObject

    win.setMember("viewportChangeEmitter", fxViewportChangeEmitter)
    return webEngine
}


// extension fun will leak this context in fx java callback so make it global val
internal val fxViewportChangeEmitter = FxViewportChangeEmitter()
fun LeafletMapView.onViewportChange(listener: ViewportChangeEventListener): Int {
    val webEngine = getWebEngineWithViewportChangeEmitter()

    webEngine.executeScript("myMap.on('moveend', () => { var bounds = myMap.getBounds(); document.viewportChangeEmitter.emit(bounds.getNorthWest().lat, bounds.getNorthWest().lng, bounds.getNorthEast().lat, bounds.getNorthEast().lng, bounds.getSouthEast().lat, bounds.getSouthEast().lng, bounds.getSouthWest().lat, bounds.getSouthWest().lng);});")
    return viewPortChangeEvent.addListener(listener)
}

fun LeafletMapView.disposeViewportChange(i: Int) {
    viewPortChangeEvent.dispose(i)
}


internal class FxLogger {
    // val logger = FxLogger()
    // win.setMember("logger", logger)
    // var log = (str) => {document.logger.log(str)}; log("on init cb");
    fun log(str: String) {
        println(str)
    }
}

