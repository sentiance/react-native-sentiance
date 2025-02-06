const drivingInsights = require("./driving-insights");
import avgOverallSafetyScoreApi from "./avg-overall-safety-score-api";

const getDrivingInsights = (transportId) => drivingInsights.getDrivingInsights(transportId);
const getHarshDrivingEvents = (transportId) => drivingInsights.getHarshDrivingEvents(transportId);
const getPhoneUsageEvents = (transportId) => drivingInsights.getPhoneUsageEvents(transportId);
const getCallWhileMovingEvents = (transportId) => drivingInsights.getCallWhileMovingEvents(transportId);
const getSpeedingEvents = (transportId) => drivingInsights.getSpeedingEvents(transportId);
const addDrivingInsightsReadyListener = drivingInsights._addDrivingInsightsReadyListener;
const events = drivingInsights.events;

const getAverageOverallSafetyScore = (params) => avgOverallSafetyScoreApi(drivingInsights, params);

module.exports = {
  getDrivingInsights,
  getHarshDrivingEvents,
  getPhoneUsageEvents,
  getCallWhileMovingEvents,
  getSpeedingEvents,
  addDrivingInsightsReadyListener,
  getAverageOverallSafetyScore,
  events
};
