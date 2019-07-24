
# react-native-sentiance

## Status
*`react-native-sentiance` is still in an early development stage and not ready for production use just yet. Please try it out, give feedback, and help fix bugs.


## Getting started

`$ npm install react-native-sentiance --save`

### Manual installation


#### iOS

__with Cocoapods__
1. Add `RNSentiance` Pod to your Podfile
	```
	pod 'RNSentiance', :path => '../node_modules/react-native-sentiance/ios/RNSentiance.podspec'
	```
2. Add `SENTSDK` Pod to your Podfile
	```
	pod 'SENTSDK', :podspec => '../node_modules/react-native-sentiance/ios/SENTSDK.podspec'
	```
3. Run `pod install` in your `ios` folder


__without Cocoapods__
1. [Download](https://developers.sentiance.com/docs/sdk/ios/integration) the latest version of the Sentiance iOS SDK from our developer documentation.
2. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
3. Go to `node_modules` ➜ `react-native-sentiance-library` and add `RNSentianceLibrary.xcodeproj`
4. In XCode, in the project navigator, select `RNSentianceLibrary.xcodeproj`. Add the folder where `SENTSDK.framework` is located to `Search Paths` ➜ `Framework Search Paths`
5. In XCode, in the project navigator, select your project. Add `libRNSentianceLibrary.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
6. Run your project (`Cmd+R`)<


__Configuring capabilities__
1. Go to the __Capabilities__ tab of your target settings
1. Turn on __Background Modes__ and enable __Location updates__
1. Turn off __Data protection__

![iOS Background Modes](./assets/ios-background-modes.png)

#### Native initialization

In your `AppDelegate` add the following:

  ```objective-c
  #import <React/RCTBundleURLProvider.h>
  #import <React/RCTRootView.h>
  @import SENTSDK;
  #import <RNSentiance.h>
  ```

```objective-c
-(BOOL) application: (UIApplication * ) application didFinishLaunchingWithOptions: (NSDictionary * ) launchOptions {
  NSURL * jsCodeLocation;

  jsCodeLocation = [
    [NSBundle mainBundle] URLForResource: @ "main"
    withExtension: @ "jsbundle"
  ];

  RCTBridge * bridge = [
    [RCTBridge alloc] initWithBundleURL: jsCodeLocation
    moduleProvider: nil
    launchOptions: launchOptions
  ];

  RNSentiance * sentiance = [bridge moduleForClass: RNSentiance.class];

  NSString * APP_ID = @ "_YOUR_APP_ID_";
  NSString * SECRET = @ "_YOUR_APP_ID_SECRET_";

  SENTConfig * config = [
    [SENTConfig alloc] initWithAppId: APP_ID secret: SECRET link: sentiance.getMetaUserLinker launchOptions: launchOptions
  ];
  
  [config setDidReceiveSdkStatusUpdate: sentiance.getSdkStatusUpdateHandler];

  [[SENTSDK sharedInstance] initWithConfig:config success :^{
    [self startSentianceSdk];
  } failure:^(SENTInitIssue issue) {
    NSLog(@"Failure issue: %lu", (unsigned long)issue);
  }];

  return YES;
}

- (void)startSentianceSdk {
  [[SENTSDK sharedInstance] start:^(SENTSDKStatus *status) {
    if ([status startStatus] == SENTStartStatusStarted) {
      NSLog(@"SDK started properly");
    } else if ([status startStatus] == SENTStartStatusPending) {
      NSLog(@"Something prevented the SDK to start properly. Once fixed, the SDK will start automatically");
    }
    else {
      NSLog(@"SDK did not start");
    }
  }];

```


#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.reactlibrary.RNSentiancePackage;` to the imports at the top of the file
  - Add `new RNSentiancePackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-sentiance'
  	project(':react-native-sentiance').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-sentiance/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
    compile project(':react-native-sentiance')
  	```
4. Add following entry to `android/build.gradle` 
  	```
    allprojects {
      repositories {
        ...
        maven {
            url "http://repository.sentiance.com"
        }
      }
    }
  	```
4. Configure foreground notification, Add the following lines to application's `AndroidManifest.xml` file inside `<application>` tag:
  	```xml
    <meta-data android:name="com.sentiance.react.bridge.notification_title" android:resource="@string/app_name"/>
    <meta-data android:name="com.sentiance.react.bridge.notification_text" android:value="Touch to open."/>
    <meta-data android:name="com.sentiance.react.bridge.notification_icon" android:resource="@mipmap/ic_launcher"/>
    <meta-data android:name="com.sentiance.react.bridge.notification_channel_name" android:value="Sentiance"/>
    <meta-data android:name="ccom.sentiance.react.bridge.notification_channel_id" android:value="sentiance"/>
  	```


## Usage
```javascript
import RNSentiance from 'react-native-sentiance';
```

#### Initializing the Sentiance SDK
Initialization is a very important step; before initialization, almost none of the methods on the Sentiance SDK interface are allowed to be called (with the exception of `init`, `isInitialized` and `getVersion`).
```javascript
try {
	const initResponse = await RNSentiance.init('APPID', 'SECRET');
	// SDK init has successfully initialized
} catch (err) {
	// SDK init has failed initializing
}
```

_NOTE: Ideally, initializing the SDK is done from `Application.onCreate` as this will guarantee that the SDK is running as often as possible. If your application uses a login flow, you will want to start the SDK only if the user is logged in, at that point you could start the SDK through JavaScript. Once the user is logged in, the SDK should always start before the end of `onCreate`. Please refer to https://developers.sentiance.com/docs/sdk/android/integration for documentation on the Android SDK integration._


#### Native initialization
1. Create `RNSentiancePackage`  instance
```java
RNSentiancePackage rnSentiancePackage = new RNSentiancePackage();
```
 2. In `ReactNativeHost#getPackages()` return `rnSentiancePackage` instance instead of creating new one
```java
private final ReactNativeHost mReactNativeHost = new ReactNativeHost(this) {
    @Override
    protected List<ReactPackage> getPackages() {
        return Arrays.<ReactPackage>asList(
                new MainReactPackage(),
                rnSentiancePackage
        );
    }
    
    //...
};
```
 3. Inside `Application#onCreate()` method, Initialize and start sentiance SDK
```java
  @Override
  public void onCreate() {
      super.onCreate();
       //create react context in background so that SDK could be delivered to JS even if app is not running
      if(!mReactNativeHost.getReactInstanceManager().hasStartedCreatingInitialContext())
          mReactNativeHost.getReactInstanceManager().createReactContextInBackground();
       //create notification
      //https://docs.sentiance.com/sdk/getting-started/android-sdk/configuration/sample-notification
      Notification notification = createNotification();
       // Create the config.
      SdkConfig config = new SdkConfig.Builder(SENTIANCE_APP_ID, SENTIANCE_SECRET, notification)
              .setOnSdkStatusUpdateHandler(rnSentiancePackage.getOnSdkStatusUpdateHandler())
              .build();
       // Initialize  and start  Sentiance SDK.
      Sentiance.getInstance(this).init(config, new OnInitCallback() {
          @Override
          public void onInitSuccess() {
              //init success, start sentiance SDK
              startSentianceSDK();
          }
           @Override
          public void onInitFailure(InitIssue issue, @Nullable Throwable throwable) {
              //init fail
          }
      });
  }
  
  private void startSentianceSDK() {
    Sentiance.getInstance(getApplicationContext()).start(new OnStartFinishedHandler() {
        @Override
        public void onStartFinished(SdkStatus sdkStatus) {
        
        }
    });
  }
 ```


#### Starting the Sentiance SDK
Starting is only allowed after successful initialization. Resolves with an SDK status object.
```javascript
try {
	const startResponse = await RNSentiance.start();
	const { startStatus } = startResponse;
	if (startStatus === 'STARTED') {
		// SDK started properly.
	} else if (startStatus === 'PENDING') {
		// Something prevented the SDK to start properly. Once fixed, the SDK will start automatically.
	}
} catch (err) {
	// SDK did not start.
}
```

#### Stopping the Sentiance SDK
Stopping is only allowed after successful initialization. While it's possible to "pause" the detections modules of the Sentiance SDK's, it's not recommended.
```javascript
try {
	const stopResponse = await RNSentiance.stop();
	// SDK stopped properly.
} catch(err) {
	// An error prevented the SDK from stopping correctly
}
```

#### Init status
Checking if SDK is initialized
```javascript
const isInitialized = await RNSentiance.isInitialized();
```

#### SDK status
The status of the Sentiance SDK
```javascript
const sdkStatus = await RNSentiance.getSdkStatus();
```

The SDK can signal SDK status updates to JavaScript without being invoked directly. You can subscribe to these status updates by creating a new NativeEventEmitter instance around your module, and adding a listener for `SDKStatusUpdate`.
```javascript
import { NativeEventEmitter } from 'react-native'

const sentianceEmitter = new NativeEventEmitter(RNSentiance);
const subscription = sentianceEmitter.addListener(
	'SDKStatusUpdate',
	res => {
		// Returns SDK status
	}
);

// Don't forget to unsubscribe, typically in componentWillUnmount
subscription.remove();
```

#### Get SDK version
```javascript
const version = await RNSentiance.getVersion();
```

#### Get user id
If the SDK is initialized, you can get the user id as follows. This user id will allow you to interact with the API's from Sentiance. You need a token and user to authorize requests and query the right data.
```javascript
const userId = await RNSentiance.getUserId();
```

#### Get user access token
If the SDK is initialized, you can get a user access token as follows. This token will allow you to interact with the API's from Sentiance. You need a token and user to authorize requests and query the right data. If the token has expired, or will expire soon, the SDK will get a new bearer token before passing it to the callback. Generally, this operation will complete instantly by returning a cached bearer token, but if a new token has to be obtained from the Sentiance API, there is a possibility it will fail.
```javascript
const { tokenId } = await RNSentiance.getUserAccessToken();
```

#### Adding custom metadata
Custom metadata allows you to store text-based key-value user properties into the Sentiance platform.
Examples are custom user id's, application related properties you need after the processing, ...
```javascript
const label = 'correlation_id'
const value = '3a5276ec-b2b2-4636-b893-eb9a9f014938'

await RNSentiance.addUserMetadataField(label, value);
```

#### Remove custom metadata
You can remove previously added metadata fields by passing the metadata label to the removeUserMetadataField function.
```javascript
const label = 'correlation_id'

await RNSentiance.removeUserMetadataField(label);
```

#### Adding multiple custom metadata fields
You can add multiple custom metadata fields by passing an object to the addUserMetadataFields function.
```javascript
const metadata = { corrolation_id: '3a5276ec-b2b2-4636-b893-eb9a9f014938' }

await RNSentiance.addUserMetadataFields(metadata);
```

#### Starting trip
Whenever you call startTrip on the SDK, you override moving state detection and the SDK will track the trip until you call stopTrip or until the timeout (2 hours) is reached. `startTrip` accepts a metadata object and a transport mode hint (`number`) as parameters.

Transport mode hint:
```
SENTTransportModeUnknown = 1,
SENTTransportModeCar = 2,
SENTTransportModeBicycle = 3,
SENTTransportModeOnFoot = 4,
SENTTransportModeTrain = 5,
SENTTransportModeTram = 6,
SENTTransportModeBus = 7,
SENTTransportModePlane = 8,
SENTTransportModeBoat = 9,
SENTTransportModeMetro = 10,
SENTTransportModeRunning = 11
```

Example:
```javascript
const metadata = { corrolation_id: '3a5276ec-b2b2-4636-b893-eb9a9f014938' }
const transportModeHint = 1

try {
	await RNSentiance.startTrip(metadata, transportModeHint);
	// Trip is started
} catch (err) {
	// Unable to start trip
}
```

#### Stopping trip
```javascript
try {
	const trip = await RNSentiance.stopTrip();
	// Stopped trip
} catch (err) {
	// Unable to stop trip
}
```

The SDK can also signal trip timeouts to JavaScript. You can subscribe to these trip timeouts by creating a new NativeEventEmitter instance around your module, and adding a listener for `TripTimeout`.
```javascript
import { NativeEventEmitter } from 'react-native'

const sentianceEmitter = new NativeEventEmitter(RNSentianceLibrary)
const subscription = sentianceEmitter.addListener(
	'TripTimeout',
	() => {
		// Trip timeout received
	}
)
```

#### Trip status
Checking trip status
```javascript
const isTripOngoing = await RNSentiance.isTripOngoing();
```

#### Control sending data
If you want to override the default behavior, you can initiate a force submission of detections. Ideally, you use this method only after explaining to the user that your app will consume more bandwidth in case the device is not connected to Wi-Fi.

```javascript
try {
	await RNSentiance.submitDetections();
} catch (err) {
	// Something went wrong with submitting data, for more information, see the error variable
}
```

#### Disk, mobile network and Wi-Fi quotas
The actual usages and limits in bytes can be obtained using the getWiFiQuotaUsage, getWiFiQuotaLimit and similar methods on the Sentiance SDK interface.

```javascript
const limit = await RNSentiance.getWiFiQuotaLimit();
```

All quota functions:

* `getWiFiQuotaLimit`
* `getWiFiQuotaUsage`
* `getMobileQuotaLimit`
* `getMobileQuotaUsage`
* `getDiskQuotaLimit`
* `getDiskQuotaUsage`

#### User Activity

Get user current activity

```javascript
const userActivity = await RNSentiance.getUserActivity();
```

The SDK can signal user activity updates to JavaScript without being invoked directly. You can subscribe to these user activity updates by creating a new NativeEventEmitter instance around your module, and adding a listener for `SDKUserActivityUpdate`.
```javascript
import { NativeEventEmitter } from 'react-native'

const sentianceEmitter = new NativeEventEmitter(RNSentiance);
const subscription = sentianceEmitter.addListener(
	'SDKUserActivityUpdate',
	userActivity => {
		// Returns SDK status
	}
);

// Don't forget to unsubscribe, typically in componentWillUnmount
subscription.remove();
```

Handling user activity
```javascript
const { type, tripInfo, stationaryInfo } = userActivity;

if (type === 'USER_ACTIVITY_TYPE_STATIONARY') {
  const { location } = stationaryInfo;

  if (location) {
    const { latitude, longitude } = location;
  }
  //..
} else if (type === 'USER_ACTIVITY_TYPE_TRIP') {
  //..
} else if (type === 'USER_ACTIVITY_TYPE_UNKNOWN') {
  //..
}

```

#### Update SDK foreground notification (ANDROID ONLY)
Updates the title and text of SDK notification. After calling this method, any notification shown by the SDK will be updated.

Note that this change is valid only during the process's lifetime. After the app process restarts, the SDK will display the default notification.

```javascript
/**
 * {string} title
 * {string} message
 */
await RNSentiance.updateSdkNotification("RN SDK Sample", "SDK is running");
```
