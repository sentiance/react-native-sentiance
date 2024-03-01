package com.sentiance.react.bridge.core;

import androidx.annotation.NonNull;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.sentiance.react.bridge.core.common.SentianceSubscriptionsManager;
import com.sentiance.sdk.Sentiance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SentiancePackage implements ReactPackage {

  @NonNull
  @Override
  public List<NativeModule> createNativeModules(@NonNull ReactApplicationContext reactContext) {
    List<NativeModule> modules = new ArrayList<>();

    SentianceModule sentianceModule = new SentianceModule(
        reactContext,
        Sentiance.getInstance(reactContext),
        new SentianceSubscriptionsManager(),
        SentianceHelper.getInstance(reactContext),
        new SentianceEmitter(reactContext),
        new SentianceConverter()
    );
    modules.add(sentianceModule);
    return modules;
  }

  @NonNull
  @Override
  public List<ViewManager> createViewManagers(@NonNull ReactApplicationContext reactContext) {
    return Collections.emptyList();
  }
}
