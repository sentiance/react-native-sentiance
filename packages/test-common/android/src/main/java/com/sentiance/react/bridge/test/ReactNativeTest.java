package com.sentiance.react.bridge.test;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.JavaOnlyArray;
import com.facebook.react.bridge.JavaOnlyMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import org.junit.Before;
import org.junit.Rule;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.annotation.Config;

/**
 * A base class that statically mocks the {@link Arguments} class.
 * <br>
 * Any test class that interacts with a component that makes use of the {@link Arguments} class internally
 * has to extend this class.
 */
@Config(manifest = Config.NONE)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*", "jdk.internal.reflect.*"})
@PrepareForTest(Arguments.class)
public abstract class ReactNativeTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(Arguments.class);
        PowerMockito.when(Arguments.createArray()).thenAnswer((Answer<WritableArray>) invocation -> new JavaOnlyArray());
        PowerMockito.when(Arguments.createMap()).thenAnswer((Answer<WritableMap>) invocation -> new JavaOnlyMap());
    }
}
