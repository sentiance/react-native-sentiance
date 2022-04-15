## Installation

This module requires that the `@react-native-sentiance/core` module is already setup and installed.

```bash
# Install & setup the core module
npm i @react-native-sentiance/core

# Install the crash detection module
npm i @react-native-sentiance/crash-detection
```

## Usage

### Importing the package

You can import the entire contents of the package for use under a namespace of your choosing:

```javascript
import * as SentianceCrashDetection from "@react-native-sentiance/crash-detection";
```

or you can require specific functionality using named imports:

```javascript
import {
    listenVehicleCrashEvents,
    invokeDummyVehicleCrash,
    isVehicleCrashDetectionSupported
} from "@react-native-sentiance/crash-detection";
```
