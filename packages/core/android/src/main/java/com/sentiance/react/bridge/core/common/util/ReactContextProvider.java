package com.sentiance.react.bridge.core.common.util;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import com.facebook.react.ReactApplication;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.bridge.ReactContext;
import com.sentiance.react.bridge.core.BuildConfig;

public final class ReactContextProvider {

    private static final String TAG = "ReactNativeContextProvider";

    private final ReactApplication mReactApplication;

    public ReactContextProvider(Context applicationContext) {
        mReactApplication = (ReactApplication) applicationContext;
    }

    /**
     * Triggers the initialization of a React context if it is not initializing already,
     * or returns the current React context if it is already initialized.
     *
     * If this call does end up triggering the initialization of a React context,
     * then it will probably return <code>null</code> since the operation is asynchronous.
     *
     * @return a ReactContext if it exists, null otherwise.
     */
    @Nullable
    public ReactContext createReactContext() {
        if (BuildConfig.IS_NEW_ARCHITECTURE_ENABLED) {
            return createReactContextInNewArchMode();
        } else {
            return createReactContextInLegacyArchMode();
        }
    }

    @Nullable
    private ReactContext createReactContextInNewArchMode() {
        try {
            java.lang.reflect.Method getReactHost = mReactApplication.getClass().getMethod("getReactHost");
            Object reactHost = getReactHost.invoke(mReactApplication);
            Class<?> reactHostClass = Class.forName("com.facebook.react.ReactHost");

            java.lang.reflect.Method start = reactHostClass.getMethod("start");
            java.lang.reflect.Method getCurrentReactContext = reactHostClass.getMethod("getCurrentReactContext");

            // Trigger the initialization of a React context, should be safe to call multiple times
            // https://github.com/facebook/react-native/blob/main/packages/react-native/ReactAndroid/src/main/java/com/facebook/react/runtime/ReactHostImpl.kt#L723
            start.invoke(reactHost);

            return (ReactContext) getCurrentReactContext.invoke(reactHost);
        } catch (Throwable t) {
            Log.d(TAG, "Failed to create React context");
            return null;
        }
    }

    @Nullable
    private ReactContext createReactContextInLegacyArchMode() {
        ReactNativeHost reactNativeHost = mReactApplication.getReactNativeHost();

        // Trigger the initialization of a React context
        if (!reactNativeHost.getReactInstanceManager().hasStartedCreatingInitialContext())
            reactNativeHost.getReactInstanceManager().createReactContextInBackground();

        return reactNativeHost.getReactInstanceManager().getCurrentReactContext();
    }
}
