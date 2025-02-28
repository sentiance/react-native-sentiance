"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const react_native_1 = require("react-native");
const defaultNativeIosModuleName = "SentianceCore";
function default_1(specs) {
    const { androidName, iosName, isModuleVerified } = specs;
    const moduleName = react_native_1.Platform.OS === "android" ? androidName : iosName !== null && iosName !== void 0 ? iosName : defaultNativeIosModuleName;
    const nativeModule = react_native_1.NativeModules[moduleName];
    if (!nativeModule) {
        console.error(`Could not locate the native ${moduleName} module.
      Make sure that your native code is properly linked, and that the module name you specified is correct.`);
        return undefined;
    }
    if (isModuleVerified(nativeModule)) {
        return nativeModule;
    }
    else {
        console.error(`The ${moduleName} module is missing 1 or more required bindings.
      Make sure that your native code is correctly exporting all expected bindings.`);
        return undefined;
    }
}
exports.default = default_1;
