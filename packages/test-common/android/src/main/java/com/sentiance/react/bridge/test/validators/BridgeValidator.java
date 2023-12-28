package com.sentiance.react.bridge.test.validators;

import com.facebook.react.bridge.JavaOnlyMap;

public interface BridgeValidator<T> {
  void validate(T expected, JavaOnlyMap actual);
}
