package com.sentiance.react.bridge.core;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SentiancePackage implements ReactPackage {

		@Override
		public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
				List<NativeModule> modules = new ArrayList<>();
				SentianceModule sentianceModule = new SentianceModule(reactContext);
				modules.add(sentianceModule);
				return modules;
		}

		// Deprecated from RN 0.47
		public List<Class<? extends JavaScriptModule>> createJSModules() {
				return Collections.emptyList();
		}

		@Override
		public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
				return Collections.emptyList();
		}
}
