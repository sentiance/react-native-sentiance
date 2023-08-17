package com.sentiance.react.bridge.drivinginsights;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.JavaOnlyArray;
import com.facebook.react.bridge.JavaOnlyMap;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.sentiance.react.bridge.core.base.AbstractSentianceModule;
import com.sentiance.react.bridge.core.common.SentianceSubscriptionsManager;
import com.sentiance.sdk.InitState;
import com.sentiance.sdk.Sentiance;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.annotation.Config;

@Config(manifest= Config.NONE)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*", "jdk.internal.reflect.*"})
@PrepareForTest(Arguments.class)
public abstract class ReactNativeModuleTest<T extends AbstractSentianceModule> {

  @Rule
  public PowerMockRule rule = new PowerMockRule();

  @Mock
  protected Sentiance mSentiance;
  @Mock
  protected ReactApplicationContext mReactApplicationContext;
  @Mock
  protected Promise mPromise;
  @Mock
  protected SentianceSubscriptionsManager mSentianceSubscriptionsManager;
  @Captor
  protected ArgumentCaptor<WritableArray> writableArrayCaptor;
  @Captor
  protected ArgumentCaptor<WritableMap> writableMapCaptor;

  protected T mModule;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    PowerMockito.mockStatic(Arguments.class);
    PowerMockito.when(Arguments.createArray()).thenAnswer((Answer<WritableArray>) invocation -> new JavaOnlyArray());
    PowerMockito.when(Arguments.createMap()).thenAnswer((Answer<WritableMap>) invocation -> new JavaOnlyMap());

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
