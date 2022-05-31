# Creating new SDK modules

Start by creating a new directory with the name of the new module under the `packages` folder:

```bash
/packages/newModule
```

A typical SDK module has the following structure:

```
newModule
│   README.md
│   package.json
│
└───android <-- The module's native android code goes here
└───ios <-- The module's native iOS code goes here
└───lib <-- The module's Javascript code goes here
    │   index.js <-- The public interface that is exposed to the module's consumers
    │   index.d.ts <-- Typescript definitions for the public Javascript interface
```

A typical, minimalistic SDK module's `package.json` file looks like this:

```json
{
  "name": "@react-native-sentiance/newModule",
  "version": "0.0.1",
  "description": "Type module description here",
  "main": "lib/index.js",
  "typings": "lib/index.d.ts",
  "scripts": {
    "test": "echo \"Error: no test specified\" && exit 1",
    "lint": "npx eslint lib/index.d.ts"
  },
  "keywords": [
    "react-native",
    "newModule",
    "sentiance"
  ],
  "peerDependencies": {
    "react-native": ">=0.60"
  },
  "homepage": "https://github.com/sentiance/react-native-sentiance/packages/newModule#readme",
  "repository": "github:sentiance/react-native-sentiance"
}
```

The key fields to note here are:

* **name**: the "fully qualified" name of the module. Notice the use of the Sentiance organisation name (`@react-native-sentiance/`)
* **version**: the version of the module.
* **description**: a human friendly description of the module to help people discover this module.
* **main**: the entry point of this module and our public Javascript interface.
* **typings**: specifies the main typescript declaration file for this module.
* **scripts**: every module has 2 npm scripts by default: `test` and `lint`. These scripts run on CircleCI during development and before publishing this module to NPM. For more information, check the related CircleCI workflows.
* **peerDependencies**: this is where we specify the minimum supported version of the React Native library.

## General configuration

### Edit the top level `package.json` file

Open up the `packages/package.json` file, and add a new NPM workspace for the new module:

```json
  "workspaces": [
    ...,
    "packages/newModule"
  ],
```

This guarantees that the new module gets picked up by the npm scripts that take care of publishing all SDK modules locally, and that CircleCI will also be able to pick up and publish the new module along with the others.

### Edit the `sync-package-versions.js` script

This script takes care of updating all modules' versions and the Sentiance peer dependencies to match a certain version.
It uses the names of the modules specified under `SENTIANCE_MODULES` as input to know exactly the names of the modules to be impacted.
open up the script and edit the `SENTIANCE_MODULES` array to include the new module as well:

```javascript
const SENTIANCE_MODULES = [..., 'newModule'];
```

## Android configuration

Open up the `settings.gradle` file that could be found under the `packages` folder, and add a new entry for the new module:

```javascript
[..., 'newModule'].each { // Add the new module to this list
  ...
}
```

This ensures that the new module will be picked up by Android Studio along the other modules when opening up the `packages` folder as a standalone AS project. 

## iOS configuration

TBD

