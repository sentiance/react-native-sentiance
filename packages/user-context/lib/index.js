import userContext from './user-context';

const getUserContext = () => userContext.getUserContext();
const listenUserContextUpdates = () => userContext.listenUserContextUpdates();

module.exports = {
  getUserContext,
  listenUserContextUpdates
};
