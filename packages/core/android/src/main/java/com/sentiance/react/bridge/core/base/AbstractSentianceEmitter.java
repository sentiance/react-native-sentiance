package com.sentiance.react.bridge.core.base;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.facebook.react.ReactApplication;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

public abstract class AbstractSentianceEmitter {

  protected final Handler mHandler = new Handler(Looper.getMainLooper());
  protected ReactContext reactContext;
  protected ReactNativeHost reactNativeHost;

  public AbstractSentianceEmitter(Context context) {
    ReactApplication reactApplication = ((ReactApplication) context.getApplicationContext());
    reactNativeHost = reactApplication.getReactNativeHost();
    reactContext = createReactContext();
  }

  protected ReactContext createReactContext() {
    if (!reactNativeHost.getReactInstanceManager().hasStartedCreatingInitialContext())
      reactNativeHost.getReactInstanceManager().createReactContextInBackground();
    return reactNativeHost.getReactInstanceManager().getCurrentReactContext();
  }

  public void sendEvent(final String key, final WritableMap map) {
    if (reactContext != null && reactContext.hasActiveCatalystInstance()) {
      this.reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(key, map);
    } else {
      // add delay
      final Counter retry = new Counter(20);
      mHandler.postDelayed(new Runnable() {
        @Override
        public void run() {
          if (AbstractSentianceEmitter.this.reactContext == null)
            AbstractSentianceEmitter.this.reactContext = createReactContext();
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
    Counter(int count) {
      this.count = count;
    }

    int count;
  }
}
