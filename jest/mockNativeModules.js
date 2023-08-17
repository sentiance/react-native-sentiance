export function mockNativeModule(platform, nativeModule, module) {
  const mockAddListener = jest.fn();
  const mockRemoveListeners = jest.fn();
  const mockAddNativeListener = jest.fn();
  const mockRemoveNativeListener = jest.fn();
  const activeModuleName = platform === 'android' ? nativeModule.androidName : nativeModule.iosName;

  jest.doMock('react-native', () => {
    const RN = jest.requireActual('react-native');
    RN.Platform.OS = platform;

    module = {
      ...module,
      addNativeListener: mockAddNativeListener,
      removeNativeListener: mockRemoveNativeListener,
      addListener: mockAddListener,
      removeListeners: mockRemoveListeners
    }

    RN.NativeModules[activeModuleName] = module;
    return RN;
  });

  return {
    name: activeModuleName,
    addListener: mockAddListener,
    removeListeners: mockRemoveListeners,
    addNativeListener: mockAddNativeListener,
    removeNativeListener: mockRemoveNativeListener
  };
}
