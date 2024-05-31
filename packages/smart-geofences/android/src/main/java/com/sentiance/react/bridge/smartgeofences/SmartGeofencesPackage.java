package com.sentiance.react.bridge.smartgeofences;

import androidx.annotation.NonNull;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.sentiance.react.bridge.core.SentianceConverter;
import com.sentiance.react.bridge.core.common.SentianceSubscriptionsManager;
import com.sentiance.react.bridge.smartgeofences.converters.SmartGeofencesConverter;
import com.sentiance.sdk.Sentiance;
import com.sentiance.sdk.smartgeofences.api.SmartGeofenceApi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SmartGeofencesPackage implements ReactPackage {
    @NonNull
    @Override
    public List<NativeModule> createNativeModules(@NonNull ReactApplicationContext reactApplicationContext) {
        List<NativeModule> modules = new ArrayList<>();
        SmartGeofencesConverter smartGeofencesConverter = new SmartGeofencesConverter(new SentianceConverter());

        SmartGeofencesModule eventTimelineModule = new SmartGeofencesModule(
            reactApplicationContext,
            Sentiance.getInstance(reactApplicationContext),
            new SentianceSubscriptionsManager(),
            SmartGeofenceApi.getInstance(reactApplicationContext),
            new SmartGeofenceEmitter(reactApplicationContext, smartGeofencesConverter),
            smartGeofencesConverter
        );
        modules.add(eventTimelineModule);
        return modules;
    }

    @NonNull
    @Override
    public List<ViewManager> createViewManagers(@NonNull ReactApplicationContext reactApplicationContext) {
        return Collections.emptyList();
    }
}
