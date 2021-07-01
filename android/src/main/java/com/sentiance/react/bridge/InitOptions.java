package com.sentiance.react.bridge;

import androidx.annotation.Nullable;

import com.sentiance.sdk.OnInitCallback;
import com.sentiance.sdk.OnStartFinishedHandler;

public class InitOptions {
  private final String appId;
  private final String secret;
  private final boolean autoStart;
  private final boolean collectAppSessionData;
  private final @Nullable String apiEndpoint;
  private final @Nullable OnInitCallback onInitCallback;
  private final @Nullable OnStartFinishedHandler onStartFinishedHandler;

  public InitOptions(Builder builder) {
    this.appId = builder.appId;
    this.secret = builder.secret;
    this.autoStart = builder.autoStart;
    this.collectAppSessionData = builder.collectAppSessionData;
    this.apiEndpoint = builder.apiEndpoint;
    this.onInitCallback = builder.onInitCallback;
    this.onStartFinishedHandler = builder.onStartFinishedHandler;
  }

  public String getAppId() {
    return appId;
  }

  public String getSecret() {
    return secret;
  }

  public boolean shouldAutoStart() {
    return autoStart;
  }

  public boolean isCollectAppSessionData() {
    return collectAppSessionData;
  }

  @Nullable
  public String getApiEndpoint() {
    return apiEndpoint;
  }

  @Nullable
  public OnInitCallback getOnInitCallback() {
    return onInitCallback;
  }

  @Nullable
  public OnStartFinishedHandler getOnStartFinishedHandler() {
    return onStartFinishedHandler;
  }

  public static class Builder {
    private final String appId;
    private final String secret;

    private boolean autoStart;
    private boolean collectAppSessionData;
    private @Nullable String apiEndpoint;
    private @Nullable OnInitCallback onInitCallback;
    private @Nullable OnStartFinishedHandler onStartFinishedHandler;

    public Builder(String appId, String secret) {
      this.appId = appId;
      this.secret = secret;

      autoStart = false;
      apiEndpoint = null;
    }

    public Builder autoStart(boolean shouldAutoStart) {
      autoStart = shouldAutoStart;
      return this;
    }

    public Builder apiEndpoint(String url) {
      apiEndpoint = url;
      return this;
    }

    public Builder initCallback(OnInitCallback callback) {
      onInitCallback = callback;
      return this;
    }

    public Builder startFinishedHandler(OnStartFinishedHandler handler) {
      onStartFinishedHandler = handler;
      return this;
    }

    public Builder collectAppSessionData(boolean enabled) {
      collectAppSessionData = enabled;
      return this;
    }

    public InitOptions build() {
      return new InitOptions(this);
    }
  }
}
