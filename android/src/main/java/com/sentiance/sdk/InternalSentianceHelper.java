package com.sentiance.sdk;

import android.content.Context;

/**
 * This is an internal helper class, it is not intended to be used externally.
 */
public class InternalSentianceHelper {
  // hide constructor
  private InternalSentianceHelper() {}

  public static boolean isThirdPartyLinked(Context context) {
    return Sentiance.getInstance(context).isThirdPartyLinked();
  }
}
