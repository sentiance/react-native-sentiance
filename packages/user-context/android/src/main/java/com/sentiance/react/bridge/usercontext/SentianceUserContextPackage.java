package com.sentiance.react.bridge.usercontext;

import androidx.annotation.NonNull;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.sentiance.react.bridge.core.common.SentianceSubscriptionsManager;
import com.sentiance.sdk.Sentiance;
import com.sentiance.sdk.usercontext.api.UserContextApi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SentianceUserContextPackage implements ReactPackage {

	@NonNull
	@Override
	public List<NativeModule> createNativeModules(@NonNull ReactApplicationContext reactContext) {
		List<NativeModule> modules = new ArrayList<>();
		SentianceUserContextModule module = new SentianceUserContextModule(
			reactContext,
			Sentiance.getInstance(reactContext),
			new SentianceSubscriptionsManager(),
			new SentianceUserContextEmitter(reactContext),
			new SentianceUserContextConverter(),
			UserContextApi.getInstance(reactContext)
		);
		modules.add(module);
		return modules;
	}

	@NonNull
	@Override
	public List<ViewManager> createViewManagers(@NonNull ReactApplicationContext reactContext) {
		return Collections.emptyList();
	}
}
