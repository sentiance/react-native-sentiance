const SentianceEventEmitter = require("@sentiance-react-native/core/lib/SentianceEventEmitter");

exports.createEventListener = async (eventName: String, emitter: SentianceEventEmitter, callback: Function) => {
  const listener = (data) => {
    callback(data);
  };
  const subscription = await emitter.addListener(eventName, listener);
  subscription.remove = async function () {
    await emitter.clearSubscription(eventName, subscription);
  }
  return subscription;
};