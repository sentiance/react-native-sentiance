"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
/**
 * Singleton to generate identifiers for callbacks. These identifiers are provided to
 * our native modules upon registering/unregistering a callback.
 */
class SubscriptionIdGenerator {
    constructor() {
        this.id = 0;
        // Prevent external instantiation
    }
    static getInstance() {
        if (!SubscriptionIdGenerator.instance) {
            SubscriptionIdGenerator.instance = new SubscriptionIdGenerator();
        }
        return SubscriptionIdGenerator.instance;
    }
    next() {
        return ++this.id;
    }
}
exports.default = SubscriptionIdGenerator.getInstance();
