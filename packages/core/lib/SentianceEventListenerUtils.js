exports.createEventListener = async (eventName, emitter, callback) => {
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
