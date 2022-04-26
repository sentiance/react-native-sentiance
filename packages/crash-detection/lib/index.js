import crashDetection from './crash-detection';

const listenVehicleCrashEvents = () => crashDetection.listenVehicleCrashEvents();
const invokeDummyVehicleCrash = () => crashDetection.invokeDummyVehicleCrash();
const isVehicleCrashDetectionSupported = () => crashDetection.isVehicleCrashDetectionSupported();
const addVehicleCrashEventListener = () => crashDetection._addVehicleCrashEventListener();

module.exports = {
  listenVehicleCrashEvents,
  invokeDummyVehicleCrash,
  isVehicleCrashDetectionSupported,
  addVehicleCrashEventListener
};
