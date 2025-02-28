package com.sentiance.react.bridge.usercontext;

import android.content.Context;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.sentiance.react.bridge.core.common.base.AbstractSentianceEmitter;
import com.sentiance.sdk.usercontext.api.UserContext;
import com.sentiance.sdk.usercontext.api.UserContextUpdateCriteria;

import java.util.List;

public class SentianceUserContextEmitter extends AbstractSentianceEmitter {

  static final String USER_CONTEXT_EVENT = "SENTIANCE_USER_CONTEXT_UPDATE_EVENT";
  private final SentianceUserContextConverter converter;

  public SentianceUserContextEmitter(Context context) {
    super(context);
    converter = new SentianceUserContextConverter();
  }

  void sendUserContext(List<UserContextUpdateCriteria> criteria, UserContext userContext) {
    WritableMap map = Arguments.createMap();
    map.putMap("userContext", converter.convertUserContext(userContext));
    map.putArray("criteria", converter.convertCriteriaList(criteria));

    sendEvent(USER_CONTEXT_EVENT, map);
  }
}
