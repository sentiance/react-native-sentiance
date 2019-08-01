package com.sentiance.react.bridge;

import android.content.Context;

import com.facebook.react.ReactApplication;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.sentiance.sdk.SdkStatus;
import com.sentiance.sdk.detectionupdates.UserActivity;

import static com.sentiance.react.bridge.RNSentianceConverter.*;

public class RNSentianceEmitter {
    private final String USER_LINK = "SDKUserLink";
    private final String STATUS_UPDATE = "SDKStatusUpdate";
    private Context mContext;
    private static final String USER_ACTIVITY_UPDATE = "SDKUserActivityUpdate";

    private ReactContext reactContext;

    public RNSentianceEmitter(Context context) {
        mContext = context;
    }

    public void sendUserLinkEvent(String installId) {
        sendEvent(USER_LINK, convertInstallId(installId));
    }

    public void sendStatusUpdateEvent(SdkStatus status) {
        sendEvent(STATUS_UPDATE, convertSdkStatus(status));
    }

    public void sendUserActivityUpdate(UserActivity userActivity) {
        sendEvent(USER_ACTIVITY_UPDATE, convertUserActivity(userActivity));
    }

    private void sendEvent(final String key, final WritableMap map) {
        ReactApplication reactApplication = ((ReactApplication) mContext.getApplicationContext());
        ReactNativeHost reactNativeHost = reactApplication.getReactNativeHost();
        reactContext = reactNativeHost.getReactInstanceManager().getCurrentReactContext();
        if (reactContext != null && reactContext.hasActiveCatalystInstance()) {
            reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(key, map);
        }
    }
}
