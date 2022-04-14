package com.sentiance.react.bridge.crashdetection;

import android.content.Context;

import com.sentiance.react.bridge.core.base.AbstractSentianceEmitter;
import com.sentiance.sdk.crashdetection.api.VehicleCrashEvent;

import static com.sentiance.react.bridge.crashdetection.SentianceCrashDetectionConverter.convertVehicleCrashEvent;

class CrashDetectionEmitter extends AbstractSentianceEmitter {

		private static final String VEHICLE_CRASH_EVENT = "SDKVehicleCrashEvent";

		public CrashDetectionEmitter(Context context) {
				super(context);
		}

		void sendVehicleCrashEvent(VehicleCrashEvent crashEvent) {
				sendEvent(VEHICLE_CRASH_EVENT, convertVehicleCrashEvent(crashEvent));
		}
}


