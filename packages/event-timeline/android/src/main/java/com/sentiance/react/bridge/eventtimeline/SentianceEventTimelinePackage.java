package com.sentiance.react.bridge.eventtimeline;

import androidx.annotation.NonNull;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.sentiance.react.bridge.core.common.SentianceSubscriptionsManager;
import com.sentiance.react.bridge.eventtimeline.converters.OnDeviceTypesConverter;
import com.sentiance.sdk.Sentiance;
import com.sentiance.sdk.eventtimeline.api.EventTimelineApi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SentianceEventTimelinePackage implements ReactPackage {
  @NonNull
  @Override
  public List<NativeModule> createNativeModules(@NonNull ReactApplicationContext reactContext) {
    List<NativeModule> modules = new ArrayList<>();
    SentianceEventTimelineModule eventTimelineModule = new SentianceEventTimelineModule(
      reactContext,
      Sentiance.getInstance(reactContext),
      new SentianceSubscriptionsManager(),
      EventTimelineApi.getInstance(reactContext),
      new EventTimelineEmitter(reactContext),
      new OnDeviceTypesConverter()
    );
    modules.add(eventTimelineModule);
    return modules;
  }

  @NonNull
  @Override
  public List<ViewManager> createViewManagers(@NonNull ReactApplicationContext reactContext) {
    return Collections.emptyList();
  }
}
