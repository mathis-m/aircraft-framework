# aircraft-framework
Opensky Api connector with integration into https://github.com/N1k145/LeafletMap.
Latest release can be found here: https://github.com/mathis-m/aircraft-framework/releases/tag/1.1-ALPHA

## Hochschule Esslingen integration approach:
As this framework is evolved from a project at HS Esslingen I want to give some advice in order to integrate it.
### Prepare Senser
Extend the sensor with a new property and constructor:
```java
LiveAircraftService liveAircraftService = null;
public Senser(LiveAircraftService liveAircraftService)
{
	this.liveAircraftService = liveAircraftService;
}
```

Use the `LiveAircraftService` within the Sensor Runnable `run()` method in order to do it better than the Profs did in the provided base solution:
The service is Timer based and does not rely on in memory filtering(like PlaneDataServer provided by Prof, which is bad because the api needs to evaluate all planes arround the world). Instead it just requests planes for a given area.
This is a much more stable and consistent solution.

```java
planeArray = liveAircraftService.getPlaneArray();
if(planeArray == null)
	continue;
```


### Using the extensions provided by the Framework
define base url to use:
```java
static String baseUrl = "https://opensky-network.org/api";
```
initialize the sensor with `LiveAircraftService` instance:

```java
Logger logger = new Logger();
BaseOpenskyApiClient baseClient = new BaseOpenskyApiClient(baseUrl);
AllStateVectorsClient client = new AllStateVectorsClient(baseClient, logger);
LiveAircraftService liveAircraftService = new LiveAircraftService(5000, client, logger);
senser = new Senser(liveAircraftService);
liveAircraftService.run();
```

initialize the map:
```java
cfMapLoadState.whenComplete((state, throwable) -> {
    if (state == Worker.State.SUCCEEDED) {
        mapIsInitialized = true;
        mapView.setView(new LatLong(52.5172, 13.4040), 9);
        mapView.addCustomMarker("plane", "icons/basicplane.png");

        // setup Map Viewport listerner in order to update the area that you want to fetch plains for:
        ViewPortExtensionsKt.onViewportChange(mapView, finalLiveAircraftService::updateMapBounds);
        // initially emit Viewport for fetching because no viewport change will hapen if you dont move the map
        MapBoundsExtensionsKt.emitMapBounds(mapView);
        // initialize rotateable markers internals
        RotateableMarkerExtensionsKt.initRotatableMarkersJs(mapView);
    }
});
```

Create Markers with Rotation:
```java
// ... init latLong and icao variable 
int angle = 0;
Marker marker = new Marker(latLong, icao, "plane", 1);
RotateableMarkerExtensionsKt.addMarker(mapView, marker, angle);
```

Rotate existing Marker:
```java
// ... init marker variable to update
int angle = 0;
RotateableMarkerExtensionsKt.rotate(marker, angle);
```
