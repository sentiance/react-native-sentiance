package com.sentiance.react.bridge;

import android.app.Notification;
import com.sentiance.sdk.OnInitCallback;

public class RNSentianceConfig {

  public String appId;
  public String appSecret;
  public Boolean autoStart = true;
  public Notification notification = null;
  public OnInitCallback initCallback = new OnInitCallback() {
    @Override
    public void onInitSuccess() {
    }

    @Override
    public void onInitFailure(InitIssue issue) {
    }
  };

  // name is still mandatory
  public RNSentianceConfig(String appId, String appSecret) {
    this.appId = appId;
    this.appSecret = appSecret;
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
}
