const {NativeModules, Platform} = require("react-native");
const {varToString} = require("@sentiance-react-native/core/lib/utils");
const SentianceEventEmitter = require("@sentiance-react-native/core/lib/SentianceEventEmitter");
const {createEventListener} = require("@sentiance-react-native/core/lib/SentianceEventListenerUtils");
const {SentianceSmartGeofences, SentianceCore} = NativeModules;

const SMART_GEOFENCE_EVENT = "SENTIANCE_SMART_GEOFENCE_EVENT";

let didLocateNativeModule = true;
let smartGeofencesModule = {};
if (Platform.OS === 'android') {
  if (!SentianceSmartGeofences) {
    didLocateNativeModule = false;
    const nativeModuleName = varToString({SentianceSmartGeofences});
    console.error(`Could not locate the native ${nativeModuleName} module.
    Make sure that your native code is properly linked, and that the module name you specified is correct.`);
  } else {
    smartGeofencesModule = SentianceSmartGeofences;
  }
} else {
  if (!SentianceCore) {
    didLocateNativeModule = false;
    const nativeModuleName = varToString({SentianceCore});
    console.error(`Could not locate the native ${nativeModuleName} module.
    Make sure that your native code is properly linked, and that the module name you specified is correct.`);
  } else {
    smartGeofencesModule = SentianceCore;
  }
}

if (didLocateNativeModule) {
  const emitter = new SentianceEventEmitter(smartGeofencesModule);

  smartGeofencesModule._addSmartGeofenceEventListener = async (onSmartGeofenceEvent) => {
    return createEventListener(SMART_GEOFENCE_EVENT, emitter, onSmartGeofenceEvent);
  };
}

const refreshGeofences = () => smartGeofencesModule.refreshGeofences();

const getDetectionMode = () => smartGeofencesModule.getDetectionMode();

const addSmartGeofenceEventListener = smartGeofencesModule._addSmartGeofenceEventListener;

module.exports = {
  refreshGeofences,
  getDetectionMode,
  addSmartGeofenceEventListener
};
module.exports.events = {
  SMART_GEOFENCE_EVENT
};
