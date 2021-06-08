package de.mathism.leaflet.extensions

import de.saring.leafletmap.LatLong
import de.saring.leafletmap.LeafletMapView
import de.saring.leafletmap.Marker

fun LeafletMapView.initRotatableMarkersJs() {
    val webEngine = webEngine()
    webEngine.executeScript(
        "(function() {\n" +
                "    // save these original methods before they are overwritten\n" +
                "    var proto_initIcon = L.Marker.prototype._initIcon;\n" +
                "    var proto_setPos = L.Marker.prototype._setPos;\n" +
                "\n" +
                "    var oldIE = (L.DomUtil.TRANSFORM === 'msTransform');\n" +
                "\n" +
                "    L.Marker.addInitHook(function () {\n" +
                "        var iconOptions = this.options.icon && this.options.icon.options;\n" +
                "        var iconAnchor = iconOptions && this.options.icon.options.iconAnchor;\n" +
                "        if (iconAnchor) {\n" +
                "            iconAnchor = (iconAnchor[0] + 'px ' + iconAnchor[1] + 'px');\n" +
                "        }\n" +
                "        this.options.rotationOrigin = this.options.rotationOrigin || iconAnchor || 'center center' ;\n" +
                "        this.options.rotationAngle = this.options.rotationAngle || 0;\n" +
                "\n" +
                "        // Ensure marker keeps rotated during dragging\n" +
                "        this.on('drag', function(e) { e.target._applyRotation(); });\n" +
                "    });\n" +
                "\n" +
                "    L.Marker.include({\n" +
                "        _initIcon: function() {\n" +
                "            proto_initIcon.call(this);\n" +
                "        },\n" +
                "\n" +
                "        _setPos: function (pos) {\n" +
                "            proto_setPos.call(this, pos);\n" +
                "            this._applyRotation();\n" +
                "        },\n" +
                "\n" +
                "        _applyRotation: function () {\n" +
                "            if(this.options.rotationAngle) {\n" +
                "                this._icon.style[L.DomUtil.TRANSFORM+'Origin'] = this.options.rotationOrigin;\n" +
                "\n" +
                "                if(oldIE) {\n" +
                "                    // for IE 9, use the 2D rotation\n" +
                "                    this._icon.style[L.DomUtil.TRANSFORM] = 'rotate(' + this.options.rotationAngle + 'deg)';\n" +
                "                } else {\n" +
                "                    // for modern browsers, prefer the 3D accelerated version\n" +
                "                    this._icon.style[L.DomUtil.TRANSFORM] += ' rotateZ(' + this.options.rotationAngle + 'deg)';\n" +
                "                }\n" +
                "            }\n" +
                "        },\n" +
                "\n" +
                "        setRotationAngle: function(angle) {\n" +
                "            this.options.rotationAngle = angle;\n" +
                "            this.update();\n" +
                "            return this;\n" +
                "        },\n" +
                "\n" +
                "        setRotationOrigin: function(origin) {\n" +
                "            this.options.rotationOrigin = origin;\n" +
                "            this.update();\n" +
                "            return this;\n" +
                "        }\n" +
                "    });\n" +
                "})();"
    )
}

fun LeafletMapView.addMarker(marker: Marker, angle: Int) {
    val method = LeafletMapView::class.java.getDeclaredMethod("getNextMarkerName")
    method.isAccessible = true

    val nextName = method.invoke(this) as String
    marker.addToMap(nextName, this, angle)
}

fun Marker.addToMap(nextMarkerName: String, map: LeafletMapView, angle: Int) {
    val webEngine = map.webEngine()
    setPrivateField("name", nextMarkerName)
    setPrivateField("map", map)
    setPrivateField("attached", true)
    val position = getPrivateField<LatLong>("position")
    val title = getPrivateField<String>("title")
    val marker = getPrivateField<String>("marker")
    val zIndexOffset = getPrivateField<Int>("zIndexOffset")
    val clickable = getPrivateField<Boolean>("clickable")
    webEngine.executeScript("var $nextMarkerName = L.marker([${position.latitude}, ${position.longitude}], "
            + "{title: '$title', icon: ${marker}, zIndexOffset: $zIndexOffset, rotationAngle: $angle}).addTo(myMap);")
    if (clickable) {
        setClickable()
    }
}

fun Marker.rotate(angle: Int) {
    val map = getPrivateField<LeafletMapView>("map")
    val name = getPrivateField<String>("name")
    val isAttached = getPrivateField<Boolean>("attached")
    if(!isAttached) return

    val webEngine = map.webEngine()
    webEngine.executeScript("$name.setRotationAngle($angle);")
}
fun Marker.updateRotationOrigin(origin: String) {
    val map = getPrivateField<LeafletMapView>("map")
    val name = getPrivateField<String>("name")
    val isAttached = getPrivateField<Boolean>("attached")
    if(!isAttached) return

    val webEngine = map.webEngine()
    webEngine.executeScript("$name.setRotationOrigin($origin);")
}

private fun Marker.setPrivateField(name: String, value: Any?) {
    val field = Marker::class.java.getDeclaredField(name)
    field.isAccessible = true
    field.set(this, value)
}

private inline fun <reified TRetValue: Any> Marker.getPrivateField(name: String): TRetValue {
    val field = Marker::class.java.getDeclaredField(name)
    field.isAccessible = true
    val retVal = field.get(this)
    return if (retVal is TRetValue) retVal else throw UnknownError("Could not cast private field value '$name'.")
}