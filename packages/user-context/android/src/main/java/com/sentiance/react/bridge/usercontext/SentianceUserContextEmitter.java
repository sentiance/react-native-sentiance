package com.sentiance.react.bridge.usercontext;

import static com.sentiance.react.bridge.usercontext.SentianceUserContextConverter.convertCriteriaList;
import static com.sentiance.react.bridge.usercontext.SentianceUserContextConverter.convertUserContext;

import android.content.Context;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.sentiance.react.bridge.core.base.AbstractSentianceEmitter;
import com.sentiance.sdk.usercontext.api.UserContext;
import com.sentiance.sdk.usercontext.api.UserContextUpdateCriteria;

import java.util.List;

class SentianceUserContextEmitter extends AbstractSentianceEmitter {

  private static final String USER_CONTEXT_EVENT = "SENTIANCE_USER_CONTEXT_UPDATE_EVENT";

  public SentianceUserContextEmitter(Context context) {
    super(context);
  }

  void sendUserContext(List<UserContextUpdateCriteria> criteria, UserContext userContext) {
    WritableMap map = Arguments.createMap();
    map.putMap("userContext", convertUserContext(userContext));
    map.putArray("criteria", convertCriteriaList(criteria));

    sendEvent(USER_CONTEXT_EVENT, map);
  }
}
