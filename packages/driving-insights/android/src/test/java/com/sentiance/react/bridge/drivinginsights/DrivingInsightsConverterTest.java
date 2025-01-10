package com.sentiance.react.bridge.drivinginsights;

import com.facebook.react.bridge.JavaOnlyMap;
import com.sentiance.react.bridge.drivinginsights.util.validators.HarshEventBridgeValidator;
import com.sentiance.react.bridge.test.ReactNativeTest;
import com.sentiance.sdk.drivinginsights.api.HarshDrivingEvent;
import com.sentiance.sdk.util.DateTime;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

// TODO: add unit tests
@RunWith(RobolectricTestRunner.class)
public class DrivingInsightsConverterTest extends ReactNativeTest {
  private DrivingInsightsConverter converter;

  @Before
  @Override
  public void setUp() throws Exception {
    super.setUp();
    converter = new DrivingInsightsConverter();
  }

  @Test
  public void testConvertHarshDrivingEvent() {
    HarshDrivingEvent harshDrivingEvent = new HarshDrivingEvent(DateTime.now(), DateTime.now(), HarshDrivingEvent.Type.ACCELERATION, 100, 20.0);
    JavaOnlyMap transformedEvent = (JavaOnlyMap) converter.convertHarshDrivingEvent(harshDrivingEvent);
    new HarshEventBridgeValidator().validate(transformedEvent, harshDrivingEvent);
  }
}
