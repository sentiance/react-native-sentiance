package com.sentiance.react.bridge.test.validators;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.JavaOnlyMap;

public interface BridgeValidator<T> {
    default void validate(@NonNull T expected, @NonNull JavaOnlyMap actual) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    default void validate(@NonNull JavaOnlyMap expected, @NonNull T actual) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }
}
