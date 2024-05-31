package com.sentiance.react.bridge.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sentiance.react.bridge.test.ReactNativeModuleTest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class SentianceModuleTest {
//  extends ReactNativeModuleTest<SentianceModule> {
//    @Mock
//    private SentianceHelper sentianceHelper;
//    @Mock
//    private SentianceEmitter sentianceEmitter;
//    @Mock
//    private SentianceConverter sentianceConverter;

//    @Override
//    protected SentianceModule initModule() {
//        return new SentianceModule(mReactApplicationContext, mSentiance,
//            mSentianceSubscriptionsManager, sentianceHelper, sentianceEmitter, sentianceConverter);
//    }

    @Test
    public void testSetIsAllowedToUseMobileData() {
//        mModule.setIsAllowedToUseMobileData(false, mPromise);
//        verify(mSentiance).setIsAllowedToUseMobileData(false);
//        verify(mPromise).resolve(null);
    }

    @Test
    public void testIsAllowedToUseMobileData() {
//        when(mSentiance.isAllowedToUseMobileData()).thenReturn(true);
//        mModule.isAllowedToUseMobileData(mPromise);
//
//        when(mSentiance.isAllowedToUseMobileData()).thenReturn(false);
//        mModule.isAllowedToUseMobileData(mPromise);
//
//        verify(mPromise, times(2)).resolve(boolCaptor.capture());
//
//        List<Boolean> allValues = boolCaptor.getAllValues();
//        assertTrue(allValues.get(0));
//        assertFalse(allValues.get(1));
    }
}
