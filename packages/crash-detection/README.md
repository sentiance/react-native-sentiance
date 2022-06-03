## Installation

This module requires that the `@sentiance-react-native/core` module is already setup and installed.

```bash
# Install & setup the core module
npm i @sentiance-react-native/core

# Install the crash detection module
npm i @sentiance-react-native/crash-detection
```

## Usage

### Importing the package

You can import the entire contents of the package for use under a namespace of your choosing:

```javascript
import * as SentianceCrashDetection from "@sentiance-react-native/crash-detection";
```

or you can require specific functionality using named imports:

```javascript
import {
    listenVehicleCrashEvents,
    invokeDummyVehicleCrash,
    isVehicleCrashDetectionSupported
} from "@sentiance-react-native/crash-detection";
```
