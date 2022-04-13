import userContext from './user-context';

const getUserContext = () => userContext.getUserContext();
const listenUserContextUpdates = () => userContext.listenUserContextUpdates();
const addUserContextUpdateListener = userContext._addUserContextUpdateListener;

module.exports = {
  getUserContext,
  listenUserContextUpdates,
  addUserContextUpdateListener
};
