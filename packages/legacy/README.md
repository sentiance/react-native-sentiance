## Installation

This module requires that the `@sentiance-react-native/crash-detection` and `@sentiance-react-native/core` modules are already setup and installed.

```bash
# Install & setup the core module
npm i @sentiance-react-native/core

# Install & setup the crash detection module
npm i @sentiance-react-native/crash-detection

# Install the legacy module
npm i @sentiance-react-native/legacy
```

## Usage

### Importing the package

You can import the entire contents of the package for use under a namespace of your choosing:

```javascript
import * as SentianceLegacy from "@sentiance-react-native/legacy";
```

or you can require specific functionality using named imports:

```javascript
import {
    enableDetections,
    invokeDummyVehicleCrash,
    createUser
} from "@sentiance-react-native/legacy";
```
