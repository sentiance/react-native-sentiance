import { type EmitterSubscription, NativeEventEmitter } from "react-native";
import { type NativeModule } from "./native-module";
export default class SentianceEventEmitter<NM extends NativeModule> {
    protected nativeModule: NM;
    protected reactNativeEventEmitter: NativeEventEmitter;
    constructor(nativeModule: NM);
    /**
     * Registers a new listener with the React Native framework, in addition to our own
     * native modules via the `addNativeListener` binding.
     *
     * @param eventName the name of the event for which you are registering a listener
     * @param listener the listener to be registered
     * @param context additional context info related to the listener
     * @returns a promise that resolves to a subscription object which provides a `remove()` function,
     *          allowing consumers to remove the associated listener.
     */
    addListener<T>(eventName: string, listener: (...args: T[]) => any, context?: any): Promise<EmitterSubscription>;
}
