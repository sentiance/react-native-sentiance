package com.sentiance.react.bridge.core.common.util;

@FunctionalInterface
public interface SingleParamRunnable<T> {
  void run(T t);
}
