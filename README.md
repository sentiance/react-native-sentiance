# react-native-sentiance

## Demo Application

https://github.com/sentiance/react-native-sentiance-example

## Getting started

```
$ npm install react-native-sentiance --save
```


### iOS 

#### iOS Configuration

1. Go to the **Capabilities** tab of your target settings
1. Turn on **Background Modes** and enable **Location updates**
1. Turn off **Data protection**

![iOS Background Modes](./assets/ios-background-modes.png)

#### iOS Initialization

The correct way to natively initialize on iOS is to do it inside the `didFinishLaunchingWithOptions` method of the `AppDelegate` class.

```objective-c
#import <RNSentiance.h> // Import Sentiance React Native bridge module
...

@implementation AppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
  ...

  [[bridge moduleForName: @"RNSentiance"] initializeWithSuccess:^ {    
    [[bridge moduleForName:@"RNSentiance"] startSDK:nil rejecter:nil];
  } failure:nil];
  
  ...
}
```

### Android

#### Android Installation

Add the following lines to the settings.gradle file in your project's android directory:


```
# android/settings.gradle

include ':react-native-sentiance'
project(':react-native-sentiance').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-sentiance/android')
```

Add the Sentiance repository to the build.gradle file in your project's android directory:

```
# android/build.gradle

allprojects {
    repositories {
        ...
        maven { url "http://repository.sentiance.com" }
    }
}
```

Finally, add a dependency to the React Native project to your app's build.gradle:

```
# android/app/build.gradle

dependencies {
    ...
    implementation project(':react-native-sentiance')
}
```

#### Android Configuration

When targeting API level 29 (Android 10), you must add the following permissions to your app's AndroidManifest.xml file.

```xml
AndroidManifest.xml

<manifest ...>
  <uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION"/>
  <uses-permission android:name="android.permission.ACTIVITY_RECOGNITION"/>
  ...
  
</manifest>
```

**Customize the Notification**

The Sentiance SDK needs to provide a notification to Android, which gets shown to the user when a foreground service is running. You can customize this notification via the **AndroidManifest.xml** file.

```xml
<application ...>
    <meta-data android:name="com.sentiance.react.bridge.notification_title" android:resource="@string/app_name"/>
    <meta-data android:name="com.sentiance.react.bridge.notification_text" android:value="Touch to open."/>
    <meta-data android:name="com.sentiance.react.bridge.notification_icon" android:resource="@mipmap/ic_launcher"/>
    <meta-data android:name="com.sentiance.react.bridge.notification_channel_name" android:value="Sentiance"/>
    <meta-data android:name="com.sentiance.react.bridge.notification_channel_id" android:value="sentiance"/>
    
    ...
</application>
```

#### Android Initialization

The correct way to natively initialize on Android is to do it inside the onCreate() method of the Application class. 

```java
import com.sentiance.react.bridge.RNSentianceHelper;

public class MainApplication extends Application implements ReactApplication {

  @Override
  public void onCreate() {
      super.onCreate();
      ...
      RNSentianceHelper rnSentianceHelper = RNSentianceHelper.getInstance(getApplicationContext());
      rnSentianceHelper.initialize(new RNSentianceHelper.InitCallback() {
          @Override
          public void onSuccess() {
              rnSentianceHelper.startSentianceSDK(null);
          }
      });
  }
}

```


## Usage

```javascript
import RNSentiance from "react-native-sentiance";
```

In order for the SDK to start collecting data you would need to perform two steps.

1. Create a SDK User
2. Start the SDK

#### Create User and start the Sentiance SDK

Create a user and start the sentiance SDK. The following should be placed where and when your application is ready to start collecting data (e.g. on user login, on reaching a particular page)

##### Without User Linking
```js
await RNSentiance.createUser({
  credentials: { appId, appSecret, baseUrl},
})
await RNSentiance.start();
```

##### With User Linking

_Please refer to https://docs.sentiance.com/guide/user-linking for documentation on the user linking._

```js
await RNSentiance.createUser({
  credentials: { appId, appSecret, baseUrl},
  linker: async (data, done) => {
    // request your backend to perform user linking
    await linkUser(data.installId);

    // Ensure you call the "done" after
    done(); 
  }
})
await RNSentiance.start();
```

