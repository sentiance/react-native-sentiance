# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [3.1.0] - 2020-02-06
- Upgrade Android SDK to [4.14.0](https://docs.sentiance.com/sdk/changelog/android#4-14-0-31-jan-2020)
- Upgrade iOS SDK to [5.6.0](https://docs.sentiance.com/sdk/changelog/ios#5-6-0-5-feb-2020)
- Added `reset` method to reset the Sentiance SDK

## [3.0.8] - 2020-01-16
- Upgrade Android SDK to [4.13.0](https://docs.sentiance.com/sdk/changelog/android#4-13-0-6-jan-2020)

## [3.0.7] - 2019-11-20
- Upgrade iOS SDK to [5.5.5](https://docs.sentiance.com/sdk/changelog/ios#5-5-5-13-nov-2019)
- Support the auto-start option when initializing the Sentiance SDK
- Send `SendUserLink` event during initialization if user linking is enabled (Android Only)
- Support subscriptions for user activity updates
- Added the ability to update SDK foreground notification

## [3.0.0] - 2019-08-01
### Added
- Added `listenUserActivity` method
- Added `initWithUserLinking` to expose the user linking [feature](https://docs.sentiance.com/guide/user-linking)
- Added `initWithBaseURL` to initialize the SDK against another regional [API](https://docs.sentiance.com/sdk/api-reference/ios/sentconfig-1#sentconfig-api)
- Added `getInitState` to retreive the init state of the SDK [link](https://docs.sentiance.com/sdk/api-reference/ios/sentsdk#getinitstate)
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
