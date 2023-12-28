package com.sentiance.react.bridge.legacy;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.sentiance.react.bridge.core.SentianceConverter;
import com.sentiance.sdk.OnStartFinishedHandler;
import com.sentiance.sdk.SdkStatus;

import java.util.ArrayList;
import java.util.List;

public class StartFinishedHandlerCreator {

  private final List<OnStartFinishedHandler> startFinishedHandlers;
  private final SentianceConverter converter;

  StartFinishedHandlerCreator() {
    this.startFinishedHandlers = new ArrayList<>();
    converter = new SentianceConverter();
  }

  OnStartFinishedHandler createNewStartFinishedHandler(final Promise promise) {
    final OnStartFinishedHandler startFinishedHandler = new OnStartFinishedHandler() {
      @Override
      public void onStartFinished(@NonNull SdkStatus sdkStatus) {
        promise.resolve(converter.convertSdkStatus(sdkStatus));
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
