import userContext from './user-context';

const requestUserContext = () => userContext.requestUserContext();
const listenUserContextUpdates = () => userContext.listenUserContextUpdates();
const addUserContextUpdateListener = userContext._addUserContextUpdateListener;

module.exports = {
  requestUserContext,
  listenUserContextUpdates,
  addUserContextUpdateListener
};
