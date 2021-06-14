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

#### Using Queue Approach
Extend the sensor with a new property and constructor:
```java
LiveAircraftService liveAircraftService = null;
public Senser(LiveAircraftService liveAircraftService)
{
	this.liveAircraftService = liveAircraftService;
}
```

Use the `LiveAircraftService` within the Sensor Runnable `run()` method in order to do it better than the Profs did in the provided base solution:
The service is based on scheduledthreadpoolexecutor and does not rely on in memory filtering(like PlaneDataServer provided by Prof, which is bad because the api needs to evaluate all planes arround the world). Instead it just requests planes for a given area.
This is a much more stable and consistent solution.

```java
planeArray = liveAircraftService.getPlaneArray();
if(planeArray == null)
	continue;
```
#### Using Behavioral Subject approach (specific type of observable)
Using a Queue to store the fetched aircraft feed and then iterating over it seems a bit odd.
It might make more sense to just use the latest feed because UI should only display the latest fetched results.
This brings me to the point where the Queue is obsolete, so I introduced a behavioral observable approach here.
By means, you can subscribe to the aircraft feed by providing a lambda, the lambda will be called with the latest emitted value and further on with each newly received value.
In addition, we do not need the Sensor to be run on a thread, instead it will just encapsulate the spread of the aircraft feed further into your pipeline.  
In order to not bloat memory you have to initialize the `LiveAircraftService` with a 4th argument `false` in order to disable queuing received aircraft feeds:
```
Logger logger = new Logger();
BaseOpenskyApiClient baseClient = new BaseOpenskyApiClient(baseUrl);
AllStateVectorsClient client = new AllStateVectorsClient(baseClient, logger);
LiveAircraftService liveAircraftService = new LiveAircraftService(5000, client, logger, false);
```

Sensor does not need to be a `Runnable` so remove the inheritance.

Example updated sensor:
````java
public class Senser2 extends SimpleObservable<JSONArray> {
    LiveAircraftService liveAircraftService;

    public Senser2(LiveAircraftService liveAircraftService) {
        this.liveAircraftService = liveAircraftService;
    }

    public void spreadAircraftFeedIntoObservable() {
        liveAircraftService.subscribeToAircraftFeed(aircraftFeed -> {
            for (int i = 0; i < aircraftFeed.length(); i++) {
                this.setChanged();
                notifyObservers(aircraftFeed.getJSONArray(i));
            }
        });
    }
}
````

### Using the extensions provided by the Framework
define base url to use:
```java
static String baseUrl = "https://opensky-network.org/api";
```
initialize the sensor with `LiveAircraftService` instance, by using one of the above stated approaches:

```java
Logger logger = new Logger();
BaseOpenskyApiClient baseClient = new BaseOpenskyApiClient(baseUrl);
AllStateVectorsClient client = new AllStateVectorsClient(baseClient, logger);
LiveAircraftService liveAircraftService = new LiveAircraftService(5000, client, logger);

// just showcasing the two approaches here pick the one you prefer
if(!liveAircraftService.getUseStorage()) {
    senser2 = new Senser2(liveAircraftService);
    senser2.spreadAircraftFeedIntoObservable();
    senser2.addObserver(messer);
} else {
    senser = new Senser(liveAircraftService);
}
```
create update bounds handler and start fetching aircrafts when first bounds are emitted:
```java

private boolean firstBoundsReceived = false;
private void onViewportChange(MapBounds bounds) {
    liveAircraftService.updateMapBounds(bounds);

    if(!firstBoundsReceived){
        firstBoundsReceived = true;
        liveAircraftService.run();
    }
}
```
initialize the map:
```java
// create update bounds handler

// later on init Leaflet map
cfMapLoadState.whenComplete((state, throwable) -> {
    if (state == Worker.State.SUCCEEDED) {
        mapIsInitialized = true;
        mapView.setView(new LatLong(52.5172, 13.4040), 9);
        mapView.addCustomMarker("plane", "icons/basicplane.png");

        // setup Map Viewport listerner in order to update the area that you want to fetch plains for:
        ViewPortExtensionsKt.onViewportChange(mapView, this::onViewportChange);
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
