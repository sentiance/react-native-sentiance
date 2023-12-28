package com.sentiance.react.bridge.core.common;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sentiance.react.bridge.core.common.util.SingleParamRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("rawtypes")
public class SentianceSubscriptionsManager {

  private final List<Subscription> mSubscriptions;
  private final Map<String, SubscriptionDefinition> mSupportedSubscriptions;

  public SentianceSubscriptionsManager() {
    mSubscriptions = new ArrayList<>();
    mSupportedSubscriptions = new HashMap<>();
  }

  public <T> void addSupportedSubscription(String eventType, SingleParamRunnable<T> nativeSubscribeLogic,
                                           SingleParamRunnable<T> nativeUnsubscribeLogic, SubscriptionType subscriptionType) {
    if (mSupportedSubscriptions.containsKey(eventType)) {
      throw new IllegalArgumentException(String.format("A subscription definition for %s has already been added.",
        eventType));
    }

    mSupportedSubscriptions.put(eventType, new SubscriptionDefinition<>(eventType, nativeSubscribeLogic,
      nativeUnsubscribeLogic, subscriptionType));
  }

  public <T> void addSubscription(@NonNull String eventType, int subscriptionId, @NonNull T eventEmitterLogic) {
    SubscriptionDefinition definition = mSupportedSubscriptions.get(eventType);
    if (definition == null) {
      return;
    }

    if (shouldSubscribeNatively(definition)) {
      //noinspection unchecked
      definition.nativeSubscribeLogic.run(eventEmitterLogic);
    }

    Subscription<T> subscription = new Subscription<>(subscriptionId, eventEmitterLogic, eventType);

    synchronized (mSubscriptions) {
      mSubscriptions.add(subscription);
    }
  }

  public void removeSubscription(int subscriptionId, String eventType) {
    SubscriptionDefinition definition = mSupportedSubscriptions.get(eventType);
    if (definition == null) {
      return;
    }

    Subscription subscription = getExistingSubscription(subscriptionId, eventType);
    if (subscription != null) {
      synchronized (mSubscriptions) {
        mSubscriptions.remove(subscription);
      }

      if (shouldUnsubscribeNatively(definition)) {
        //noinspection unchecked
        definition.nativeUnsubscribeLogic.run(subscription.eventEmitterLogic);
      }
    }
  }

  @Nullable
  private Subscription getExistingSubscription(int subscriptionId, String eventType) {
    synchronized (mSubscriptions) {
      for (Subscription subscription : mSubscriptions) {
        if (subscription.id == subscriptionId && subscription.eventType.equals(eventType)) {
          return subscription;
        }
      }
      return null;
    }
  }

  private boolean shouldSubscribeNatively(SubscriptionDefinition definition) {
    return shouldInvokeNativeLogic(definition);
  }

  private boolean shouldUnsubscribeNatively(SubscriptionDefinition definition) {
    return shouldInvokeNativeLogic(definition);
  }

  private boolean shouldInvokeNativeLogic(SubscriptionDefinition definition) {
    if (definition.subscriptionType == SubscriptionType.MULTIPLE) {
      return true;
    }

    return definition.subscriptionType == SubscriptionType.SINGLE && !subscriptionsExists(definition.eventType);
  }

  private boolean subscriptionsExists(String eventType) {
    synchronized (mSubscriptions) {
      for (Subscription sub : mSubscriptions) {
        if (sub.eventType.equals(eventType)) {
          return true;
        }
      }
      return false;
    }
  }

  private static class Subscription<T> {
    private final int id;
    @Nullable
    private final T eventEmitterLogic;
    private final String eventType;

    public Subscription(int id, @Nullable T eventEmmitterLogic, String eventType) {
      this.id = id;
      this.eventEmitterLogic = eventEmmitterLogic;
      this.eventType = eventType;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Subscription<?> that = (Subscription<?>) o;
      return id == that.id && Objects.equals(eventEmitterLogic, that.eventEmitterLogic) && Objects.equals(eventType, that.eventType);
    }

    @Override
    public int hashCode() {
      return Objects.hash(id, eventEmitterLogic, eventType);
    }

    @Override
    public String toString() {
      return "Subscription{" +
        "id=" + id +
        ", eventType='" + eventType + '\'' +
        '}';
    }
  }

  /**
   * Outlines how the native code should react to incoming requests in order to subscribe to/unsubscribe from
   * global events from the JS code.
   * <br/>
   * <p>
   * It defines how to add a listener on the Sentiance SDK to get updates related to the provided <code>eventType</code>,
   * and how to remove the said listener.
   * </p>
   *
   * @param <T> The type of the Sentiance SDK listener to be added/removed.
   */
  private static class SubscriptionDefinition<T> {
    private final String eventType;
    /**
     * The subscription logic to run towards the native SDK.
     */
    private final SingleParamRunnable<T> nativeSubscribeLogic;
    /**
     * The unsubscription logic to run towards the native SDK.
     */
    private final SingleParamRunnable<T> nativeUnsubscribeLogic;

    private final SubscriptionType subscriptionType;

    public SubscriptionDefinition(String eventType, SingleParamRunnable<T> nativeSubscribeLogic,
                                  SingleParamRunnable<T> nativeUnsubscribeLogic, SubscriptionType subscriptionType) {
      this.eventType = eventType;
      this.nativeSubscribeLogic = nativeSubscribeLogic;
      this.nativeUnsubscribeLogic = nativeUnsubscribeLogic;
      this.subscriptionType = subscriptionType;
    }

    @Override
    public String toString() {
      return "SubscriptionDefinition{" +
        "eventType='" + eventType + '\'' +
        ", subscriptionType=" + subscriptionType +
        '}';
    }
  }

  /**
   * Indicates whether the Sentiance SDK supports setting multiple listeners for a certain event type,
   * or just a single listener.
   *
   * <pre>
   *   {@code
   *     someSentianceApi.setSomeEventListener(); // SINGLE
   *     someSentianceApi.addSomeEventListener(); // MULTIPLE
   *   }
   * </pre>
   */
  public enum SubscriptionType {
    SINGLE,
    MULTIPLE
  }
}
