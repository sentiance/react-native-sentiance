package com.sentiance.react.bridge.eventtimeline;

import static com.sentiance.react.bridge.eventtimeline.ErrorCodes.E_INVALID_FEEDBACK_TYPE;
import static com.sentiance.react.bridge.eventtimeline.ErrorCodes.E_OCCUPANT_ROLE_FEEDBACK_SUBMISSION;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.sentiance.react.bridge.core.common.SentianceSubscriptionsManager;
import com.sentiance.react.bridge.core.common.base.AbstractSentianceModule;
import com.sentiance.react.bridge.eventtimeline.converters.OnDeviceTypesConverter;
import com.sentiance.sdk.Sentiance;
import com.sentiance.sdk.feedback.api.FeedbackApi;
import com.sentiance.sdk.feedback.api.OccupantRoleFeedback;
import com.sentiance.sdk.feedback.api.OccupantRoleFeedbackResult;

public class SentianceFeedbackModule extends AbstractSentianceModule {
    public static final String NATIVE_MODULE_NAME = "SentianceFeedback";

    private final FeedbackApi mFeedbackApi;
    private final OnDeviceTypesConverter mOnDeviceTypesConverter;

    public SentianceFeedbackModule(ReactApplicationContext reactApplicationContext,
                                   Sentiance sentiance,
                                   FeedbackApi feedbackApi,
                                   SentianceSubscriptionsManager subscriptionsManager,
                                   OnDeviceTypesConverter converter) {
        super(reactApplicationContext, sentiance, subscriptionsManager);
        this.mOnDeviceTypesConverter = converter;
        this.mFeedbackApi = feedbackApi;
    }

    @Override
    @ReactMethod
    protected void removeNativeListener(String eventName, int subscriptionId, Promise promise) {
        // Implementation can be added here if needed
    }

    @Override
    @ReactMethod
    protected void addNativeListener(String eventName, int subscriptionId, @Nullable ReadableMap payload, Promise promise) {
        // Implementation can be added here if needed
    }

    @Override
    @ReactMethod
    protected void addListener(String eventName) {
        // Implementation can be added here if needed
    }

    @Override
    @ReactMethod
    protected void removeListeners(Integer count) {
        // Implementation can be added here if needed
    }

    @NonNull
    @Override
    public String getName() {
        return NATIVE_MODULE_NAME;
    }

    @ReactMethod
    public void submitOccupantRoleFeedback(final String transportId,
                                           final String occupantFeedbackRole,
                                           final Promise promise) {
        if (rejectIfNotInitialized(promise)) {
            return;
        }

        OccupantRoleFeedback occupantRoleFeedback = mOnDeviceTypesConverter.getOccupantRoleFeedbackFrom(occupantFeedbackRole);
        if (occupantRoleFeedback == null) {
            promise.reject(E_INVALID_FEEDBACK_TYPE, "Invalid feedback role");
            return;
        }

        OccupantRoleFeedbackResult result = mFeedbackApi.submitOccupantRoleFeedback(transportId, occupantRoleFeedback);
        promise.resolve(result.name());
    }
}
