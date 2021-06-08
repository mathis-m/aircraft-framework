package de.mathism.leaflet.dto

import de.saring.leafletmap.LatLong

data class MapBounds(
    val upperLeftCorner: LatLong,
    val upperRightCorner: LatLong,
    val lowerRightCorner: LatLong,
    val lowerLeftCorner: LatLong
)