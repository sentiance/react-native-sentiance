package com.sentiance.react.bridge;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import com.sentiance.sdk.OnInitCallback;
import com.sentiance.sdk.OnSdkStatusUpdateHandler;
import com.sentiance.sdk.OnStartFinishedHandler;
import com.sentiance.sdk.SdkConfig;
import com.sentiance.sdk.SdkStatus;
import com.sentiance.sdk.Sentiance;
import com.sentiance.sdk.Token;
import com.sentiance.sdk.TokenResultCallback;
import com.sentiance.sdk.trip.StartTripCallback;
import com.sentiance.sdk.trip.StopTripCallback;
import com.sentiance.sdk.trip.TripType;
import com.sentiance.core.model.thrift.ExternalEventType;
import com.sentiance.core.model.thrift.TransportMode;

import android.content.Context;
import android.content.Intent;
import android.app.Notification;
import android.app.PendingIntent;
import android.support.v4.app.NotificationCompat;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

public class RNSentianceModule extends ReactContextBaseJavaModule implements LifecycleEventListener {

	private static final boolean DEBUG = true;
	private final ReactApplicationContext reactContext;
	private final String STATUS_UPDATE = "SDKStatusUpdate";
	private final String TRIP_TIMEOUT= "TripTimeout";
	private final String E_SDK_INIT_ERROR = "E_SDK_INIT_ERROR";
	private final String E_SDK_GET_TOKEN_ERROR = "E_SDK_GET_TOKEN_ERROR";
	private final String E_SDK_START_TRIP_ERROR = "E_SDK_START_TRIP_ERROR";
	private final String E_SDK_STOP_TRIP_ERROR = "E_SDK_STOP_TRIP_ERROR";
	private final String E_SDK_ADD_USER_METADATA_FIELD_ERROR = "E_SDK_ADD_USER_METADATA_FIELD_ERROR";

	public RNSentianceModule(ReactApplicationContext reactContext) {
		super(reactContext);
		this.reactContext = reactContext;
	}

	@Override
	public String getName() {
		return "RNSentiance";
	}

	private void log(String msg, Object... params) {
		if (DEBUG) {
			Log.e("SentianceSDK", String.format(msg, params));
		}
	}

	private WritableMap convertSdkStatus(SdkStatus status) {
		WritableMap map = Arguments.createMap();
		try {
			map.putString("startStatus", status.startStatus.name());
			map.putBoolean("canDetect", status.canDetect);
			map.putBoolean("isRemoteEnabled", status.isRemoteEnabled);
			map.putBoolean("isLocationPermGranted", status.isLocationPermGranted);
			map.putString("locationSetting", status.locationSetting.name());
			map.putBoolean("isAccelPresent", status.isAccelPresent);
			map.putBoolean("isGyroPresent", status.isGyroPresent);
			map.putBoolean("isGpsPresent", status.isGpsPresent);
			map.putBoolean("isGooglePlayServicesMissing", status.isGooglePlayServicesMissing);
			map.putString("wifiQuotaStatus", status.wifiQuotaStatus.toString());
			map.putString("mobileQuotaStatus", status.mobileQuotaStatus.toString());
			map.putString("diskQuotaStatus", status.diskQuotaStatus.toString());
		} catch (Exception ignored) {
		}

		return map;
	}

	private WritableMap convertToken(Token token) {
		WritableMap map = Arguments.createMap();
		try {
			map.putString("tokenId", token.getTokenId());
			map.putString("expiryDate", String.valueOf(token.getExpiryDate()));
		} catch (Exception ignored) {
		}

		return map;
	}

	private Map<String, String> convertReadableMapToMap(ReadableMap inputMap) {
		Map<String, String> map = new HashMap<String, String>();
		ReadableMapKeySetIterator iterator = inputMap.keySetIterator();
		while (iterator.hasNextKey()) {
			String key = iterator.nextKey();
			try {
				map.put(key, String.valueOf(inputMap.getString(key)));
			} catch (Exception ignored) {

			}
		}
		return map;
	}

