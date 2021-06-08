package de.mathism.leaflet.extensions

import de.saring.leafletmap.LatLong
import de.saring.leafletmap.LeafletMapView

fun LeafletMapView.setBounds(corner1: LatLong, corner2: LatLong) {
    val webEngine = webEngine()
    webEngine.executeScript(
        "myMap.fitBounds([\n" +
                "    [${corner1.latitude}, ${corner1.longitude}],\n" +
                "    [${corner2.latitude}, ${corner2.longitude}]\n" +
                "]);"
    )
}