#### Stopping the Sentiance SDK

Stopping is only allowed after successful initialization. While it's possible to "pause" the detections modules of the Sentiance SDK's, it's not recommended.

```javascript
try {
  const stopResponse = await RNSentiance.stop();
  // SDK stopped properly.
} catch (err) {
  // An error prevented the SDK from stopping correctly
}
```

#### Init status

Checking if SDK is initialized

```javascript
const initState = await RNSentiance.getInitState();
const isInitialized = initState == "INITIALIZED";
```

#### SDK status

The status of the Sentiance SDK

```javascript
const sdkStatus = await RNSentiance.getSdkStatus();
```

The SDK can signal SDK status updates to JavaScript without being invoked directly. You can subscribe to these status updates by creating a new NativeEventEmitter instance around your module, and adding a listener for `SDKStatusUpdate`.

```javascript
import { NativeEventEmitter } from "react-native";

const sentianceEmitter = new NativeEventEmitter(RNSentiance);
const subscription = sentianceEmitter.addListener("SDKStatusUpdate", res => {
  // Returns SDK status
});

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
const label = "correlation_id";
const value = "3a5276ec-b2b2-4636-b893-eb9a9f014938";

await RNSentiance.addUserMetadataField(label, value);
```

#### Remove custom metadata

You can remove previously added metadata fields by passing the metadata label to the removeUserMetadataField function.

```javascript
const label = "correlation_id";

await RNSentiance.removeUserMetadataField(label);
```

#### Adding multiple custom metadata fields

You can add multiple custom metadata fields by passing an object to the addUserMetadataFields function.

```javascript
const metadata = { corrolation_id: "3a5276ec-b2b2-4636-b893-eb9a9f014938" };

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
const metadata = { corrolation_id: "3a5276ec-b2b2-4636-b893-eb9a9f014938" };
const transportModeHint = 1;

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
import { NativeEventEmitter } from "react-native";

const sentianceEmitter = new NativeEventEmitter(RNSentianceLibrary);
const subscription = sentianceEmitter.addListener("TripTimeout", () => {
  // Trip timeout received
});
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

- `getWiFiQuotaLimit`
- `getWiFiQuotaUsage`
- `getMobileQuotaLimit`
- `getMobileQuotaUsage`
- `getDiskQuotaLimit`
- `getDiskQuotaUsage`

#### User Activity

Get user current activity

```javascript
const userActivity = await RNSentiance.getUserActivity();
```

The SDK can signal user activity updates to JavaScript without being invoked directly. You can subscribe to these user activity updates by creating a new NativeEventEmitter instance around your module, and adding a listener for `SDKUserActivityUpdate`.

```javascript
import { NativeEventEmitter } from "react-native";

const sentianceEmitter = new NativeEventEmitter(RNSentiance);
const subscription = sentianceEmitter.addListener(
  "SDKUserActivityUpdate",
  userActivity => {
    // Handle user activity
  }
);

RNSentiance.listenUserActivityUpdates();

// Don't forget to unsubscribe, typically in componentWillUnmount
subscription.remove();
```

Handling user activity

```javascript
const { type, tripInfo, stationaryInfo } = userActivity;

