const drivingInsights = require('./driving-insights');

const getDrivingInsights = (transportId) => drivingInsights.getDrivingInsights(transportId);
const getHarshDrivingEvents = (transportId) => drivingInsights.getHarshDrivingEvents(transportId);
const getPhoneUsageEvents = (transportId) => drivingInsights.getPhoneUsageEvents(transportId);
const getCallWhileMovingEvents = (transportId) => drivingInsights.getCallWhileMovingEvents(transportId);
const getSpeedingEvents = (transportId) => drivingInsights.getSpeedingEvents(transportId);
const addDrivingInsightsReadyListener = drivingInsights._addDrivingInsightsReadyListener;
const events = drivingInsights.events;

module.exports = {
  getDrivingInsights,
  getHarshDrivingEvents,
  getPhoneUsageEvents,
  getCallWhileMovingEvents,
  getSpeedingEvents,
  addDrivingInsightsReadyListener,
  events
};
