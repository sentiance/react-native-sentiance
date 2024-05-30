package com.sentiance.react.bridge.smartgeofences;

import static com.sentiance.react.bridge.smartgeofences.SmartGeofenceEmitter.SMART_GEOFENCE_EVENT;
import static com.sentiance.react.bridge.smartgeofences.utils.ErrorCodes.E_SMART_GEOFENCES_REFRESH_ERROR;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.sentiance.react.bridge.core.common.SentianceSubscriptionsManager;
import com.sentiance.react.bridge.core.common.base.AbstractSentianceModule;
import com.sentiance.react.bridge.smartgeofences.converters.SmartGeofencesConverter;
import com.sentiance.sdk.Sentiance;
import com.sentiance.smartgeofences.api.DetectionMode;
import com.sentiance.smartgeofences.api.SmartGeofenceApi;
import com.sentiance.smartgeofences.api.SmartGeofenceEventListener;

public class SmartGeofencesModule extends AbstractSentianceModule {

    public static final String NATIVE_MODULE_NAME = "SentianceSmartGeofences";
    private final SmartGeofenceApi mSmartGeofenceApi;
    private final SmartGeofenceEmitter mEmitter;
    private final SmartGeofencesConverter mSmartGeofencesConverter;

    public SmartGeofencesModule(ReactApplicationContext reactApplicationContext,
                                Sentiance sentiance,
                                SentianceSubscriptionsManager subscriptionsManager,
                                SmartGeofenceApi smartGeofenceApi,
                                SmartGeofenceEmitter emitter,
                                SmartGeofencesConverter smartGeofencesConverter) {
        super(reactApplicationContext, sentiance, subscriptionsManager);
        mSmartGeofenceApi = smartGeofenceApi;
        mEmitter = emitter;
        mSmartGeofencesConverter = smartGeofencesConverter;
    }

    @NonNull
    @Override
    public String getName() {
        return NATIVE_MODULE_NAME;
    }

    @Override
    protected void addSupportedEventSubscriptions(SentianceSubscriptionsManager subscriptionsManager) {
        subscriptionsManager.addSupportedSubscription(
            SMART_GEOFENCE_EVENT,
            mSmartGeofenceApi::setSmartGeofenceEventListener,
            listener -> mSmartGeofenceApi.setSmartGeofenceEventListener(null),
            SentianceSubscriptionsManager.SubscriptionType.SINGLE
        );
    }

    @ReactMethod
    public void refreshGeofences(final Promise promise) {
        if (rejectIfNotInitialized(promise)) {
            return;
        }

        try {
            mSmartGeofenceApi.refreshGeofences()
                .addOnCompleteListener(pendingOperation -> {
                    if (pendingOperation.isSuccessful()) {
                        promise.resolve(null);
                    } else {
                        promise.reject(
                            E_SMART_GEOFENCES_REFRESH_ERROR,
                            "Failed to refresh smart geofences",
                            mSmartGeofencesConverter.convertRefreshError(pendingOperation.getError())
                        );
                    }
                });
        } catch (Exception e) {
            promise.reject(e);
        }
    }

    @ReactMethod
    public void getDetectionMode(final Promise promise) {
        if (rejectIfNotInitialized(promise)) {
            return;
        }

        try {
            DetectionMode detectionMode = mSmartGeofenceApi.getDetectionMode();
            promise.resolve(mSmartGeofencesConverter.convertDetectionMode(detectionMode));
        } catch (Exception e) {
            promise.reject(e);
        }
    }

    @Override
    @ReactMethod
    protected void removeNativeListener(String eventName, int subscriptionId, Promise promise) {
        if (rejectIfNotInitialized(promise)) {
            return;
        }

        mSubscriptionsManager.removeSubscription(subscriptionId, eventName);
        promise.resolve(null);
    }

    @Override
    @ReactMethod
    protected void addNativeListener(String eventName, int subscriptionId, Promise promise) {
        if (rejectIfNotInitialized(promise)) {
            return;
        }

        if (eventName.equals(SMART_GEOFENCE_EVENT)) {
            mSubscriptionsManager.addSubscription(eventName, subscriptionId, (SmartGeofenceEventListener) mEmitter::sendSmartGeofencesEvent);
        }
        promise.resolve(null);
    }

    @Override
    @ReactMethod
    protected void addListener(String eventName) {

    }

    @Override
    @ReactMethod
    protected void removeListeners(Integer count) {

    }
}
