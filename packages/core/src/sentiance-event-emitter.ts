import { type EmitterSubscription, NativeEventEmitter } from "react-native";
import { type NativeModule } from "./native-module";
import subscriptionIdGenerator from "./subscription-id-gen";

export default class SentianceEventEmitter {

  protected reactNativeEventEmitter: NativeEventEmitter;

  constructor(protected nativeModule: NativeModule) {
    this.reactNativeEventEmitter = new NativeEventEmitter(nativeModule);
  }

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
  async addListener<T>(eventName: string, listener: (...args: T[]) => any, context?: any): Promise<EmitterSubscription> {
    // Register a new subscription with the React Native framework
    const subscription = this.reactNativeEventEmitter.addListener(eventName, listener, context);
    const rnRemove = subscription.remove.bind(subscription);
    // Upon removing a subscription, we want to notify the RN framework in addition to our own native modules.
    // so we upgrade the behavior of the subscriptions' remove() function
    subscription.key = subscriptionIdGenerator.next();
    subscription.eventType = eventName;
    subscription.remove = () => {
      rnRemove();
      this.nativeModule.removeNativeListener(eventName, subscription.key);
    };
    await this.nativeModule.addNativeListener(eventName, subscription.key, context);
    return subscription;
  }
}
