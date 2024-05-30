#!/bin/bash

SCRIPTS_FOLDER="scripts"
source "$SCRIPTS_FOLDER/new-sdk-module/shared.sh"

# Path to the new module's android/src folder
android_src_folder="$1"
# Simple path of the new module, e.g com/sentiance/newmodule
simple_module_path="$2"
# Qualified package name of the new module, e.g com.sentiance.newmodule
qualified_module_name="$3"
# Original module name, e.g new-module
module_name="$4"

# Convert to pascal case using perl
pascal_case_module_name=$(echo "$module_name" | perl -pe 's/(^|-)([a-z])/\U$2/gi')
moduleClassName="${pascal_case_module_name}Module"
moduleTestClassName="${pascal_case_module_name}ModuleTest"
react_native_package_class_name="${pascal_case_module_name}Package"

function generateModuleClasses() {
  generateProductionModuleClass $moduleClassName "$android_src_folder/main/java/$simple_module_path/${moduleClassName}.java"
  generateModuleTestClass $moduleClassName $moduleTestClassName "$android_src_folder/test/java/$simple_module_path/${moduleTestClassName}.java"
}

function generateProductionModuleClass() {
  local class_name="$1"
  local output_file="$2"
  local native_module_name="Sentiance$pascal_case_module_name"

  cat << EOF > "$output_file"
package $qualified_module_name;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.sentiance.react.bridge.core.common.SentianceSubscriptionsManager;
import com.sentiance.react.bridge.core.common.base.AbstractSentianceModule;
import com.sentiance.sdk.Sentiance;

public class $class_name extends AbstractSentianceModule {

    // This is the name that Javascript code will use to refer to this native module
    private static final String NATIVE_MODULE_NAME = "$native_module_name";

    public $class_name(ReactApplicationContext reactApplicationContext, Sentiance sentiance,
                            SentianceSubscriptionsManager subscriptionsManager) {
        super(reactApplicationContext, sentiance, subscriptionsManager);
    }

    @NonNull
    @Override
    public String getName() {
        return NATIVE_MODULE_NAME;
    }

    @Override
    protected void addSupportedEventSubscriptions(SentianceSubscriptionsManager subscriptionsManager) {
        // Add new supported subscriptions here via the subscriptions manager. e.g:

        // subscriptionsManager.addSupportedSubscription(
        //    SMART_GEOFENCE_EVENT,                                               <------- The event you're interested in
        //    mSmartGeofenceApi::setSmartGeofenceEventListener,                   <------- How to set a listener for the event of interest
        //    listener -> mSmartGeofenceApi.setSmartGeofenceEventListener(null),  <------- How to unset the said listener
        //    SentianceSubscriptionsManager.SubscriptionType.SINGLE               <------- Subscription type, to specify whether the native SDK
        // );                                                                              supports setting listeners (1 max), or adding multiple listeners
    }

    @Override
    @ReactMethod
    public void addListener(String eventName) {
        // Exposing a binding with this exact signature is needed to neutralize a runtime warning.
    }

    @Override
    @ReactMethod
    public void removeListeners(Integer count) {
        // Exposing a binding with this exact signature is needed to neutralize a runtime warning.
    }

    @Override
    @ReactMethod
    protected void removeNativeListener(String eventName, int subscriptionId, Promise promise) {
        if (rejectIfNotInitialized(promise)) {
            return;
        }

        mSubscriptionsManager.removeSubscription(subscriptionId, eventName);
        promise.resolve(null);
    }

    @Override
    @ReactMethod
    protected void addNativeListener(String eventName, int subscriptionId, Promise promise) {
        // This binding is invoked when the Sentiance SDK's Javascript APIs attempt to register
        // listeners for a specific event.
        throw new UnsupportedOperationException("Not yet implemented.");
    }
}
EOF

  echo_gray "Generated new module class: $qualified_module_name.$class_name"
}

function generateModuleTestClass() {
  local module_class_name="$1"
  local module_test_class_name="$2"
  local output_file="$3"

  cat << EOF > "$output_file"
package $qualified_module_name;

import com.sentiance.react.bridge.test.ReactNativeModuleTest;

import org.junit.Test;

public class $module_test_class_name extends ReactNativeModuleTest<$module_test_class_name> {
    @Override
    protected $module_class_name initModule() {
        return new $module_class_name(mReactApplicationContext, mSentiance, mSentianceSubscriptionsManager);
    }

    @Test
    public void someBindingTest() {
        /*
         mModule.someBinding();
         verify(mPromise).resolve(writableMapCaptor.capture());
        */
    }
}
EOF

  echo_gray "Generated new module test class: $qualified_module_name.$module_test_class_name"
}

function generateNewPackageForAutoLinking() {
  local output_file="$1"

  cat << EOF > "$output_file"
package $qualified_module_name;

import androidx.annotation.NonNull;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.sentiance.react.bridge.core.common.SentianceSubscriptionsManager;
import com.sentiance.sdk.Sentiance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class $react_native_package_class_name implements ReactPackage {
    @NonNull
    @Override
    public List<NativeModule> createNativeModules(@NonNull ReactApplicationContext reactApplicationContext) {
        List<NativeModule> modules = new ArrayList<>();

        $module_class_name module = new $module_class_name(
            reactApplicationContext,
            Sentiance.getInstance(reactApplicationContext),
            new SentianceSubscriptionsManager()
        );
        modules.add(module);

        return modules;
    }

    @NonNull
    @Override
    public List<ViewManager> createViewManagers(@NonNull ReactApplicationContext reactApplicationContext) {
        return Collections.emptyList();
    }
}
EOF

  echo_gray "Generated new package class: $qualified_module_name.$react_native_package_class_name"
}

generateModuleClasses
generateNewPackageForAutoLinking "$android_src_folder/main/java/$simple_module_path/${react_native_package_class_name}.java"
