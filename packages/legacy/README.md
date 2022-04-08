## Installation

This module requires that the `@react-native-sentiance/crash-detection` and `@react-native-sentiance/core` modules are already setup and installed.

```bash
# Install & setup the core module
npm i @react-native-sentiance/core

# Install & setup the crash detection module
npm i @react-native-sentiance/crash-detection

# Install the legacy module
npm i @react-native-sentiance/legacy
```

## Usage

### Importing the package

You can import the entire contents of the package for use under a namespace of your choosing:

```javascript
import * as SentianceLegacy from "@react-native-sentiance/legacy";
```

or you can require specific functionality using named imports:

```javascript
import {
    enableDetections,
    invokeDummyVehicleCrash,
    createUser
} from "@react-native-sentiance/legacy";
```
