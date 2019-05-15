package com.sentiance.react.bridge;

import android.support.annotation.NonNull;

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

    private final CountDownLatch metaUserLinkLatch = new CountDownLatch(1);
    private MetaUserLinker sentianceModuleMetaUserLinker = null;
    private OnSdkStatusUpdateHandler sentianceModuleOnSdkStatusUpdateHandler = null;

    private OnSdkStatusUpdateHandler onSdkStatusUpdateHandler = new OnSdkStatusUpdateHandler() {
        @Override
        public void onSdkStatusUpdate(SdkStatus status) {
            if (sentianceModuleOnSdkStatusUpdateHandler != null)
                sentianceModuleOnSdkStatusUpdateHandler.onSdkStatusUpdate(status);
        }
    };

    private MetaUserLinker metaUserLinker = new MetaUserLinker() {
        @Override
        public boolean link(String installId) {
            try {
                metaUserLinkLatch.await();
                return sentianceModuleMetaUserLinker.link(installId);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
        }
    };

    @Override
    @NonNull
    public List<NativeModule> createNativeModules(@NonNull ReactApplicationContext reactContext) {


        List<NativeModule> modules = new ArrayList<>();
        RNSentianceModule rnSentianceModule = new RNSentianceModule(reactContext);
        modules.add(rnSentianceModule);
        sentianceModuleOnSdkStatusUpdateHandler = rnSentianceModule.getSdkStatusUpdateHandler();
        sentianceModuleMetaUserLinker = rnSentianceModule.getMetaUserLinker();
        metaUserLinkLatch.countDown();
        return modules;
    }

    // Deprecated from RN 0.47
    public List<Class<? extends JavaScriptModule>> createJSModules() {
        return Collections.emptyList();
    }

    @Override
    @NonNull
    public List<ViewManager> createViewManagers(@NonNull ReactApplicationContext reactContext) {
        return Collections.emptyList();
    }

    public OnSdkStatusUpdateHandler getOnSdkStatusUpdateHandler() {
        return onSdkStatusUpdateHandler;
    }


    public MetaUserLinker getMetaUserLinker() {
        return metaUserLinker;
    }
}
