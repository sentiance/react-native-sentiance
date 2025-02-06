package com.sentiance.react.bridge.drivinginsights;

import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsEmitter.DRIVING_INSIGHTS_READY_EVENT;
import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsModule.NATIVE_MODULE_NAME;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.module.annotations.ReactModule;
import com.sentiance.react.bridge.core.common.SentianceSubscriptionsManager;
import com.sentiance.react.bridge.core.common.base.AbstractSentianceModule;
import com.sentiance.sdk.Sentiance;
import com.sentiance.sdk.drivinginsights.api.CallWhileMovingEvent;
import com.sentiance.sdk.drivinginsights.api.DrivingInsights;
import com.sentiance.sdk.drivinginsights.api.DrivingInsightsApi;
import com.sentiance.sdk.drivinginsights.api.DrivingInsightsReadyListener;
import com.sentiance.sdk.drivinginsights.api.HarshDrivingEvent;
import com.sentiance.sdk.drivinginsights.api.PhoneUsageEvent;
import com.sentiance.sdk.drivinginsights.api.SafetyScoreRequestParameters;
import com.sentiance.sdk.drivinginsights.api.SpeedingEvent;

import java.util.List;

@ReactModule(name = NATIVE_MODULE_NAME)
public class DrivingInsightsModule extends AbstractSentianceModule {

    // This is the name that Javascript code will use to refer to this native module
    public static final String NATIVE_MODULE_NAME = "SentianceDrivingInsights";

    private final DrivingInsightsEmitter mEmitter;
    private final DrivingInsightsApi mDrivingInsightsApi;
    private final DrivingInsightsConverter converter;

    public DrivingInsightsModule(ReactApplicationContext reactApplicationContext,
                                 Sentiance sentiance,
                                 DrivingInsightsApi drivingInsightsApi,
                                 DrivingInsightsEmitter emitter,
                                 SentianceSubscriptionsManager subscriptionsManager,
                                 DrivingInsightsConverter drivingInsightsConverter) {
        super(reactApplicationContext, sentiance, subscriptionsManager);
        mDrivingInsightsApi = drivingInsightsApi;
        mEmitter = emitter;
        converter = drivingInsightsConverter;
    }

    @NonNull
    @Override
    public String getName() {
        return NATIVE_MODULE_NAME;
    }

    @Override
    protected void addSupportedEventSubscriptions(SentianceSubscriptionsManager subscriptionsManager) {
        subscriptionsManager.addSupportedSubscription(
            DRIVING_INSIGHTS_READY_EVENT,
            mDrivingInsightsApi::setDrivingInsightsReadyListener,
            drivingInsightsReadyListener -> mDrivingInsightsApi.setDrivingInsightsReadyListener(null),
            SentianceSubscriptionsManager.SubscriptionType.SINGLE
        );
    }

    @ReactMethod
    public void getHarshDrivingEvents(String transportId, final Promise promise) {
        if (rejectIfNotInitialized(promise)) {
            return;
        }

        try {
            List<HarshDrivingEvent> harshDrivingEvents = mDrivingInsightsApi.getHarshDrivingEvents(transportId);
            WritableArray array = Arguments.createArray();
            for (HarshDrivingEvent event : harshDrivingEvents) {
                array.pushMap(converter.convertHarshDrivingEvent(event));
            }
            promise.resolve(array);
        } catch (Exception e) {
            promise.reject(e);
        }
    }

    @ReactMethod
    public void getPhoneUsageEvents(String transportId, final Promise promise) {
        if (rejectIfNotInitialized(promise)) {
            return;
        }

        try {
            List<PhoneUsageEvent> phoneUsageEvents = mDrivingInsightsApi.getPhoneUsageEvents(transportId);
            WritableArray array = Arguments.createArray();
            for (PhoneUsageEvent event : phoneUsageEvents) {
                array.pushMap(converter.convertPhoneUsageEvent(event));
            }
            promise.resolve(array);
        } catch (Exception e) {
            promise.reject(e);
        }
    }

    @ReactMethod
    public void getCallWhileMovingEvents(String transportId, final Promise promise) {
        if (rejectIfNotInitialized(promise)) {
            return;
        }

        try {
            List<CallWhileMovingEvent> callWhileMovingEvents = mDrivingInsightsApi.getCallWhileMovingEvents(transportId);
            WritableArray array = Arguments.createArray();
            for (CallWhileMovingEvent event : callWhileMovingEvents) {
                array.pushMap(converter.convertCallWhileMovingEvent(event));
            }
            promise.resolve(array);
        } catch (Exception e) {
            promise.reject(e);
        }
    }

    @ReactMethod
    public void getSpeedingEvents(String transportId, final Promise promise) {
        if (rejectIfNotInitialized(promise)) {
            return;
        }

        try {
            List<SpeedingEvent> speedingEvents = mDrivingInsightsApi.getSpeedingEvents(transportId);
            WritableArray array = Arguments.createArray();
            for (SpeedingEvent event : speedingEvents) {
                array.pushMap(converter.convertSpeedingEvent(event));
            }
            promise.resolve(array);
        } catch (Exception e) {
            promise.reject(e);
        }
    }

    @ReactMethod
    public void getDrivingInsights(String transportId, final Promise promise) {
        if (rejectIfNotInitialized(promise)) {
            return;
        }

        try {
            DrivingInsights drivingInsights = mDrivingInsightsApi.getDrivingInsights(transportId);
            WritableMap convertedDrivingInsights = null;
            if (drivingInsights != null) {
                convertedDrivingInsights = converter.convertDrivingInsights(drivingInsights);
            }
            promise.resolve(convertedDrivingInsights);
        } catch (Exception e) {
            promise.reject(e);
        }
    }

    @ReactMethod
    public void getAverageOverallSafetyScore(ReadableMap params, final Promise promise) {
        if (rejectIfNotInitialized(promise)) {
            return;
        }

        SafetyScoreRequestParameters requestParameters = converter.convertToSafetyScoreRequestParameters(params);
        promise.resolve(mDrivingInsightsApi.getAverageOverallSafetyScore(requestParameters));
    }

    @Override
    @ReactMethod
    public void addNativeListener(String eventName, int subscriptionId, Promise promise) {
        if (rejectIfNotInitialized(promise)) {
            return;
        }

        switch (eventName) {
            case DRIVING_INSIGHTS_READY_EVENT:
                mSubscriptionsManager.addSubscription(eventName, subscriptionId, (DrivingInsightsReadyListener) mEmitter::sendDrivingInsightsReadyEvent);
                break;
        }
        promise.resolve(null);
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
    protected void addListener(String eventName) {
    }

    @Override
    @ReactMethod
    public void removeListeners(Integer count) {
    }

}
