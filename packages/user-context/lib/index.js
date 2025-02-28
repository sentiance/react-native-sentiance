const userContext = require("./user-context");

const requestUserContext = (includeProvisionalEvents) => {
  // See: https://github.com/facebook/react-native/issues/24250
  return userContext.requestUserContext(!!includeProvisionalEvents);
};
const addUserContextUpdateListener = (listener, includeProvisionalEvents) =>
  userContext._addUserContextUpdateListener(listener, !!includeProvisionalEvents);

module.exports = {
  requestUserContext,
  addUserContextUpdateListener
};
module.exports.events = {
  USER_CONTEXT_UPDATE_EVENT: userContext.events.USER_CONTEXT_UPDATE_EVENT
};
