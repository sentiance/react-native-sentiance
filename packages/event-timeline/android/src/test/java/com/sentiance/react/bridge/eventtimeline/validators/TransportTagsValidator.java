package com.sentiance.react.bridge.eventtimeline.validators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.JavaOnlyMap;
import com.sentiance.react.bridge.test.validators.BridgeValidator;

import java.util.Map;

public class TransportTagsValidator implements BridgeValidator<Map<String, String>> {
    @Override
    public void validate(@NonNull JavaOnlyMap expected, @NonNull Map<String, String> actualTags) {
        Map<String, Object> expectedTags = expected.toHashMap();
        assertEquals(expectedTags.size(), actualTags.size());

        expectedTags.forEach((key, value) -> {
            assertTrue(actualTags.containsKey(key));
            assertEquals(expectedTags.get(key), actualTags.get(key));
        });
    }

    @Override
    public void validate(@NonNull Map<String, String> expected, @NonNull JavaOnlyMap actual) {
        validate(actual, expected);
    }
}