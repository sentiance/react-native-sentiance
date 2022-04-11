import crashDetection from './crash-detection';

const listenVehicleCrashEvents = () => crashDetection.listenVehicleCrashEvents();
const invokeDummyVehicleCrash = () => crashDetection.invokeDummyVehicleCrash();
const isVehicleCrashDetectionSupported = () => crashDetection.isVehicleCrashDetectionSupported();

module.exports = {
  listenVehicleCrashEvents,
  invokeDummyVehicleCrash,
  isVehicleCrashDetectionSupported
};
