const userContext = require('./user-context');

const requestUserContext = () => userContext.requestUserContext();
const addUserContextUpdateListener = userContext._addUserContextUpdateListener;

module.exports = {
  requestUserContext,
  addUserContextUpdateListener
};
