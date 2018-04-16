
# react-native-sentiance

## Getting started

`$ npm install react-native-sentiance --save`

### Mostly automatic installation

`$ react-native link react-native-sentiance`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-sentiance-library` and add `RNSentianceLibrary.xcodeproj`
3. In XCode, in the project navigator, select `RNSentianceLibrary.xcodeproj`. Add the folder where `SENTTransportDetectionSDK.framework` is located to `Search Paths` ➜ `Framework Search Paths`
4. In XCode, in the project navigator, select your project. Add `libRNSentianceLibrary.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
5. Run your project (`Cmd+R`)<

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

Android is not supported yet!


## Usage
```javascript
import RNSentianceLibrary from 'react-native-sentiance-library';
```

#### Initializing the Sentiance SDK
Initialization is a very important step; before initialization, almost none of the methods on the Sentiance SDK interface are allowed to be called (with the exception of `init`, `isInitialized` and `getVersion`).
```javascript
RNSentianceLibrary.init('APPID', 'SECRET').then(
	res => {
		// SDK init has successfully initialized
	},
	err => {
		// SDK init has failed initializing
	})
```

#### Starting the Sentiance SDK
Starting is only allowed after successful initialization.
```javascript
RNSentianceLibrary.start().then(
	res => {
		if (res === 'STARTED') {
			// SDK started properly.
		} else if (res === 'PENDING') {
			// Something prevented the SDK to start properly. Once fixed, the SDK will start automatically.
		}
	},
	err => {
		// SDK did not start.
	})
```

#### Stopping the Sentiance SDK
Stopping is only allowed after successful initialization. While it's possible to "pause" the detections modules of the Sentiance SDK's, it's not recommended.
```javascript
RNSentianceLibrary.stop().then(
	res => {
		// SDK stopped properly.
	},
	err => {
		// SDK did not stop.
	})
```

#### Cooldown period
It's best practice to implement a cooldown period to protect against flip-flops or state race conditions. To cancel a scheduled stop-with-cooldown, simply call start again.
```javascript
RNSentianceLibrary.stopAfter(300).then(
	res => {
		// SDK scheduled stop properly.
	},
	err => {
		// SDK did not schedule stop.
	})
```

#### Init status
Checking if SDK is initialized
```javascript
RNSentianceLibrary.isInitialized().then(
	res => {
		if (res) {
			// SDK is initialized
		} else {
			// SDK is not initialized
		}
	},
	err => {
		// Unable to check init status
	})
```

#### SDK status
The status of the Sentiance SDK
```javascript
RNSentianceLibrary.getSdkStatus().then(
	res => {
		// Returns SDK status
	},
	err => {
		// Unable to get SDK status
	}
)
```

The SDK can signal SDK status updates to JavaScript without being invoked directly. You can subscribe to these status updates by creating a new NativeEventEmitter instance around your module, and adding a listener for `SDKStatusUpdate`.
```javascript
import { NativeEventEmitter } from 'react-native'

const sentianceEmitter = new NativeEventEmitter(RNSentianceLibrary)
const subscription = sentianceEmitter.addListener(
	'SDKStatusUpdate',
	res => {
		// Returns SDK status
	}
)

// Don't forget to unsubscribe, typically in componentWillUnmount
subscription.remove();
```

#### Get SDK version
```javascript
RNSentianceLibrary.getVersion().then(
	res => {
		// Returns SDK version
	},
	err => {
		// Unable to get SDK version
	}
)
```

#### Get user id
If the SDK is initialized, you can get the user id as follows. This user id will allow you to interact with the API's from Sentiance. You need a token and user to authorize requests and query the right data.
```javascript
RNSentianceLibrary.getUserId().then(
	res => {
		// Returns user id
	},
	err => {
		// Unable to get user id
	}
)
```

#### Get user access token
If the SDK is initialized, you can get a user access token as follows. This token will allow you to interact with the API's from Sentiance. You need a token and user to authorize requests and query the right data. If the token has expired, or will expire soon, the SDK will get a new bearer token before passing it to the callback. Generally, this operation will complete instantly by returning a cached bearer token, but if a new token has to be obtained from the Sentiance API, there is a possibility it will fail.
```javascript
RNSentianceLibrary.getUserAccessToken().then(
	res => {
		// Returns user access token
	},
	err => {
		// Unable to get user access token
	}
)
```

#### Adding custom metadata
Custom metadata allows you to store text-based key-value user properties into the Sentiance platform.
Examples are custom user id's, application related properties you need after the processing, ...
```javascript
const label = 'correlation_id'
const value = '3a5276ec-b2b2-4636-b893-eb9a9f014938'

RNSentianceLibrary.addUserMetadataField(label, value).then(
	res => {
		// Added user metadata field
	},
	err => {
		// Unable to add user metadata field
	}
)
```

