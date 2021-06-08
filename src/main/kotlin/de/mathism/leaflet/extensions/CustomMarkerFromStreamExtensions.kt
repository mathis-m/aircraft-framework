package de.mathism.leaflet.extensions

import de.saring.leafletmap.LeafletMapView
import java.io.IOException
import java.io.InputStream
import java.util.*

// better rely on stream this makes it possible to use getResourceAsStream which is the recommended way of loading resources like pictures
fun LeafletMapView.addCustomMarker(markerName: String, inputStream: InputStream): String {
    val webEngine = webEngine()
    var imageString: String? = null
    try {
        val imageBytes = inputStream.readAllBytes()
        inputStream.close()
        val encoder = Base64.getEncoder()
        imageString = encoder.encodeToString(imageBytes)
    } catch (e: IOException) {
        e.printStackTrace()
    }

    webEngine.executeScript(
        "var $markerName = L.icon({\n" +
            "iconUrl: '${"data:image/png;base64,$imageString"}',\n" +
            "iconSize: [24, 24],\n" +
            "iconAnchor: [12, 12],\n" +
        "});"
    )
    return markerName
}