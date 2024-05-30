# Creating new SDK modules

Start by running `npm i` to download and install the project's dependencies.

Then, run the script located at `scripts/new-sdk-module/run.sh` from the root of the project and follow the steps.

## iOS specific configuration

### Project
Use existing module as reference and update the following.

- Module name via Xcode.
- Podspec file.
- Classes name for: native module, converter, error codes.

### Native Module
In your native module class `NewModule.m` add these methods to prevent react-native runtime warnings.
```obj-c  
@property (assign) BOOL hasListeners;

// Will be called when this module's first listener is added.
- (void)startObserving {
    self.hasListeners = YES;
    // Set up any upstream listeners or background tasks as necessary
}

// Will be called when this module's last listener is removed, or on dealloc.
- (void)stopObserving {
    self.hasListeners = NO;
    // Remove upstream listeners, stop unnecessary background tasks
}
```

