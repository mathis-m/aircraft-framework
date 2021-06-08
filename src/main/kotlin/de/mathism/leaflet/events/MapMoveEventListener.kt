package de.mathism.leaflet.events

import de.mathism.leaflet.dto.MapBounds

interface ViewportChangeEventListener {
    fun onViewportChange(mapBounds: MapBounds)
}