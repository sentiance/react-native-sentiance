import {EmitterSubscription, NativeEventEmitter, Platform} from "react-native";

class SentianceEventEmitter extends NativeEventEmitter {

  constructor(nativeModule) {
    super(nativeModule);

    const bindings = this.requireNativeBindings(nativeModule);
    this._addNativeListener = bindings.addNativeListener;
    this._removeNativeListener = bindings.removeNativeListener;
    this._subscriptionsMap = {};
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
    if (!this._subscriptionsMap[eventType]) {
      this._subscriptionsMap[eventType] = [];
    }
    const subscriptionsForType = this._subscriptionsMap[eventType];
    const key = subscriptionsForType.length;
    const subscription = super.addListener(eventType, listener, context);
    subscription.key = key;
    subscriptionsForType.push(subscription);
    await this._addNativeListener(eventType, key);
    return subscription;
  }

  async clearSubscription(eventType, subscription) {
    const key = subscription.key;
    const subscriptionsForType = this._subscriptionsMap[eventType];
    if (subscriptionsForType) {
      const targetSubIndex = subscriptionsForType.findIndex(sub => sub.key === key);
      if (targetSubIndex !== -1) {
        subscriptionsForType.splice(targetSubIndex, 1);
      }
    }
    if (super.removeSubscription != null) {
      super.removeSubscription(subscription);
    } else {
      // RN 0.65+ no longer provides a removeSubscription function
      subscription.remove();
    }
    await this._removeNativeListener(eventType, subscription.key);
  }
}

module.exports = SentianceEventEmitter;
