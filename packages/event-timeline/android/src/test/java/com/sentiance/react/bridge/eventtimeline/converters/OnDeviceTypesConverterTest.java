package com.sentiance.react.bridge.eventtimeline.converters;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.JavaOnlyMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.sentiance.react.bridge.eventtimeline.validators.TransportTagsValidator;
import com.sentiance.react.bridge.test.ReactNativeTest;
import com.sentiance.react.bridge.eventtimeline.validators.EventBridgeValidator;
import com.sentiance.react.bridge.test.validators.GeoLocationBridgeValidator;
import com.sentiance.react.bridge.test.validators.VenueBridgeValidator;
import com.sentiance.react.bridge.test.validators.WaypointBridgeValidator;
import com.sentiance.sdk.ondevice.api.GeoLocation;
import com.sentiance.sdk.ondevice.api.Waypoint;
import com.sentiance.sdk.ondevice.api.event.Event;
import com.sentiance.sdk.ondevice.api.event.OffTheGridEvent;
import com.sentiance.sdk.ondevice.api.event.StationaryEvent;
import com.sentiance.sdk.ondevice.api.event.TransportEvent;
import com.sentiance.sdk.ondevice.api.event.TransportMode;
import com.sentiance.sdk.ondevice.api.venue.Venue;
import com.sentiance.sdk.ondevice.api.venue.VenueSignificance;
import com.sentiance.sdk.ondevice.api.venue.VenueType;
import com.sentiance.sdk.util.DateTime;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(RobolectricTestRunner.class)
public class OnDeviceTypesConverterTest extends ReactNativeTest {
    private OnDeviceTypesConverter converter;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        converter = new OnDeviceTypesConverter();
    }

    @Test
    public void testConvertEvents() {
        OffTheGridEvent otg = new OffTheGridEvent("otg", DateTime.now(), DateTime.fromMillis(now() + 2000), DateTime.now());
        StationaryEvent stationary = new StationaryEvent("stationary1", DateTime.now(), DateTime.fromMillis(now() + 2000), DateTime.now(),
            newVenue(newGeoLocation()), null);
        List<Event> events = Arrays.asList(otg, stationary);
        WritableArray transformedEvents = converter.convertEvents(events);
        assertEquals(events.size(), transformedEvents.size());
    }

    @Test
    public void testConvertEvent() {
        OffTheGridEvent otg = new OffTheGridEvent("otg", DateTime.now(), DateTime.fromMillis(now() + 2000), DateTime.now());
        StationaryEvent stationary1 = new StationaryEvent("stationary1", DateTime.now(), DateTime.fromMillis(now() + 2000), DateTime.now(),
            newVenue(newGeoLocation()), null);
        StationaryEvent stationary2 = new StationaryEvent("stationary2", DateTime.now(), null, DateTime.now(),
            newVenue(null), newGeoLocation());
        TransportEvent transport1 = new TransportEvent("transport1", DateTime.now(), DateTime.fromMillis(now() + 2000), DateTime.now(),
            TransportMode.TRAIN, Collections.emptyList(), null, new HashMap<>());

        Map<String, String> tags = new HashMap<String, String>() {{
            put("key1", "value1");
            put("key2", "value2");
        }};
        TransportEvent transport2 = new TransportEvent("transport2", DateTime.now(), DateTime.fromMillis(now() + 2000), DateTime.now(),
            TransportMode.BUS, Collections.emptyList(), 100, tags);

        List<Event> events = Arrays.asList(otg, stationary1, stationary2, transport1, transport2);
        for (Event event : events) {
            JavaOnlyMap transformedEvent = (JavaOnlyMap) converter.convertEvent(event);
            new EventBridgeValidator().validate(event, transformedEvent);
        }
    }

    @Test
    public void testConvertVenue() {
        for (Venue venue : Arrays.asList(newVenue(), newVenue(newGeoLocation()))) {
            WritableMap transformedVenue = converter.convertVenue(venue);
            new VenueBridgeValidator().validate(venue, (JavaOnlyMap) transformedVenue);
        }
    }

    @Test
    public void testConvertWaypoint() {
        Waypoint waypoint = new Waypoint(0.54, 8.76, now(), 10, 5.5f, 7f);
        Waypoint waypointWithoutSpeedLimitInfo = new Waypoint(0.545, 8.76, now(), 10, 5.5f, -1);
        Waypoint unlimitedSpeedLimitWaypoint = mock(Waypoint.class);
        when(unlimitedSpeedLimitWaypoint.getLatitude()).thenReturn(0.56);
        when(unlimitedSpeedLimitWaypoint.getLongitude()).thenReturn(0.56);
        when(unlimitedSpeedLimitWaypoint.getTimestamp()).thenReturn(now());
        when(unlimitedSpeedLimitWaypoint.getAccuracyInMeters()).thenReturn(10);
        when(unlimitedSpeedLimitWaypoint.isSpeedLimitInfoSet()).thenReturn(true);
        when(unlimitedSpeedLimitWaypoint.hasUnlimitedSpeedLimit()).thenReturn(true);

        for (Waypoint wpt : Arrays.asList(waypoint, waypointWithoutSpeedLimitInfo, unlimitedSpeedLimitWaypoint)) {
            WritableMap transformedWaypoint = converter.convertWaypoint(wpt);
            new WaypointBridgeValidator().validate(wpt, (JavaOnlyMap) transformedWaypoint);
        }
    }

    @Test
    public void testConvertGeoLocation() {
        GeoLocation location = newGeoLocation();
        WritableMap transformedGeoLocation = converter.convertGeoLocation(location);
        new GeoLocationBridgeValidator().validate(location, (JavaOnlyMap) transformedGeoLocation);
    }

    @Test
    public void testConvertTransportTags() {
        WritableMap tags = Arguments.createMap();
        tags.putString("key1", "value1");
        tags.putString("key2", "value2");
        tags.putString("key3", "value3");
        Map<String, String> convertedTags = converter.convertTransportTags(tags);

        new TransportTagsValidator().validate((JavaOnlyMap) tags, convertedTags);
    }

    private long now() {
        return DateTime.now().getEpochTime();
    }

    private Venue newVenue() {
        return newVenue(null);
    }

    private Venue newVenue(@Nullable GeoLocation location) {
        return newVenue(VenueSignificance.POINT_OF_INTEREST, VenueType.INDUSTRIAL, location);
    }

    private Venue newVenue(VenueSignificance significance, VenueType type, @Nullable GeoLocation location) {
        return new Venue(significance, type, location);
    }

    private GeoLocation newGeoLocation() {
        return new GeoLocation(0.56, 8.56, 10);
    }
}
