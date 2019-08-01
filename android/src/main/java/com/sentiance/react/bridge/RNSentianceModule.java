package com.sentiance.react.bridge;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.LifecycleEventListener;

import android.os.Handler;
import android.os.Looper;

public class RNSentianceModule extends ReactContextBaseJavaModule implements LifecycleEventListener {

    private final RNSentianceHelper helper;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private boolean userLinkingEnabled = false;

    public RNSentianceModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.helper = new RNSentianceHelper(reactContext);
    }

    @Override
    public String getName() {
        return "RNSentiance";
    }

    @ReactMethod
    public void userLinkCallback(final Boolean linkResult, final Promise promise) {
        helper.userLinkCallback(linkResult);
        promise.resolve(null);
    }

    @ReactMethod
    public void init(final String appId, final String appSecret, final String baseURL, final Promise promise) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                helper.initSDK(appId, appSecret, baseURL, false, userLinkingEnabled, null, promise);
            }
        });
    }

    @ReactMethod
    public void initWithUserLinkingEnabled(final String appId, final String appSecret, final String baseURL, final Promise promise) {
        this.userLinkingEnabled = true;
        init(appId, appSecret, baseURL, promise);
    }

    @ReactMethod
    public void start(final Promise promise) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                helper.startSDK();
                promise.resolve(null);
            }
        });
    }

    @ReactMethod
    public void stop(final Promise promise) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                helper.stopSDK();
                promise.resolve(null);
            }
        });
    }

    @ReactMethod
    public void getInitState(final Promise promise) {
        helper.getInitState(promise);
    }


    @ReactMethod
    public void getSdkStatus(final Promise promise) {
        helper.getSdkStatus(promise);
    }

    @ReactMethod
    public void getVersion(final Promise promise) {
        helper.getVersion(promise);
    }

    @ReactMethod
    public void getUserAccessToken(final Promise promise) {
        helper.getAccessToken(promise);
    }

    @ReactMethod
    public void getUserId(final Promise promise) {
        helper.getUserId(promise);
    }

    @ReactMethod
    public void startTrip(ReadableMap metadata, int hint, final Promise promise) {
        helper.startTrip(metadata, hint, promise);
    }

    @ReactMethod
    public void stopTrip(final Promise promise) {
        helper.stopTrip(promise);
    }

    @ReactMethod
    public void addUserMetadataField(final String label, final String value, final Promise promise) {
        helper.addUserMetadataField(label, value, promise);
    }

    @ReactMethod
    public void addTripMetadata(ReadableMap inputMetadata, final Promise promise) {
        helper.addTripMetadata(inputMetadata, promise);
    }

    @ReactMethod
    public void addUserMetadataFields(ReadableMap inputMetadata, final Promise promise) {
        helper.addUserMetadataFields(inputMetadata, promise);
    }

    @ReactMethod
    public void removeUserMetadataField(final String label, final Promise promise) {
        helper.removeUserMetadataField(label, promise);
    }

    @ReactMethod
    public void submitDetections(final Promise promise) {
        helper.submitDetections(promise);
    }

    @ReactMethod
    public void getWiFiQuotaLimit(final Promise promise) {
        helper.getWiFiQuotaLimit(promise);
    }

    @ReactMethod
    public void getWiFiQuotaUsage(final Promise promise) {
        helper.getWiFiQuotaUsage(promise);
    }

    @ReactMethod
    public void getMobileQuotaLimit(final Promise promise) {
        helper.getMobileQuotaLimit(promise);
    }

    @ReactMethod
    public void getMobileQuotaUsage(final Promise promise) {
        helper.getMobileQuotaUsage(promise);
    }

    @ReactMethod
    public void getDiskQuotaLimit(final Promise promise) {
        helper.getDiskQuotaLimit(promise);
    }

    @ReactMethod
    public void getDiskQuotaUsage(final Promise promise) {
        helper.getDiskQuotaUsage(promise);
    }

    @ReactMethod
    public void disableBatteryOptimization(final Promise promise) {
        helper.disableBatteryOptimization(promise);
    }

    @ReactMethod
    public void listenUserActivityUpdates(Promise promise) {
        helper.listenUserActivityUpdates(promise);
    }

    @ReactMethod
    public void getUserActivity(final Promise promise) {
        helper.getUserActivity(promise);
    }

    @Override
    public void onHostResume() {
        // Activity `onResume`
    }

    @Override
    public void onHostPause() {
        // Activity `onPause`
    }

    @Override
    public void onHostDestroy() {
        // Activity `onDestroy`
    }
}
