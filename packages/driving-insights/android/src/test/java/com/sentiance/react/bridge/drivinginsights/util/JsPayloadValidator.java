package com.sentiance.react.bridge.drivinginsights.util;

import com.facebook.react.bridge.JavaOnlyMap;

interface JsPayloadValidator<T> {
  void validate(T expected, JavaOnlyMap actual);
}
