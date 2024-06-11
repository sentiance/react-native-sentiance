#!/bin/bash

SCRIPTS_FOLDER="scripts"
source "$SCRIPTS_FOLDER/new-sdk-module/shared.sh"

output_file="$1"
fully_qualified_module_name="$2"

cat << EOF > "$output_file"
plugins {
  id "com.android.library"
}

def coreProj
if (findProject(':core')) {
  coreProj = project(':core')
} else if (findProject(':sentiance-react-native_core')) {
  // Starting from RN 0.61, the @ sign is stripped from project names
  coreProj = project(':sentiance-react-native_core')
} else if (findProject(':@sentiance-react-native_core')) {
  // On RN 0.60, the @ sign is not stripped from project names
  coreProj = project(':@sentiance-react-native_core')
} else {
  throw new GradleException('Could not find the @sentiance-react-native/core package, have you installed it?')
}

android {
  namespace "$fully_qualified_module_name"

  compileOptions {
    sourceCompatibility JavaVersion.VERSION_1_8
    targetCompatibility JavaVersion.VERSION_1_8
  }
}

apply from: "\$coreProj.projectDir/package-json-reader.gradle"
apply from: "\$coreProj.projectDir/sentiance-version-finder.gradle"

def corePackageJson = PackageJson.of(coreProj)
applyAndroidVersionsFrom(corePackageJson)
def sentianceSdkVersion = getSentianceSdkVersion()

dependencies {
    implementation(platform("com.sentiance:sdk-bom:\${sentianceSdkVersion}"))
    api coreProj

    if (findProject(':test-common')) {
        testImplementation project(':test-common')
    }

    // Add here any other dependencies that the new module requires
}

applyReactNativeDependency()
EOF
