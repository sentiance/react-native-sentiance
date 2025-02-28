export interface NativeModule {
    addListener: (eventType: string) => void;
    removeListeners: (count: number) => void;
    addNativeListener: (eventName: string, subscriptionKey: number, payload?: Map<string, any>) => Promise<void>;
    removeNativeListener: (eventName: string, subscriptionKey: number) => Promise<void>;
}
export declare function isValidNativeModule(obj: Partial<NativeModule>): obj is NativeModule;
