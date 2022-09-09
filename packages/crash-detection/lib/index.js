const crashDetection = require('./crash-detection');

const listenVehicleCrashEvents = () => crashDetection.listenVehicleCrashEvents();
const invokeDummyVehicleCrash = () => crashDetection.invokeDummyVehicleCrash();
const isVehicleCrashDetectionSupported = () => crashDetection.isVehicleCrashDetectionSupported();
const addVehicleCrashEventListener = crashDetection._addVehicleCrashEventListener;
const addVehicleCrashDiagnosticListener = crashDetection._addVehicleCrashDiagnosticListener;

module.exports = {
  listenVehicleCrashEvents,
  invokeDummyVehicleCrash,
  isVehicleCrashDetectionSupported,
  addVehicleCrashEventListener,
  addVehicleCrashDiagnosticListener,
};
