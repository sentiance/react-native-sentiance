"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const react_native_1 = require("react-native");
const subscription_id_gen_1 = __importDefault(require("./subscription-id-gen"));
class SentianceEventEmitter {
    constructor(nativeModule) {
        this.nativeModule = nativeModule;
        this.reactNativeEventEmitter = new react_native_1.NativeEventEmitter(nativeModule);
    }
    /**
     * Registers a new listener with the React Native framework, in addition to our own
     * native modules via the `addNativeListener` binding.
     *
     * @param eventName the name of the event for which you are registering a listener
     * @param listener the listener to be registered
     * @param context additional context info related to the listener
     * @returns a promise that resolves to a subscription object which provides a `remove()` function,
     *          allowing consumers to remove the associated listener.
     */
    addListener(eventName, listener, context) {
        return __awaiter(this, void 0, void 0, function* () {
            // Register a new subscription with the React Native framework
            const subscription = this.reactNativeEventEmitter.addListener(eventName, listener, context);
            const rnRemove = subscription.remove.bind(subscription);
            // Upon removing a subscription, we want to notify the RN framework in addition to our own native modules.
            // so we upgrade the behavior of the subscriptions' remove() function
            subscription.key = subscription_id_gen_1.default.next();
            subscription.eventType = eventName;
            subscription.remove = () => {
                rnRemove();
                this.nativeModule.removeNativeListener(eventName, subscription.key);
            };
            yield this.nativeModule.addNativeListener(eventName, subscription.key, context);
            return subscription;
        });
    }
}
exports.default = SentianceEventEmitter;
