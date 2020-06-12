package com.sentiance.react.bridge;

import com.facebook.react.bridge.Promise;
import com.sentiance.sdk.OnStartFinishedHandler;
import com.sentiance.sdk.SdkStatus;

import java.util.ArrayList;
import java.util.List;

public class StartFinishedHandlerCreator {

  private final RNSentianceEmitter emitter;
  private final List<OnStartFinishedHandler> startFinishedHandlers;

  StartFinishedHandlerCreator(RNSentianceEmitter emitter) {
    this.emitter = emitter;
    this.startFinishedHandlers = new ArrayList<>();
  }

  OnStartFinishedHandler createNewStartFinishedHandler(final Promise promise) {
    final OnStartFinishedHandler startFinishedHandler = new OnStartFinishedHandler() {
      @Override
      public void onStartFinished(SdkStatus sdkStatus) {
        promise.resolve(RNSentianceConverter.convertSdkStatus(sdkStatus));
        emitter.sendStatusUpdateEvent(sdkStatus);
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
