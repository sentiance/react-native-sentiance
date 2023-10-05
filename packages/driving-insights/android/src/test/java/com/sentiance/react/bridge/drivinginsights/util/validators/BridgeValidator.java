package com.sentiance.react.bridge.drivinginsights.util.validators;

import com.facebook.react.bridge.JavaOnlyMap;

/**
 * A component that verifies that the expected Java object is correctly bridged towards the JS side.
 * @param <T> the type of Java object against which the BridgeValidator will be validating.
 */
interface BridgeValidator<T> {
  void validate(T expected, JavaOnlyMap actual);
}