#### Remove custom metadata
You can remove previously added metadata fields by passing the metadata label to the removeUserMetadataField function.
```javascript
const label = 'correlation_id'

RNSentianceLibrary.removeUserMetadataField(label).then(
	res => {
		// Removed user metadata field
	},
	err => {
		// Unable to remove user metadata field
	}
)
```

#### Adding multiple custom metadata fields
You can add multiple custom metadata fields by passing an object to the addUserMetadataFields function.
```javascript
const metadata = { corrolation_id: '3a5276ec-b2b2-4636-b893-eb9a9f014938' }

RNSentianceLibrary.addUserMetadataFields(metadata).then(
	res => {
		// Added user metadata fields
	},
	err => {
		// Unable to add user metadata fields
	}
)
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

RNSentianceLibrary.startTrip(metadata, transportModeHint).then(
	res => {
		// Started trip
	},
	err => {
		// Unable to start trip
	}
)
```

#### Stopping trip
```javascript
RNSentianceLibrary.stopTrip().then(
	res => {
		// Stopped trip, returns a trip object
	},
	err => {
		// Unable to stop trip
	}
)
```

The SDK can also signal trip timeouts to JavaScript. You can subscribe to these trip timeouts by creating a new NativeEventEmitter instance around your module, and adding a listener for `TripTimeout`.
```javascript
import { NativeEventEmitter } from 'react-native'

const sentianceEmitter = new NativeEventEmitter(RNSentianceLibrary)
const subscription = sentianceEmitter.addListener(
	'TripTimeout',
	res => {
		// Returns a trip object
	}
)
```

#### Trip status
Checking trip status
```javascript
RNSentianceLibrary.isTripOngoing().then(
	res => {
		if (res) {
			// Trip is ongoing
		} else {
			// Trip is not ongoing
		}
	},
	err => {
		// Unable to check trip status
	}
)
```

#### Adding external events
When the external event state becomes active.

Event types:
```
SENTExternalEventTypeOther = 1,
SENTExternalEventTypeBeacon = 2,
SENTExternalEventTypeCustomRegion = 3
```

Example:
```javascript
const externalEventType = 1
const timestamp = Date.now() // timestamp should be in UNIX Epoch time
const id = 'a247ee7b-6438-477b-ab23-b8f039db2106'
const label = 'iBeacon is within 10 meter'

RNSentianceLibrary.registerExternalEvent(externalEventType, timestamp, id, label).then(
	res => {
		// Registered external event
	},
	err => {
		// Unable to register external event
	}
)
```

#### Deregistering external events
When the external event state becomes inactive.

Event types:
```
SENTExternalEventTypeOther = 1,
SENTExternalEventTypeBeacon = 2,
SENTExternalEventTypeCustomRegion = 3
```

Example:
```javascript
const externalEventType = 1
const timestamp = Date.now() // timestamp should be in UNIX Epoch time
const id = 'a247ee7b-6438-477b-ab23-b8f039db2106'
const label = 'iBeacon is out of range'

RNSentianceLibrary.deregisterExternalEvent(externalEventType, timestamp, id, label).then(
	res => {
		// Registered external event
	},
	err => {
		// Unable to register external event
	}
)
```

#### Control sending data
If you want to override the default behavior, you can initiate a force submission of detections. Ideally, you use this method only after explaining to the user that your app will consume more bandwidth in case the device is not connected to Wi-Fi.

```javascript
RNSentianceLibrary.submitDetections().then(
	res => {
		// If any data was pending, this now is submitted.
	},
	err => {
		// Something went wrong with submitting data, for more information, see the error variable
	}
)
```

#### Disk, mobile network and Wi-Fi quotas
The actual usages and limits in bytes can be obtained using the getWiFiQuotaUsage, getWiFiQuotaLimit and similar methods on the Sentiance SDK interface.

```javascript
RNSentianceLibrary.getWiFiQuotaLimit().then(
	res => {
		// Returns quota
	},
	err => {
		// Unable to get quota
	}
)
```

All quota functions:

* `getWiFiQuotaLimit`
* `getWiFiQuotaUsage`
* `getMobileQuotaLimit`
* `getMobileQuotaUsage`
* `getDiskQuotaLimit`
* `getDiskQuotaUsage`
* `getWiFiLastSeenTimestamp`

#### Last time seen Wi-Fi
```javascript
RNSentianceLibrary.getWiFiLastSeenTimestamp().then(
	res => {
		// Returns Wi-Fi last seen timestamp
	},
	err => {
		// Unable to get Wi-Fi last seen timestamp
	}
)
```