package com.sentiance.react.bridge;

import android.app.Notification;
import com.sentiance.sdk.OnInitCallback;
import java.lang.Throwable;

public class RNSentianceConfig {

  public String appId;
  public String appSecret;
  public Boolean autoStart = false;
  public Notification notification = null;
  public String baseURL = null;
  public OnInitCallback initCallback = new OnInitCallback() {
    @Override
    public void onInitSuccess() {
    }

    @Override
    public void onInitFailure(InitIssue issue, Throwable throwable) {
    }
  };

  // name is still mandatory
  public RNSentianceConfig(String appId, String appSecret) {
    this(appId, appSecret, null);
  }

  public RNSentianceConfig(String appId, String appSecret, String baseURL) {
    this.appId = appId;
    this.appSecret = appSecret;
    if (baseURL != null) {
      this.baseURL = baseURL;
    }
  }

  public void setInitCallback(OnInitCallback initCallback) {
    this.initCallback = initCallback;
  }

  public void setNotification(Notification customNotification) {
    this.notification = customNotification;
  }

  public void setAutoStart(Boolean autoStart) {
    this.autoStart = autoStart;
  }

  public void setBaseURL(String url) {
    this.baseURL = url;
  }
}
