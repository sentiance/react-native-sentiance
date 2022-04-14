package com.sentiance.react.bridge.legacy;

import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.Nullable;

import com.sentiance.react.bridge.core.SentianceHelper;
import com.sentiance.react.bridge.core.utils.SentianceUtils;
import com.sentiance.sdk.OnInitCallback;
import com.sentiance.sdk.OnStartFinishedHandler;
import com.sentiance.sdk.SdkConfig;
import com.sentiance.sdk.SdkStatus;
import com.sentiance.sdk.Sentiance;

import java.lang.ref.WeakReference;
import java.util.Date;

public class RNSentianceHelper {

  private static final String SDK_NATIVE_INIT_FLAG = "SDK_NATIVE_INIT_FLAG";
  private static final String MY_PREFS_NAME = "RNSentianceHelper";
  private static final String TAG = "RNSentianceHelper";
  private static RNSentianceHelper rnSentianceHelper;
  private static SentianceHelper sentianceHelper;

  private final WeakReference<Context> weakContext;

  public static RNSentianceHelper getInstance(Context context) {
    if (rnSentianceHelper == null) {
      synchronized (RNSentianceHelper.class) {
        rnSentianceHelper = new RNSentianceHelper(context);
        if (sentianceHelper == null) {
          synchronized (SentianceHelper.class) {
            sentianceHelper = SentianceHelper.getInstance(context);
          }
        }
      }
    }
    return rnSentianceHelper;
  }


  private RNSentianceHelper(Context context) {
    weakContext = new WeakReference<>(context);
  }

  /**
   * ========================================
   * Temporary wrapper methods to ease the
   * integration of the SDK
   * ========================================
   */

  /**
   * The wrapper method handle SDK initialization without needing to pass the
   * crendentials. This method is meant to be called in the "MainActivity.onCreate". While
   * the credentials are passed during the "createUser" method exposed.
   * <p>
   * This method basically rallies around the state vairables.
   * - SENTIANCE_SDK_IS_READY_FOR_BACKGROUND
   * - SENTIANCE_SDK_APP_ID
   * - SENTIANCE_SDK_APP_SECRET
   * <p>
   * The above variables are set in the "createUser" method
   *
   * @param callback
   */
  public void initialize(@Nullable final InitCallback callback) {
    String isReadyForBackground = this.getValueForKey("SENTIANCE_SDK_IS_READY_FOR_BACKGROUND", "");

    if (!isReadyForBackground.equals("YES")) {
      return;
    }

    String appId = this.getValueForKey("SENTIANCE_SDK_APP_ID", "");
    String appSecret = this.getValueForKey("SENTIANCE_SDK_APP_SECRET", "");
    String baseUrl = this.getValueForKey("SENTIANCE_SDK_APP_BASE_URL", "");
    final String isDisabled = this.getValueForKey("SENTIANCE_SDK_IS_DISABLED", "");

    if (appId.equals("") || appSecret.equals("")) {
      return;
    }

    this.initializeSentianceSDK(appId, appSecret, false, baseUrl, new OnInitCallback() {
      @Override
      public void onInitSuccess() {
        if (!isDisabled.equals("YES")) {
          startSentianceSDK(
            new OnStartFinishedHandler() {
              @Override
              public void onStartFinished(SdkStatus sdkStatus) {
                if (callback != null) callback.onSuccess();
              }
            }
          );
        }
      }

      @Override
      public void onInitFailure(InitIssue issue, @Nullable Throwable throwable) {
        if (callback != null) callback.onFailure(issue);
      }
    }, null);
  }

  public void initializeAndStartSentianceSDK(String appId, String appSecret,
                                             final boolean shouldStart, @Nullable String baseUrl,
                                             boolean userLinkingEnabled,
                                             final @Nullable OnInitCallback initCallback,
                                             final @Nullable OnStartFinishedHandler startFinishedHandler) {
    Context context = weakContext.get();
    if (context == null) return;

    Notification notification = SentianceUtils.createNotificationFromManifestData(weakContext);

    // Create the config.
    SdkConfig.Builder builder = new SdkConfig.Builder(appId, appSecret, notification)
      .enableAllFeatures()
      .setOnSdkStatusUpdateHandler(sentianceHelper.getOnSdkStatusUpdateHandler());
    if (userLinkingEnabled)
      builder.setUserLinker(sentianceHelper.getUserLinker());
    if (baseUrl != null)
      builder.baseURL(baseUrl);

    SdkConfig config = builder.build();

    // Initialize and start Sentiance SDK.
    Sentiance.getInstance(context).init(config, new OnInitCallback() {
      @Override
      public void onInitSuccess() {
        if (initCallback != null)
          initCallback.onInitSuccess();
        if (shouldStart)
          startSentianceSDK(startFinishedHandler);
      }

      @Override
      public void onInitFailure(InitIssue issue, @Nullable Throwable throwable) {
        if (initCallback != null)
          initCallback.onInitFailure(issue, throwable);
        Log.e(TAG, issue.toString());
      }
    });
  }

