# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [4.8.4] - 2022-07-27

- Upgrade iOS SDK to [5.15.0](https://docs.sentiance.com/sdk/changelog/ios#5.14.0-30-may-2022)

## [4.8.3] - 2022-05-30

### Changed

- Upgrade Android SDK to [4.22.0](https://docs.sentiance.com/sdk/changelog/android)

## [4.8.2] - 2022-05-30

### Changed

- Upgrade iOS SDK to [5.14.0](https://docs.sentiance.com/sdk/changelog/ios#5.14.0-30-may-2022)

## [4.8.1] - 2022-05-04

### Changed

- Upgrade iOS SDK to [5.13.0](https://docs.sentiance.com/sdk/changelog/ios#5.13.0-5-may-2022)

## [4.8.0] - 2022-04-06

### Fixed

- createUserExperimental to resolve only after SDK is INITIALIZED

### Changed

- createUserExperimental does not start the SDK by default

## [4.7.2] - 2022-03-10

### Fixed

- createUserExperimental hanging at "getUserId" on Android

## [4.7.1] - 2022-02-28

### Added

- Experimental createUser workflow (method: `createUserExperimental`, `enableExperimental`, `disableExperimental`, `resetExperimental`)
- Experimental native background initialization workflow (ios: `initializeWithSuccess`, android: `initialize`)

## [4.7.0] - 2022-02-03

### Changed

- Upgrade the Android SDK to use latest available hotfix release for [4.21.x] [changelog](https://docs.sentiance.com/sdk/changelog/android)

## [4.6.1] - 2022-02-03

### Fixed

- Runtime exception on Android 12 caused by unspecified PendingIntent mutability.

## [4.6.0] - 2022-01-06

### Changed

- Upgrade Android SDK to use latest available hotfix release for [4.20.x] [changelog](https://docs.sentiance.com/sdk/changelog/android)

## [4.5.0] - 2022-01-05

### Changed

- Upgrade iOS SDK to use latest available release for [5.12.x] [changelog](http://docs.sentiance.com/sdk/changelog/ios)

## [4.4.7] - 2021-07-23

### Changed

- Upgrade iOS SDK to use latest available release for [5.11.x] [changelog](http://docs.sentiance.com/sdk/changelog/ios)

## [4.4.5] - 2021-07-02

### Changed

- Upgrade Android SDK to use latest available hotfix release for [4.19.x] [changelog](https://docs.sentiance.com/sdk/changelog/android)

## [4.4.3] - 2021-06-10

### Changed

- Upgrade Android SDK to [4.19.1](https://docs.sentiance.com/sdk/changelog/android#4-19-1-9-jun-2021)

## [4.4.1] - 2021-04-20

### Fixed

- getValueForKey function signature on iOS

## [4.4.0] - 2021-04-19

### Changed

- Upgrade iOS SDK to [5.10.0](https://docs.sentiance.com/sdk/changelog/ios#5-10-0-14-apr-2021)
- Upgrade Android SDK to [4.19.0](https://docs.sentiance.com/sdk/changelog/android#4-19-0-14-apr-2021)

### Added

- `isThirdPartyLinked` to determine whether third part linking has been completed successfully
- `initSDKIfUserLinkingCompleted` to initialize the SDK only if user linking has been completed successfully

## [4.3.1] - 2021-02-03

### Changed

- Updated iOS integration doc

## [4.3.0] - 2021-02-03

### Changed

- Upgrade iOS SDK to [5.9.0](https://docs.sentiance.com/sdk/changelog/ios#5-9-0-29-jan-2021)
- Upgrade Android SDK to [4.18.0](https://docs.sentiance.com/sdk/changelog/android#4-18-0-14-jan-2021)

### Added

- `invokeDummyVehicleCrash` invoke a dummy vehicle crash event for development purpose
- `isVehicleCrashDetectionSupported`
- `listenVehicleCrashEvents`

### Removed

- `SENTSDK.podspec` The reference of SENTSDK.podspec is no longer needed in the enclosing App's Podfile.

## [4.2.0] - 2020-10-07

### Added

- enableNativeInitialization
- disableNativeInitialization
- isNativeInitializationEnabled

## [4.1.0] - 2020-09-07

### Fixed

- Broken startWithStopDate on android.

### Added

- startWithStopDate on iOS.

## [4.0.2] - 2020-08-04

### Changed

- Upgrade iOS SDK to [5.7.4](https://docs.sentiance.com/sdk/changelog/ios#5-7-4-15-jul-2020)
- Omit the SDKStatusEvent from startSDK calls
- Restore the top most activitiy when clicking the notification on Android

## [4.0.1] - 2020-07-30

### Changed

- Fix a notification issue on the Android side. A notification will pop up once the SDK starts. Clicking the notification will bring up the launch intent.

## [4.0.0] - 2020-07-06

### Changed

- The type of the parameter for `isTripOngoing` in the iOS bridge is changed to `String`, which is aligned with the Android bridge.
- Error codes and error messages are standardized on every method in iOS and Android bridges.
- Bridge methods resolve promises with truthy values instead of `null`s.
- Upgrade Android SDK to [4.16.2](https://docs.sentiance.com/sdk/changelog/android#4-16-2-2-jul-2020)

### Added

- Comprehensive type definitions for all SDK objects, events and methods.

## [3.4.0] - 2020-06-25

### Changed

- Upgrade Android SDK to [4.16.1](https://docs.sentiance.com/sdk/changelog/android#4-16-1-19-jun-2020)
- Upgrade iOS SDK to [5.7.1](https://docs.sentiance.com/sdk/changelog/ios#5-7-1-4-jun-2020)

### Added

- Support for on-device trip profiling and hard event detection. This feature is not enabled by default.
- Missing android SDK status fields.

### Fixed

- User linking after SDK rest.
- `init` / `start` promise never resolved (Android Only).

## [3.3.0] - 2020-05-22

### Removed

- Sentiance china sdk code

## [3.2.0] - 2020-04-27

### Added

- The ability to listen to crash detection events

## [3.1.3] - 2020-03-30

### Changed

- Allow `baseUrl` to be nullable when initializing the Sentiance SDK

## [3.1.2] - 2020-03-24

### Changed

- Upgrade iOS SDK to [5.6.1](https://docs.sentiance.com/sdk/changelog/ios#5-6-1-21-feb-2020)

## [3.1.1] - 2020-03-24

### Added

- `reset` typing for typescript

## [3.1.0] - 2020-02-06

### Added

- `reset` method to reset the Sentiance SDK

### Changed

- Upgrade Android SDK to [4.14.0](https://docs.sentiance.com/sdk/changelog/android#4-14-0-31-jan-2020)
- Upgrade iOS SDK to [5.6.0](https://docs.sentiance.com/sdk/changelog/ios#5-6-0-5-feb-2020)

## [3.0.8] - 2020-01-16

### Changed

- Upgrade Android SDK to [4.13.0](https://docs.sentiance.com/sdk/changelog/android#4-13-0-6-jan-2020)

## [3.0.7] - 2019-11-20

### Added

- The ability to update SDK foreground notification. This is only applicable to Android.
- Send `SDKUserLink` event during initialization if user linking is enabled
- Support subscriptions for user activity updates
- Support the auto-start option when initializing the Sentiance SDK

### Changed

- Upgrade iOS SDK to [5.5.5](https://docs.sentiance.com/sdk/changelog/ios#5-5-5-13-nov-2019)

## [3.0.0] - 2019-08-01

### Added

- `listenUserActivity` method
- `initWithUserLinking` to expose the user linking [feature](https://docs.sentiance.com/guide/user-linking)
- `initWithBaseURL` to initialize the SDK against another regional [API](https://docs.sentiance.com/sdk/api-reference/ios/sentconfig-1#sentconfig-api)
- `getInitState` to retreive the init state of the SDK [link](https://docs.sentiance.com/sdk/api-reference/ios/sentsdk#getinitstate)
- Start maintaining a CHANGELOG.md

### Changed

- Updated `init` function with baseURL argument

### Removed

- Removed `isInitialized` bridge function in favor of `getInitState` [link](https://docs.sentiance.com/sdk/api-reference/ios/sentsdk#isinitialised)

## [2.0.0 - 2.2.1]

### Added

- Update SDK to latest

## [1.0.0]

### Added

- Bridge necessary SDK methods for both iOS and Android
