const {NativeModules} = require("react-native");

const {SentianceCrashDetection} = NativeModules;

/**
 * This is a description for listenVehicleCrashEvents.
 * @returns {Promise<boolean>}
 */
const listenVehicleCrashEvents = () => SentianceCrashDetection.listenVehicleCrashEvents();
const invokeDummyVehicleCrash = () => SentianceCrashDetection.invokeDummyVehicleCrash();
const isVehicleCrashDetectionSupported = () => SentianceCrashDetection.isVehicleCrashDetectionSupported();

module.exports = {
  listenVehicleCrashEvents,
  invokeDummyVehicleCrash,
  isVehicleCrashDetectionSupported
};