	private TripType toTripType(final String type) {
		if(type.equals("sdk")) {
			return TripType.SDK_TRIP;
		}
		else if (type.equals("external")) {
			return TripType.EXTERNAL_TRIP;
		}
		else {
			return TripType.ANY;
		}
	}

	@ReactMethod
	public void init(
		String appId,
		String appSecret,
		final Promise promise
	) {
		Log.v("------init()", "appId: " + appId + " | appSecret: " + appSecret);

		if (Sentiance.getInstance(this.reactContext).isInitialized()) {
			promise.resolve(null);
			return;
		}
		Context context = getReactApplicationContext();
		Notification notification = new NotificationCompat.Builder(context)
			.build();

		OnSdkStatusUpdateHandler statusHandler = new OnSdkStatusUpdateHandler() {
			@Override
			public void onSdkStatusUpdate(SdkStatus status) {
				sendStatusUpdate(status);
			}
		};
		final SdkConfig sdkConfig = new SdkConfig.Builder(appId, appSecret, notification)
				.setOnSdkStatusUpdateHandler(statusHandler)
				.build();

		OnInitCallback initCallback = new OnInitCallback() {
			@Override
			public void onInitSuccess() {
				promise.resolve(null);
			}
			@Override
			public void onInitFailure(InitIssue issue) {
				promise.reject(E_SDK_INIT_ERROR, issue.toString());
			}
		};
		Sentiance.getInstance(this.reactContext).init(sdkConfig, initCallback);
	}

	private void sendStatusUpdate(
			SdkStatus sdkStatus
	) {
		this.reactContext
				.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
				.emit(STATUS_UPDATE, convertSdkStatus(sdkStatus));
	}

	@ReactMethod
	public void start(
		final Promise promise
	) {
		Sentiance.getInstance(this.reactContext).start(new OnStartFinishedHandler() {
			@Override
			public void onStartFinished(SdkStatus sdkStatus) {
				promise.resolve(convertSdkStatus(sdkStatus));
			}
		});
	}

	@ReactMethod
	public void stop(
		final Promise promise
	) {
		Sentiance.getInstance(this.reactContext).stop();
		promise.resolve("OK");
	}

	@ReactMethod
	public void startTrip(
		ReadableMap metadata,
		String hintParam,
		final Promise promise
	) {
		final TransportMode hint = hintParam == null ? null : TransportMode.valueOf(hintParam);
		Sentiance.getInstance(this.reactContext).startTrip(null, null, new StartTripCallback() {
			@Override
				public void onSuccess() {
					promise.resolve(null);
			}

			@Override
			public void onFailure(SdkStatus sdkStatus) {
				promise.reject(E_SDK_START_TRIP_ERROR, sdkStatus.toString());
			}
		});
	}

	@ReactMethod
	public void stopTrip(
		final Promise promise
	) {
		Sentiance.getInstance(this.reactContext).stopTrip(new StopTripCallback() {
			@Override
			public void onSuccess() {
				promise.resolve(null);
			}

			@Override
			public void onFailure(SdkStatus sdkStatus) {
				promise.reject(E_SDK_STOP_TRIP_ERROR, sdkStatus.toString());
			}
		});
	}

	@ReactMethod
	public void getSdkStatus(
		final Promise promise
	) {
		SdkStatus sdkStatus = Sentiance.getInstance(this.reactContext).getSdkStatus();
		promise.resolve(convertSdkStatus(sdkStatus));
	}

	@ReactMethod
	public void getVersion(
		final Promise promise
	) {
		String version = Sentiance.getInstance(this.reactContext).getVersion();
		promise.resolve(version);
	}

	@ReactMethod
	public void isInitialized(
		final Promise promise
	) {
		Boolean isInitialized = Sentiance.getInstance(this.reactContext).isInitialized();
		promise.resolve(isInitialized);
	}

