package com.sentiance.react.bridge.smartgeofences;

import static com.sentiance.react.bridge.smartgeofences.SmartGeofenceEmitter.SMART_GEOFENCE_EVENT;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;

import android.location.Location;

import com.facebook.react.bridge.JavaOnlyMap;
import com.facebook.react.bridge.WritableMap;
import com.sentiance.react.bridge.core.SentianceConverter;
import com.sentiance.react.bridge.smartgeofences.converters.SmartGeofencesConverter;
import com.sentiance.react.bridge.smartgeofences.util.validators.SmartGeofenceRefreshErrorBridgeValidator;
import com.sentiance.react.bridge.smartgeofences.utils.ErrorCodes;
import com.sentiance.react.bridge.test.ReactNativeModuleTest;
import com.sentiance.sdk.pendingoperation.OnCompleteListener;
import com.sentiance.sdk.pendingoperation.PendingOperation;
import com.sentiance.sdk.smartgeofences.api.DetectionMode;
import com.sentiance.sdk.smartgeofences.api.SmartGeofence;
import com.sentiance.sdk.smartgeofences.api.SmartGeofenceApi;
import com.sentiance.sdk.smartgeofences.api.SmartGeofenceEvent;
import com.sentiance.sdk.smartgeofences.api.SmartGeofenceEventListener;
import com.sentiance.sdk.smartgeofences.api.SmartGeofencesRefreshError;
import com.sentiance.sdk.smartgeofences.api.SmartGeofencesRefreshFailureReason;
import com.sentiance.sdk.smartgeofences.api.SmartGeofencesRefreshResult;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;

@RunWith(RobolectricTestRunner.class)
public class SmartGeofencesModuleTest extends ReactNativeModuleTest<SmartGeofencesModule> {

    @Mock
    private SmartGeofenceApi mSmartGeofenceApi;
    @Mock
    private SmartGeofenceEmitter mSmartGeofenceEmitter;
    @Captor
    private ArgumentCaptor<SmartGeofenceEventListener> smartGeofenceEventListenerCaptor;

    private SmartGeofenceRefreshErrorBridgeValidator refreshErrorBridgeValidator;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        refreshErrorBridgeValidator = new SmartGeofenceRefreshErrorBridgeValidator();
    }

    @Override
    protected SmartGeofencesModule initModule() {
        return new SmartGeofencesModule(
            mReactApplicationContext,
            mSentiance,
            mSentianceSubscriptionsManager,
            mSmartGeofenceApi,
            mSmartGeofenceEmitter,
            new SmartGeofencesConverter(new SentianceConverter())
        );
    }

    @Test
    public void testRefreshGeofencesSucceeds() {
        PendingOperation<SmartGeofencesRefreshResult, SmartGeofencesRefreshError> pendingOperation =
            Mockito.mock(PendingOperation.class);

        Mockito.when(pendingOperation.isSuccessful()).thenReturn(true);
        Mockito.when(mSmartGeofenceApi.refreshGeofences()).thenReturn(pendingOperation);
        Mockito.when(pendingOperation.addOnCompleteListener(any())).thenAnswer((Answer<Void>) invocationOnMock -> {
            OnCompleteListener listener = (OnCompleteListener) invocationOnMock.getArguments()[0];
            listener.onComplete(pendingOperation);
            return null;
        });

        mModule.refreshGeofences(mPromise);
        verify(mPromise).resolve(null);
    }

    @Test
    public void testRefreshGeofencesFails() {
        PendingOperation<SmartGeofencesRefreshResult, SmartGeofencesRefreshError> pendingOperation =
            Mockito.mock(PendingOperation.class);

        SmartGeofencesRefreshError expectedError = new SmartGeofencesRefreshError(
            SmartGeofencesRefreshFailureReason.NETWORK_ERROR,
            "network error details"
        );
        Mockito.when(pendingOperation.isSuccessful()).thenReturn(false);
        Mockito.when(pendingOperation.getError()).thenReturn(expectedError);
        Mockito.when(mSmartGeofenceApi.refreshGeofences()).thenReturn(pendingOperation);
        Mockito.when(pendingOperation.addOnCompleteListener(any())).thenAnswer((Answer<Void>) invocationOnMock -> {
            OnCompleteListener listener = (OnCompleteListener) invocationOnMock.getArguments()[0];
            listener.onComplete(pendingOperation);
            return null;
        });

        mModule.refreshGeofences(mPromise);

        verify(mPromise).reject(Mockito.eq(ErrorCodes.E_SMART_GEOFENCES_REFRESH_ERROR), anyString(), writableMapCaptor.capture());
        WritableMap capturedErrorMap = writableMapCaptor.getValue();

        refreshErrorBridgeValidator.validate(expectedError, (JavaOnlyMap) capturedErrorMap);
    }

    @Test
    public void testGetDetectionMode() {
        DetectionMode expectedDetectionMode = DetectionMode.BACKGROUND;
        Mockito.when(mSmartGeofenceApi.getDetectionMode()).thenReturn(expectedDetectionMode);

        mModule.getDetectionMode(mPromise);
        verify(mPromise).resolve(expectedDetectionMode.name());
    }

    @Test
    public void testAddSmartGeofenceEventListener() {
        int subscriptionId = 1;
        mModule.addNativeListener(SMART_GEOFENCE_EVENT, subscriptionId, null, mPromise);

        verify(mPromise).resolve(null);
        verify(mSentianceSubscriptionsManager)
            .addSubscription(stringCaptor.capture(), intCaptor.capture(), smartGeofenceEventListenerCaptor.capture());

        assertEquals(SMART_GEOFENCE_EVENT, stringCaptor.getValue());
        assertEquals(subscriptionId, intCaptor.getValue().intValue());

        SmartGeofenceEventListener listener = smartGeofenceEventListenerCaptor.getValue();
        SmartGeofenceEvent event = new SmartGeofenceEvent(
          System.currentTimeMillis(),
            Arrays.asList(
                new SmartGeofence("sent_id1", 1.23, 4.56, 100, null),
                new SmartGeofence("sent_id2", 1.23, 4.56, 10, "external_id2")
            ),
            SmartGeofenceEvent.Type.ENTRY,
            new Location("gps")
        );
        listener.onSmartGeofenceEvent(event);

        verify(mSmartGeofenceEmitter).sendSmartGeofencesEvent(event);
    }

    @Test
    public void testRemoveSmartGeofenceEventListener() {
        int subscriptionId = 1;
        mModule.removeNativeListener(SMART_GEOFENCE_EVENT, subscriptionId, mPromise);

        verify(mSentianceSubscriptionsManager)
            .removeSubscription(intCaptor.capture(), stringCaptor.capture());

        assertEquals(subscriptionId, intCaptor.getValue().intValue());
        assertEquals(SMART_GEOFENCE_EVENT, stringCaptor.getValue());
    }
}
