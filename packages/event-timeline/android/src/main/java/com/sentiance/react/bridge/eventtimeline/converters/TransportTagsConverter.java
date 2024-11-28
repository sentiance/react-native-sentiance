package com.sentiance.react.bridge.eventtimeline.converters;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.WritableMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Utility converter to handle conversions from/to React Native maps and standard Java hashmaps.
 */
public class TransportTagsConverter {
    public Map<String, String> convertFrom(ReadableMap tags) {
        Map<String, String> map = new HashMap<>();
        ReadableMapKeySetIterator iterator = tags.keySetIterator();
        while (iterator.hasNextKey()) {
            String key = iterator.nextKey();
            String value = tags.getString(key);

            if (value == null) {
                throw new IllegalArgumentException("Cannot get string value for key: " + key);
            }

            map.put(key, value);
        }
        return map;
    }

    public WritableMap convertFrom(Map<String, String> tags) {
        WritableMap writableMap = Arguments.createMap();
        Set<String> keys = tags.keySet();

        for (String key : keys) {
            writableMap.putString(key, tags.get(key));
        }

        return writableMap;
    }
}
