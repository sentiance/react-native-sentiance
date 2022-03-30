package com.sentiance.react.bridge.crashdetection;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SentianceCrashDetectionPackage implements ReactPackage {

		@Override
		public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
				List<NativeModule> modules = new ArrayList<>();
				SentianceCrashDetectionModule crashDetectionModule = new SentianceCrashDetectionModule(reactContext);
				modules.add(crashDetectionModule);
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
