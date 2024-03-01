package com.sentiance.react.bridge.test;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.sentiance.react.bridge.core.common.SentianceSubscriptionsManager;
import com.sentiance.react.bridge.core.common.base.AbstractSentianceModule;
import com.sentiance.sdk.InitState;
import com.sentiance.sdk.Sentiance;

import org.junit.After;
import org.junit.Before;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public abstract class ReactNativeModuleTest<T extends AbstractSentianceModule> extends ReactNativeTest {

    @Mock
    protected Sentiance mSentiance;
    @Mock
    protected ReactApplicationContext mReactApplicationContext;
    @Mock
    protected Promise mPromise;
    @Mock
    protected SentianceSubscriptionsManager mSentianceSubscriptionsManager;
    @Mock
    protected WritableArray mockWritableArray;
    @Mock
    protected WritableMap mockWritableMap;
    @Captor
    protected ArgumentCaptor<WritableArray> writableArrayCaptor;
    @Captor
    protected ArgumentCaptor<WritableMap> writableMapCaptor;
    @Captor
    protected ArgumentCaptor<String> stringCaptor;
    @Captor
    protected ArgumentCaptor<Integer> intCaptor;
    @Captor
    protected ArgumentCaptor<Boolean> boolCaptor;

    protected T mModule;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);
        ensureSdkIsInitialized();
        mModule = initModule();
    }

    @After
    public void tearDown() {
        Mockito.validateMockitoUsage();
    }

    protected abstract T initModule();

    private void ensureSdkIsInitialized() {
        Mockito.when(mSentiance.getInitState()).thenReturn(InitState.INITIALIZED);
    }
}
