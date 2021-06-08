package de.mathism.leaflet.extensions

import de.saring.leafletmap.LeafletMapView
import javafx.scene.web.WebEngine

internal fun LeafletMapView.webEngine(): WebEngine {
    val field = LeafletMapView::class.java.getDeclaredField("webEngine")
    field.isAccessible = true
    return field.get(this) as WebEngine
}