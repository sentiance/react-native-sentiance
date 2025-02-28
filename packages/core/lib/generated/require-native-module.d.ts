import { type NativeModule } from "./native-module";
export default function <T extends NativeModule>(specs: {
    androidName: string;
    isModuleVerified: (obj: Partial<T>) => obj is T;
    iosName?: string;
}): T | undefined;