  @SuppressWarnings({"unused", "WeakerAccess"})
  public void startSentianceSDK(@Nullable final OnStartFinishedHandler callback) {
    startSentianceSDK(null, callback);
  }

  @SuppressWarnings({"unused", "WeakerAccess"})
  public void startSentianceSDK(@Nullable final Long stopEpochTimeMs,
                                @Nullable final OnStartFinishedHandler callback) {
    Context context = weakContext.get();
    if (context == null) return;

    if (stopEpochTimeMs != null) {
      Sentiance.getInstance(context).start(new Date(stopEpochTimeMs), callback);
    } else {
      Sentiance.getInstance(context).start(callback);
    }
  }

  @SuppressWarnings({"unused", "WeakerAccess"})
  public void initializeSentianceSDK(String appId, String appSecret, boolean shouldStart,
                                     @Nullable OnInitCallback initCallback,
                                     @Nullable OnStartFinishedHandler startFinishedHandler) {
    initializeAndStartSentianceSDK(appId, appSecret, shouldStart, null, false, initCallback,
      startFinishedHandler);
  }

  @SuppressWarnings({"unused", "WeakerAccess"})
  public void initializeSentianceSDK(String appId, String appSecret, boolean shouldStart,
                                     @Nullable String baseUrl, @Nullable OnInitCallback initCallback,
                                     @Nullable OnStartFinishedHandler startFinishedHandler) {
    initializeAndStartSentianceSDK(appId, appSecret, shouldStart, baseUrl, false, initCallback,
      startFinishedHandler);
  }

  @SuppressWarnings({"unused", "WeakerAccess"})
  public void initializeSentianceSDKWithUserLinking(String appId, String appSecret, boolean shouldStart,
                                                    @Nullable OnInitCallback initCallback,
                                                    @Nullable OnStartFinishedHandler startFinishedHandler) {
    initializeAndStartSentianceSDK(appId, appSecret, shouldStart, null, true, initCallback,
      startFinishedHandler);
  }

  @SuppressWarnings({"unused", "WeakerAccess"})
  public void initializeSentianceSDKWithUserLinking(String appId, String appSecret, boolean shouldStart,
                                                    @Nullable String baseUrl, @Nullable OnInitCallback initCallback,
                                                    @Nullable OnStartFinishedHandler startFinishedHandler) {
    initializeAndStartSentianceSDK(appId, appSecret, shouldStart, baseUrl, true, initCallback,
      startFinishedHandler);
  }

  public Boolean initializeSentianceSDKIfUserLinkingCompleted(String appId, String appSecret, boolean shouldStart,
                                                              @Nullable String baseUrl,
                                                              @Nullable OnInitCallback initCallback,
                                                              @Nullable OnStartFinishedHandler startFinishedHandler) {
    if (isThirdPartyLinked()) {
      initializeAndStartSentianceSDK(appId, appSecret, shouldStart, baseUrl, true, initCallback,
        startFinishedHandler);
      return true;
    } else {
      return false;
    }
  }

  public Boolean isThirdPartyLinked() {
    Context context = weakContext.get();
    if (context == null) return false;
    return Sentiance.getInstance(context).isUserLinked();
  }

  @SuppressWarnings({"unused", "WeakerAccess"})
  public void setValueForKey(String key, String value) {
    Context context = weakContext.get();
    if (context == null) return;
    SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
    prefs.edit().putString(key, value).apply();
  }

  @SuppressWarnings({"unused", "WeakerAccess"})
  public String getValueForKey(String key, String defaultValue) {
    Context context = weakContext.get();
    if (context == null) return defaultValue;
    SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
    return prefs.getString(key, defaultValue);
  }

  @SuppressWarnings({"unused", "WeakerAccess"})
  public Boolean isNativeInitializationEnabled() {
    String nativeInitFlag = rnSentianceHelper.getValueForKey(SDK_NATIVE_INIT_FLAG, "");
    return nativeInitFlag.equals("enabled");
  }

  @SuppressWarnings({"unused", "WeakerAccess"})
  public void enableNativeInitialization() {
    setValueForKey(SDK_NATIVE_INIT_FLAG, "enabled");
  }

  @SuppressWarnings({"unused", "WeakerAccess"})
  public void disableNativeInitialization() {
    setValueForKey(SDK_NATIVE_INIT_FLAG, "");
  }

  public interface InitCallback {
    void onSuccess();

    void onFailure(OnInitCallback.InitIssue issue);
  }
}
