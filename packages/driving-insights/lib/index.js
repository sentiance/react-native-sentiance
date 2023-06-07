const drivingInsights = require('./driving-insights');

const getDrivingInsights = (transportId: String) => drivingInsights.getDrivingInsights(transportId);
const getHarshDrivingEvents = (transportId: String) => drivingInsights.getHarshDrivingEvents(transportId);
const getPhoneUsageEvents = (transportId: String) => drivingInsights.getPhoneUsageEvents(transportId);
const addDrivingInsightsReadyListener = drivingInsights._addDrivingInsightsReadyListener;

module.exports = {
  getDrivingInsights,
  getHarshDrivingEvents,
  getPhoneUsageEvents,
  addDrivingInsightsReadyListener
};
