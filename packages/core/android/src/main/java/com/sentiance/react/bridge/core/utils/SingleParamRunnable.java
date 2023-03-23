package com.sentiance.react.bridge.core.utils;

@FunctionalInterface
public interface SingleParamRunnable<T> {
  void run(T t);
}
