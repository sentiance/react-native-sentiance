package com.sentiance.react.bridge.usercontext;

import static com.sentiance.react.bridge.usercontext.SentianceUserContextEmitter.USER_CONTEXT_EVENT;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.sentiance.react.bridge.core.common.SentianceSubscriptionsManager;
import com.sentiance.react.bridge.core.common.base.AbstractSentianceModule;
import com.sentiance.react.bridge.usercontext.utils.ErrorCodes;
import com.sentiance.sdk.Sentiance;
import com.sentiance.sdk.pendingoperation.PendingOperation;
import com.sentiance.sdk.usercontext.api.RequestUserContextError;
import com.sentiance.sdk.usercontext.api.UserContext;
import com.sentiance.sdk.usercontext.api.UserContextApi;
import com.sentiance.sdk.usercontext.api.UserContextUpdateListener;

public class SentianceUserContextModule extends AbstractSentianceModule {

    private static final String NATIVE_MODULE_NAME = "SentianceUserContext";
    static final String JS_PAYLOAD_KEY_INCLUDE_PROVISIONAL_EVENTS = "includeProvisionalEvents";

    private final SentianceUserContextEmitter mEmitter;
    private final SentianceUserContextConverter mConverter;
    private final UserContextApi mUserContextApi;

    public SentianceUserContextModule(ReactApplicationContext reactContext,
                                      Sentiance sentiance,
                                      SentianceSubscriptionsManager subscriptionsManager,
                                      SentianceUserContextEmitter emitter,
                                      SentianceUserContextConverter converter,
                                      UserContextApi userContextApi) {
        super(reactContext, sentiance, subscriptionsManager);
        mEmitter = emitter;
        mConverter = converter;
        mUserContextApi = userContextApi;
    }

    @Override
    protected void addSupportedEventSubscriptions(SentianceSubscriptionsManager subscriptionsManager) {
        mSubscriptionsManager.addSupportedSubscription(
            USER_CONTEXT_EVENT,
            mUserContextApi::addProvisionalAwareUserContextUpdateListener,
            mUserContextApi::removeUserContextUpdateListener,
            SentianceSubscriptionsManager.SubscriptionType.MULTIPLE
        );
    }

    @NonNull
    @Override
    public String getName() {
        return NATIVE_MODULE_NAME;
    }

    @ReactMethod
    @SuppressWarnings("unused")
    public void requestUserContext(final boolean includeProvisionalEvents, final Promise promise) {
        if (rejectIfNotInitialized(promise)) {
            return;
        }

        PendingOperation<UserContext, RequestUserContextError> userContextRequest =
            includeProvisionalEvents ? mUserContextApi.requestUserContextIncludingProvisionalEvents()
                : mUserContextApi.requestUserContext();

        userContextRequest.addOnCompleteListener(pendingOperation -> {
            if (pendingOperation.isSuccessful()) {
                UserContext userContext = pendingOperation.getResult();
                promise.resolve(mConverter.convertUserContext(userContext));
            } else {
                RequestUserContextError error = pendingOperation.getError();
                promise.reject(ErrorCodes.E_SDK_REQUEST_USER_CONTEXT_ERROR,
                    mConverter.stringifyGetUserContextError(error));
            }
        });
    }

    @Override
    @ReactMethod
    protected void addNativeListener(String eventName, int subscriptionId, @Nullable ReadableMap payload, Promise promise) {
        if (rejectIfNotInitialized(promise)) {
            return;
        }

        switch (eventName) {
            case USER_CONTEXT_EVENT:
                mSubscriptionsManager.addSubscription(eventName, subscriptionId, (UserContextUpdateListener) mEmitter::sendUserContext);
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
