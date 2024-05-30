#!/bin/bash

SCRIPTS_FOLDER="scripts"
source "$SCRIPTS_FOLDER/new-sdk-module/shared.sh"

# Helper scripts
GENERATE_BUILD_GRADLE="$SCRIPTS_FOLDER/new-sdk-module/generate_build_gradle.sh"
GENERATE_JAVA_CODE="$SCRIPTS_FOLDER/new-sdk-module/generate_java_code.sh"

# e.g packages/new-module
new_module_dir="$1"
module_name="$2"
# Strip the module name from any dashes
module_name_stripped=$(stripDashes $module_name)
fully_qualified_module_name="$ANDROID_BASE_PACKAGE_NAME.$module_name_stripped"
android_folder="$new_module_dir/android"
android_src_folder="$android_folder/src"
android_main_folder="$android_src_folder/main/java"
android_test_folder="$android_src_folder/test/java"

function createAndroidSrcFolder() {
  local android_base_package_names_slashed=$(echo "$ANDROID_BASE_PACKAGE_NAME" | sed 's/\./\//g')
  local simple_module_path=$(getPackagePathForModule $module_name)

  local result=$SUCCESS

  # Create the main folder structure
  main_module_path="$android_main_folder/$simple_module_path"
  mkdir -p "$main_module_path"
  result=$?
  if [ $result -ne $SUCCESS ]; then
    echo_red "Failed to create android/main folder"
    return $ERR_CREATE_ANDROID_MAIN_FOLDER
  fi
  echo_gray "Created a new directory at $main_module_path"

  # Create the AndroidManifest.xml file
  createManifestFile $android_main_folder
  result=$?
  if [ $result -ne $SUCCESS ]; then
    return $result
  fi

  # Create the test folder structure
  test_module_path="$android_test_folder/$simple_module_path"
  mkdir -p "$test_module_path"
  result=$?
  if [ $result -ne $SUCCESS ]; then
    echo_red "Failed to create android/test folder"
    return $ERR_CREATE_ANDROID_TEST_FOLDER
  fi
  echo_gray "Created a new directory at $test_module_path"

  return $result
}

function editSettingsGradleFile() {
  # Check if the new module is already included in the Gradle build
  if grep -q "'$module_name'" "$GRADLE_SETTINGS_FILE_PATH"; then
    echo_yellow "Module '$module_name' is already included in the Gradle build."
    return
  fi

  # Add the new module before the closing bracket of the modules array
  sed -i '' "/].each { projectName ->/i \\
  \ \ '$module_name',
  " "$GRADLE_SETTINGS_FILE_PATH"

  if [ $? -ne $SUCCESS ]; then
    echo_red "Failed to include '$module_name' in the project wide settings.gradle config."
    return $ERR_SETTINGS_GRADLE
  fi

  echo_gray "Added a new entry for $module_name in $GRADLE_SETTINGS_FILE_PATH"
}

function createManifestFile() {
  local android_main_folder="$1"
  local android_manifest_path="$android_main_folder/AndroidManifest.xml"

  echo '<?xml version="1.0" encoding="UTF-8"?>' > "$android_manifest_path"
  echo "<manifest package=\"$fully_qualified_module_name\">" >> "$android_manifest_path"
  echo "</manifest>" >> "$android_manifest_path"

  if [ $? -ne $SUCCESS ]; then
    echo_red "Failed to create $android_manifest_path"
    return $ERR_ANDROID_MANIFEST
  fi

  echo_gray "Created an Android manifest file at $android_manifest_path"
}

function createModuleGradleBuildFile() {
  local buildGradleFile="$android_folder/build.gradle"

  $GENERATE_BUILD_GRADLE $buildGradleFile

  if [ $? -ne $SUCCESS ]; then
    echo_red "Failed to create a $buildGradleFile file."
    return $ERR_MODULE_BUILD_GRADLE
  fi

  echo_gray "Created a build.gradle file at $buildGradleFile"
}

function generateJavaCode() {
  local simple_module_path=$(getPackagePathForModule $module_name)

  $GENERATE_JAVA_CODE $android_src_folder $simple_module_path $fully_qualified_module_name $module_name

  if [ $? -ne $SUCCESS ]; then
    echo_red "An error occurred while generating Java code."
    return $ERR_GENERATE_JAVA_CODE
  fi
}

# Result will be something like com/sentiance/react/bridge/newmodulenamewithoutdashes
function getPackagePathForModule() {
  local android_base_package_names_slashed=$(echo "$ANDROID_BASE_PACKAGE_NAME" | sed 's/\./\//g')
  echo "$android_base_package_names_slashed/$module_name_stripped"
}

echo_cyan "Setting a new native Android module up..."
function runSetup() {
  createAndroidSrcFolder || { code=$?; return $code; }
  editSettingsGradleFile || { code=$?; return $code; }
  createModuleGradleBuildFile || { code=$?; return $code; }
  generateJavaCode || { code=$?; return $code; }
}

runSetup
exitCode=$?
exit $exitCode
