const drivingInsights = require('./driving-insights');

const getDrivingInsights = (transportId) => drivingInsights.getDrivingInsights(transportId);
const getHarshDrivingEvents = (transportId) => drivingInsights.getHarshDrivingEvents(transportId);
const getPhoneUsageEvents = (transportId) => drivingInsights.getPhoneUsageEvents(transportId);
const addDrivingInsightsReadyListener = drivingInsights._addDrivingInsightsReadyListener;
const events = drivingInsights.events;

module.exports = {
  getDrivingInsights,
  getHarshDrivingEvents,
  getPhoneUsageEvents,
  addDrivingInsightsReadyListener,
  events
};
