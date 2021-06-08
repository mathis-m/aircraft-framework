package de.mathism.leaflet.events

import de.mathism.leaflet.dto.MapBounds
import de.mathism.leaflet.events.ViewportChangeEventListener
import java.util.ArrayList

class ViewportChangeEventMaker {
    private val listeners = ArrayList<ViewportChangeEventListener>()

    fun addListener(toAdd: ViewportChangeEventListener): Int {
        listeners.add(toAdd)
        return listeners.size - 1
    }

    fun dispose(i: Int) {
        listeners.removeAt(i)
    }

    fun notifyListeners(mapBounds: MapBounds) {
        for (hl in listeners)
            hl.onViewportChange(mapBounds)
    }
}