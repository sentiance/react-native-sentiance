# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [3.0.0] - 2019-08-01
### Added
- Added `initWithUserLinking` to expose the user linking [feature](https://docs.sentiance.com/guide/user-linking)
- Added `initWithBaseURL` to initialize the SDK against another regional [API](https://docs.sentiance.com/sdk/api-reference/ios/sentconfig-1#sentconfig-api)
- Added `getInitState` to retreive the init state of the SDK [link](https://docs.sentiance.com/sdk/api-reference/ios/sentsdk#getinitstate)
- Start maintaining a CHANGELOG.md

### Changed
- `init` function takes nullable baseURL argument;

### Removed
- removed `isInitialized` bridge function in favor of `getInitState` [link](https://docs.sentiance.com/sdk/api-reference/ios/sentsdk#isinitialised)

## [2.0.0 - 2.2.1]
### Added
- Update SDK to latest

## [1.0.0]
### Added
- Bridge necessary SDK methods for both iOS and Android