	@ReactMethod
	public void isTripOngoing(
		String typeParam,
		final Promise promise
	) {
		if (typeParam == null) {
			typeParam = "sdk";
		}
		final TripType type = toTripType(typeParam);
		Boolean isTripOngoing = Sentiance.getInstance(this.reactContext).isTripOngoing(type);
		promise.resolve(isTripOngoing);
	}

	@ReactMethod
	public void getUserAccessToken(
		final Promise promise
	) {
		Sentiance.getInstance(this.reactContext).getUserAccessToken(new TokenResultCallback() {
			@Override
			public void onSuccess(Token token) {
				promise.resolve(convertToken(token));
			}

			@Override
			public void onFailure() {
				promise.reject(E_SDK_GET_TOKEN_ERROR, "test");
			}
		});
	}

	@ReactMethod
	public void getUserId(
		final Promise promise
	) {
		String userId = Sentiance.getInstance(this.reactContext).getUserId();
		promise.resolve(userId);
	}

	@ReactMethod
	public void addUserMetadataField(
		final String label,
		final String value,
		final Promise promise
	) {
		Log.v("label", label);
		Sentiance.getInstance(this.reactContext).addUserMetadataField(label, value);
		promise.resolve(null);
	}

	@ReactMethod
	public void addUserMetadataFields(
			ReadableMap inputMetadata,
			final Promise promise
	) {
		final Map<String, String> metadata = convertReadableMapToMap(inputMetadata);
		Sentiance.getInstance(this.reactContext).addUserMetadataFields(metadata);
		promise.resolve(null);
	}

	@ReactMethod
	public void removeUserMetadataField(
			final String label,
			final Promise promise
	) {
		Sentiance.getInstance(this.reactContext).removeUserMetadataField(label);
		promise.resolve(null);
	}

	@ReactMethod
	public void submitDetections(
			final Promise promise
	) {
		promise.resolve(null);
	}

	@ReactMethod
	public void getWiFiQuotaLimit(
		final Promise promise
	) {
		Long wifiQuotaLimit = Sentiance.getInstance(this.reactContext).getWiFiQuotaLimit();
		promise.resolve(wifiQuotaLimit.toString());
	}

	@ReactMethod
	public void getWiFiQuotaUsage(
		final Promise promise
	) {
		Long wifiQuotaUsage = Sentiance.getInstance(this.reactContext).getWiFiQuotaUsage();
		promise.resolve(wifiQuotaUsage.toString());
	}

	@ReactMethod
	public void getMobileQuotaLimit(
		final Promise promise
	) {
		Long mobileQuotaLimit = Sentiance.getInstance(this.reactContext).getMobileQuotaLimit();
		promise.resolve(mobileQuotaLimit.toString());
	}

	@ReactMethod
	public void getMobileQuotaUsage(
		final Promise promise
	) {
		Long mobileQuotaUsage = Sentiance.getInstance(this.reactContext).getMobileQuotaUsage();
		promise.resolve(mobileQuotaUsage.toString());
	}

	@ReactMethod
	public void getDiskQuotaLimit(
		final Promise promise
	) {
		Long diskQuotaLimit = Sentiance.getInstance(this.reactContext).getDiskQuotaLimit();
		promise.resolve(diskQuotaLimit.toString());
	}

	@ReactMethod
	public void getDiskQuotaUsage(
		final Promise promise
	) {
		Long diskQuotaUsage = Sentiance.getInstance(this.reactContext).getDiskQuotaUsage();
		promise.resolve(diskQuotaUsage.toString());
	}

	@Override
	public void onHostResume() {
		// Activity `onResume`
	}

	@Override
	public void onHostPause() {
		// Activity `onPause`
	}

	@Override
	public void onHostDestroy() {
		// Activity `onDestroy`
	}

}
