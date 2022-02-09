const { NativeModules } = require('react-native');

const { RNSentiance } = NativeModules;

RNSentiance.TransportMode = {};
(function (TransportMode) {
  TransportMode[TransportMode["UNKNOWN"] = 1] = "UNKNOWN";
  TransportMode[TransportMode["CAR"] = 2] = "CAR";
  TransportMode[TransportMode["BICYCLE"] = 3] = "BICYCLE";
  TransportMode[TransportMode["ON_FOOT"] = 4] = "ON_FOOT";
  TransportMode[TransportMode["TRAIN"] = 5] = "TRAIN";
  TransportMode[TransportMode["TRAM"] = 6] = "TRAM";
  TransportMode[TransportMode["BUS"] = 7] = "BUS";
  TransportMode[TransportMode["PLANE"] = 8] = "PLANE";
  TransportMode[TransportMode["BOAT"] = 9] = "BOAT";
  TransportMode[TransportMode["METRO"] = 10] = "METRO";
  TransportMode[TransportMode["RUNNING"] = 11] = "RUNNING";
})(RNSentiance.TransportMode);

RNSentiance.createUser = (credentials, linker) => {
  console.log({credentials, linker})

  console.log("[bridge setting credentials]")
  RNSentiance.setValueForKey("SENTIANCE_SDK_APP_ID", credentials.appId);
  RNSentiance.setValueForKey("SENTIANCE_SDK_APP_SECRET", credentials.appSecret);
  RNSentiance.setValueForKey("SENTIANCE_SDK_APP_BASE_URL", credentials.baseUrl ?? null);

}

module.exports = RNSentiance;
