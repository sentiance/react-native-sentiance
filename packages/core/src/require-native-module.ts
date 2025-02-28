import { NativeModules, Platform } from "react-native";
import { type NativeModule } from "./native-module";

const defaultNativeIosModuleName = "SentianceCore";

export default function <T extends NativeModule>(
  specs: {
    androidName: string,
    isModuleVerified: (obj: Partial<T>) => obj is T,
    iosName?: string
  }
): T | undefined {
  const { androidName, iosName, isModuleVerified } = specs;
  const moduleName = Platform.OS === "android" ? androidName : iosName ?? defaultNativeIosModuleName;
  const nativeModule = NativeModules[moduleName] as Partial<T> | undefined;

  if (!nativeModule) {
    console.error(`Could not locate the native ${moduleName} module.
      Make sure that your native code is properly linked, and that the module name you specified is correct.`);
    return undefined;
  }

  if (isModuleVerified(nativeModule)) {
    return nativeModule;
  } else {
    console.error(`The ${moduleName} module is missing 1 or more required bindings.
      Make sure that your native code is correctly exporting all expected bindings.`);
    return undefined;
  }
}
