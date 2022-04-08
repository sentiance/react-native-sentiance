package com.sentiance.react.bridge.legacy;

import com.facebook.react.bridge.Promise;
import com.sentiance.react.bridge.core.SentianceConverter;
import com.sentiance.react.bridge.core.SentianceEmitter;
import com.sentiance.sdk.OnStartFinishedHandler;
import com.sentiance.sdk.SdkStatus;

import java.util.ArrayList;
import java.util.List;

public class StartFinishedHandlerCreator {

		private final SentianceEmitter emitter;
		private final List<OnStartFinishedHandler> startFinishedHandlers;

		StartFinishedHandlerCreator(SentianceEmitter emitter) {
				this.emitter = emitter;
				this.startFinishedHandlers = new ArrayList<>();
		}

		OnStartFinishedHandler createNewStartFinishedHandler(final Promise promise) {
				final OnStartFinishedHandler startFinishedHandler = new OnStartFinishedHandler() {
						@Override
						public void onStartFinished(SdkStatus sdkStatus) {
								promise.resolve(SentianceConverter.convertSdkStatus(sdkStatus));
								removeStartFinishHandler(this);
						}
				};
				// hold strong reference
				addStartFinishHandler(startFinishedHandler);
				return startFinishedHandler;
		}

		private synchronized void addStartFinishHandler(OnStartFinishedHandler handler) {
				startFinishedHandlers.add(handler);
		}

		private synchronized void removeStartFinishHandler(OnStartFinishedHandler handler) {
				startFinishedHandlers.remove(handler);
		}
}