package com.sentiance.react.bridge.core.common.base;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.sentiance.react.bridge.core.common.util.ReactContextProvider;

public abstract class AbstractSentianceEmitter {

    protected final Handler mHandler = new Handler(Looper.getMainLooper());
    @Nullable
    protected ReactContext reactContext;
    @NonNull
    protected ReactContextProvider reactContextProvider;

    protected AbstractSentianceEmitter(Context context) {
        reactContextProvider = new ReactContextProvider(context.getApplicationContext());
        reactContext = reactContextProvider.createReactContext();
    }

    protected void sendEvent(final String key, final WritableMap map) {
        if (reactContext != null && reactContext.hasActiveCatalystInstance()) {
            this.reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(key, map);
        } else {
            // add delay
            final Counter retry = new Counter(20);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (AbstractSentianceEmitter.this.reactContext == null)
                        AbstractSentianceEmitter.this.reactContext = reactContextProvider.createReactContext();
                    if (AbstractSentianceEmitter.this.reactContext != null && AbstractSentianceEmitter.this.reactContext.hasActiveCatalystInstance()) {
                        AbstractSentianceEmitter.this.reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(key, map);
                    } else if (retry.count-- > 0) {
                        mHandler.postDelayed(this, 500);
                    }
                }
            }, 500);
        }
    }

    private static class Counter {
        int count;

        Counter(int count) {
            this.count = count;
        }
    }
}
