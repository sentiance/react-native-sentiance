const { NativeModules, NativeEventEmitter } = require('react-native');

const { RNSentiance } = NativeModules;

const SENTIANCE_EMITTER = new NativeEventEmitter(RNSentiance);
const SENTINACE_STORE_KEYS = [
  "SENTIANCE_SDK_APP_ID", 
  "SENTIANCE_SDK_APP_SECRET", 
  "SENTIANCE_SDK_APP_BASE_URL", 
  "SENTIANCE_SDK_IS_READY_FOR_BACKGROUND"
]

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



RNSentiance.createUser = async (configuration) => {
  const {credentials, linker} = configuration;

  SENTINACE_STORE_KEYS.forEach(async key => {
    console.log(key, await RNSentiance.getValueForKey(key, ""));
  })
  RNSentiance.setValueForKey("SENTIANCE_SDK_IS_READY_FOR_BACKGROUND", "");
  console.log({credentials, linker})
 
  console.log("[bridge setting credentials]")
  const baseUrl = credentials.baseUrl ?? null
  const { appId, appSecret } = credentials;

  RNSentiance.setValueForKey("SENTIANCE_SDK_APP_ID", appId);
  RNSentiance.setValueForKey("SENTIANCE_SDK_APP_SECRET", appSecret);
  RNSentiance.setValueForKey("SENTIANCE_SDK_APP_BASE_URL", baseUrl);

  if(! linker) {
    await RNSentiance.init(credentials.appId, credentials.appSecret, baseUrl, false)
    await RNSentiance.setValueForKey("SENTIANCE_SDK_IS_READY_FOR_BACKGROUND", "YES")
    return Promise.resolve();
  }
  
  return new Promise(async (resolve, _reject) => {
    SENTIANCE_EMITTER.addListener('SDKUserLink', (data) => {
      console.log("[helper]", { data})
      linker(data, () => {
        console.log("[helper] ready to link success")
        RNSentiance.userLinkCallback(true)
        RNSentiance.setValueForKey("SENTIANCE_SDK_IS_READY_FOR_BACKGROUND", "YES")
      }, () => {
        RNSentiance.userLinkCallback(false)
      });
    })
    
    await RNSentiance.initWithUserLinkingEnabled(appId,appSecret,baseUrl,false)
    resolve();
  })
  
}

RNSentiance.clear = () => {
  SENTINACE_STORE_KEYS.forEach(async key => {
    console.log(key, await RNSentiance.getValueForKey(key, ""));
    RNSentiance.setValueForKey(key, "");
  })
  RNSentiance.reset();
}

module.exports = RNSentiance;
