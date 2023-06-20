import {EmitterSubscription, NativeEventEmitter, Platform} from "react-native";

class SentianceEventEmitter extends NativeEventEmitter {

  constructor(nativeModule) {
    super(nativeModule);

    const bindings = this.requireNativeBindings(nativeModule);
    this._addNativeListener = bindings.addNativeListener;
    this._removeNativeListener = bindings.removeNativeListener;
  }

  requireNativeBindings(nativeModule) {
    if (!nativeModule) {
      throw new Error('Native module cannot have a null value.');
    }

    const hasAddListener = typeof nativeModule.addNativeListener === 'function';
    const hasRemoveListener = typeof nativeModule.removeNativeListener === 'function';

    if (!hasAddListener) {
      throw new Error('Native module does not expose an addNativeListener function');
    }
    if (!hasRemoveListener) {
      throw new Error('Native module does not expose a removeNativeListener function');
    }

    return {
      addNativeListener: nativeModule.addNativeListener,
      removeNativeListener: nativeModule.removeNativeListener
    };
  }

  async addListener(eventType, listener, context): EmitterSubscription {
    const subscription = super.addListener(eventType, listener, context);
    await this._addNativeListener(eventType, subscription.key);
    return subscription;
  }

  async clearSubscription(eventType, subscription) {
    if (super.removeSubscription != null) {
      super.removeSubscription(subscription);
    } else {
      // RN 0.64+ no longer provides a removeSubscription function
      subscription.remove();
    }
    await this._removeNativeListener(eventType, subscription.key);
  }
}

module.exports = SentianceEventEmitter;
