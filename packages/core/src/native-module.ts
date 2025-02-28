export interface NativeModule {
  // Required by RN
  addListener: (eventType: string) => void;
  removeListeners: (count: number) => void;

  // Required by us
  addNativeListener: (
    eventName: string,
    subscriptionKey: number,
    payload?: Map<string, any>
  ) => Promise<void>;
  removeNativeListener: (
    eventName: string,
    subscriptionKey: number
  ) => Promise<void>;
}

export function isValidNativeModule(
  obj: Partial<NativeModule>
): obj is NativeModule {
  return (
    typeof obj.addListener === "function" &&
    typeof obj.removeListeners === "function" &&
    typeof obj.addNativeListener === "function" &&
    typeof obj.removeNativeListener === "function"
  );
}
