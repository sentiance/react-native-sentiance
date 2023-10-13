package com.sentiance.react.bridge.drivinginsights;

import androidx.annotation.NonNull;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.sentiance.react.bridge.core.common.SentianceSubscriptionsManager;
import com.sentiance.sdk.Sentiance;
import com.sentiance.sdk.drivinginsights.api.DrivingInsightsApi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SentianceDrivingInsightsPackage implements ReactPackage {

  @NonNull
  @Override
  public List<NativeModule> createNativeModules(@NonNull ReactApplicationContext reactContext) {
    List<NativeModule> modules = new ArrayList<>();
    DrivingInsightsModule drivingInsightsModule = new DrivingInsightsModule(
      reactContext,
      Sentiance.getInstance(reactContext),
      DrivingInsightsApi.getInstance(reactContext),
      new DrivingInsightsEmitter(reactContext),
      new SentianceSubscriptionsManager());
    modules.add(drivingInsightsModule);
    return modules;
  }

  @NonNull
  @Override
  public List<ViewManager> createViewManagers(@NonNull ReactApplicationContext reactContext) {
    return Collections.emptyList();
  }
}
