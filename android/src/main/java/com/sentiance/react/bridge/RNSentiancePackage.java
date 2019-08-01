package com.sentiance.react.bridge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.facebook.react.bridge.JavaScriptModule;
import com.sentiance.sdk.MetaUserLinker;
import com.sentiance.sdk.OnSdkStatusUpdateHandler;
import com.sentiance.sdk.SdkStatus;

public class RNSentiancePackage implements ReactPackage {

    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        List<NativeModule> modules = new ArrayList<>();
        RNSentianceModule rnSentianceModule = new RNSentianceModule(reactContext);
        modules.add(rnSentianceModule);
        return modules;
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        return Collections.emptyList();
    }
}
