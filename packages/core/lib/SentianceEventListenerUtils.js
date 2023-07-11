const SentianceEventEmitter = require("@sentiance-react-native/core/lib/SentianceEventEmitter");

exports.createEventListener = async (eventName: String, emitter: SentianceEventEmitter, callback: Function) => {
  const listener = (data) => {
    callback(data);
  };
  const subscription = await emitter.addListener(eventName, listener);
  return {
    key: subscription.key,
    eventName,
    remove: () => emitter.clearSubscription(eventName, subscription)
  };
};
