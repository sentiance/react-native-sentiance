/**
 * Singleton to generate identifiers for callbacks. These identifiers are provided to
 * our native modules upon registering/unregistering a callback.
 */
declare class SubscriptionIdGenerator {
    private static instance;
    private id;
    private constructor();
    static getInstance(): SubscriptionIdGenerator;
    next(): number;
}
declare const _default: SubscriptionIdGenerator;
export default _default;