if (type === "USER_ACTIVITY_TYPE_STATIONARY") {
  const { location } = stationaryInfo;

  if (location) {
    const { latitude, longitude } = location;
  }
  //..
} else if (type === "USER_ACTIVITY_TYPE_TRIP") {
  //..
} else if (type === "USER_ACTIVITY_TYPE_UNKNOWN") {
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

#### Clearing/Resetting the SDK

To delete the Sentiance user and its data from the device, you can reset the SDK by calling `RNSentiance.clear`. This allows you to create a new Sentiance user by reinitializing the SDK, and link it to a new third party ID.

```javascript
try {
  await RNSentiance.clear();
  // The SDK was successfully cleared and reset
} catch (err) {
  // Resetting the SDK failed
  // err.name has three values: SDK_INIT_IN_PROGRESS, SDK_RESET_IN_PROGRESS, SDK_RESET_UNKNOWN_ERROR
}
```

#### Crash Event Detection(deprecated)

Subscribe to vehicle crash events.

```javascript
import { NativeEventEmitter } from "react-native";

const sentianceEmitter = new NativeEventEmitter(RNSentiance);
const sdkCrashEventSubscription = sentianceEmitter.addListener(
  "SDKCrashEvent",
  ({ time, lastKnownLocation }) => {
    // parameter time is in milliseconds
    // parameter lastKnownLocation is nullable
    if (lastKnownLocation) {
      const { latitude, longitude } = lastKnownLocation;
    }
  }
);

RNSentiance.listenCrashEvents();

// To unsubscribe
sdkCrashEventSubscription.remove();
```

#### Trip Profiling

##### Handle on-device trip profiling

```javascript
import { NativeEventEmitter } from "react-native";

const sentianceEmitter = new NativeEventEmitter(RNSentiance);

const sdkTripProfilesSubscription = sentianceEmitter.addListener(
  "SDKTripProfile",
  /**
   * tripProfile: {
   *   tripId: String
   *   transportSegments: Array[
   *     TransportSegment{
   *       startTime: number // milliseconds since 1970-01-01
   *       endTime: number // milliseconds since 1970-01-01
   *       vehicleMode: string, VEHICLE | NOT_VEHICLE | IDLE | UNKNOWN
   *       distance?: number // in meters
   *       averageSpeed?: number // the average speed travelled in m/s
   *       topSpeed?: number // the top speed travelled in m/s
   *       percentOfTimeSpeeding?: number // the percent of time the user was speeding
   *       hardEvents?: Array[
   *         HardEvent{
   *           magnitude: number, the magnitude of this hard event in m/s2
   *           timestamp: milliseconds since 1970-01-01
   *         }
   *       ]
   *     }
   *   ]
   * }
   */
  (tripProfile) => {
  }
);
RNSentiance.listenTripProfiles();

// To unsubscribe
sdkTripProfilesSubscription.remove();
```

##### Update Trip profiling config

```javascript
/**
 * enableFullProfiling:
 *   If set to true, full trip profiling will be enabled allowing the Sentiance platform to profile
 *   the trip and the results made available via the API. In addition, the app will no longer receive trip profiles via
 *   the "SDKTripProfile" listener.
 *   If set to false, on-device trip profiling will be enabled.
 * speedLimit:
 *   Sets the speed limit in km/h, which is used to determine the percent of time the user was speeding.
 *   If null, the SDK will use an internal default value.
 */
try {
  await RNSentiance.updateTripProfileConfig({ enableFullProfiling: false, speedLimit: 80 })
} catch (err) {
  console.error(err)
}
```

###### Determine if the app should initialize Sentiance SDK natively

To make user linking possible, the first SDK initialization should be executed in JS. After it completes successfully,
```await RNSentiance.enableNativeInitialization()``` should be invoked.

In AppDelegate (iOS) and MainApplication (Android), ```isNativeInitializationEnabled``` can be used to determine if the SDK should be initialized natively.

To disable native initialization, invoke ```await RNSentiance.disableNativeInitialization()```.

Please refer to our [example app](https://github.com/sentiance/react-native-sentiance-example) for a complete usage.

##### Invoke a dummy vehicle crash event

```javascript
await RNSentiance.invokeDummyVehicleCrash()
```

##### Check if crash detection is supported

```javascript
const crashDetectionSupported = await RNSentiance.isVehicleCrashDetectionSupported('TRIP_TYPE_SDK')
if (crashDetectionSupported) {
  // setup vehicle crash event listener
}
```

#### Vehicle Crash Event Detection

Listen to vehicle crash events.

```javascript
import { NativeEventEmitter } from "react-native";

const sentianceEmitter = new NativeEventEmitter(RNSentiance);
const vehicleCrashEventSubscription = sentianceEmitter.addListener(
  "VehicleCrashEvent",
  (event: VehicleCrashEvent) => {}
);

RNSentiance.listenVehicleCrashEvents();

// To unsubscribe
vehicleCrashEventSubscription.remove();
```