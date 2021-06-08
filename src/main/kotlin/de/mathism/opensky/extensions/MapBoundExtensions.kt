package de.mathism.opensky.extensions

import de.mathism.leaflet.dto.MapBounds
import de.mathism.opensky.api.dto.BoundingBox

fun MapBounds.toOpenskyBoundingBox() = BoundingBox(
    lowerLeftCorner.latitude,
    upperRightCorner.latitude,
    lowerLeftCorner.longitude,
    upperRightCorner.longitude,
)