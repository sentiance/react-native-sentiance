const {NativeModules} = require("react-native");

const {SentianceUserContext} = NativeModules;

const getUserContext = () => SentianceUserContext.getUserContext();
const listenUserContextUpdates = () => SentianceUserContext.listenUserContextUpdates();

module.exports = {
  getUserContext,
  listenUserContextUpdates
};
