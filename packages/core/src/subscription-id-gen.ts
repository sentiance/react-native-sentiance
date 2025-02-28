/**
 * Singleton to generate identifiers for callbacks. These identifiers are provided to
 * our native modules upon registering/unregistering a callback.
 */
class SubscriptionIdGenerator {
  private static instance: SubscriptionIdGenerator;
  private id = 0;

  private constructor() {
    // Prevent external instantiation
  }

  static getInstance(): SubscriptionIdGenerator {
    if (!SubscriptionIdGenerator.instance) {
      SubscriptionIdGenerator.instance = new SubscriptionIdGenerator();
    }
    return SubscriptionIdGenerator.instance;
  }

  next(): number {
    return ++this.id;
  }
}

export default SubscriptionIdGenerator.getInstance();
