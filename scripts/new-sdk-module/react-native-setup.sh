#!/bin/bash

SCRIPTS_FOLDER="scripts"
source "$SCRIPTS_FOLDER/new-sdk-module/shared.sh"

# e.g packages/new-module
new_module_dir="$1"
module_name="$2"

lib_dir="$new_module_dir/lib"

function createPackageJson() {
  local package_json_path="$new_module_dir/package.json"

  cat << EOF > "$package_json_path"
{
   "name":"$ORG_NAME/$module_name",
   "version":"0.0.1",
   "description":"New module description goes here",
   "main":"lib/index.js",
   "typings":"lib/index.d.ts",
   "scripts":{
      "test":"jest --verbose",
      "lint":"npx eslint lib/index.d.ts"
   },
   "keywords":[
      "react-native",
      "$module_name",
      "sentiance"
   ],
   "peerDependencies":{
      "@sentiance-react-native/core":"6.0.0"
   },
   "homepage":"https://github.com/sentiance/react-native-sentiance/packages/$module_name#readme",
   "repository":"github:sentiance/react-native-sentiance",
   "publishConfig":{
      "access":"public"
   }
}
EOF

  # Format the JSON file using Prettier
  ./node_modules/.bin/prettier --write --loglevel=silent "$package_json_path"

  if [ $? -ne $SUCCESS ]; then
    return $ERR_PACKAGE_JSON
  fi

  echo_gray "Created a package.json file at $package_json_path"
}

function createJsTestsFolder() {
  local tests_dir="$new_module_dir/__tests__"
  mkdir -p $tests_dir

  # Use output re-direction to create an empty file
  > "$tests_dir/$module_name.test.js"

  if [ $? -ne $SUCCESS ]; then
    echo_red "Failed to create the $tests_dir directory."
    return $ERR_CREATE_JS_TESTS_FOLDER
  fi

  echo_gray "Created a jest __tests__ folder at $tests_dir"
}

function createLibFolder() {
  mkdir "$lib_dir"
  local result=$?

  if [ $result -ne $SUCCESS ]; then
    echo_red "Failed to create the $lib_dir directory."
    return $ERR_CREATE_JS_LIBS_FOLDER
  fi

  createTypescriptDefinitionFile
  result=$?
  if [ $result -ne $SUCCESS ]; then
    return $result
  fi

  indexJsFile="$lib_dir/index.js"
  > "$indexJsFile"
  result=$?
  if [ $result -ne $SUCCESS ]; then
    echo_red "Failed to create $indexJsFile"
    return $ERR_CREATE_JS_ENTRY_POINT
  fi

  echo_gray "Created a JS entry point at $indexJsFile"
  return $result
}

function createReadmeFile() {
  local output_file="$new_module_dir/README.md"
  local formatted_module_name=$(replace_dashes_with_spaces_and_capitalize "$module_name")

  cat << EOF > "$output_file"
# Sentiance $formatted_module_name module for React Native

## Demo Application

https://github.com/sentiance/sample-apps-react-native

## Usage

To use the $formatted_module_name SDK module, please visit the corresponding [API reference page.](https://docs.sentiance.com/important-topics/sdk/api-reference/react-native/$module_name)

EOF

  if [ $? -ne $SUCCESS ]; then
    echo_red "Failed to create a README file."
    return $ERR_CREATE_README
  fi

  echo_gray "Created a README.md at $output_file"
}

function addNewModuleToNpmWorkspaces() {
  json_file="package.json"

  if [ ! -f "$json_file" ]; then
    echo_red "Could not locate the top level $json_file"
    return $ERR_ADD_NPM_WORKSPACE
  fi

  # The name of the workspace of the new SDK module, wrapped in double quotes
  local new_workspace="\"$PACKAGES_FOLDER/$module_name\""

  # Use jq to check if the new module's workspace name already exists in the 'workspaces' array
  if jq --exit-status --argjson value "$new_workspace" '.workspaces | index($value)' "$json_file" > /dev/null; then
    echo_yellow "The workspace $new_workspace is already present in the project's workspaces config."
    return $SUCCESS
  fi

  # Use jq to add the new SDK module to the 'workspaces' array
  jq ".workspaces += [$new_workspace]" "$json_file" > "temp_$json_file"

  if [ $? -eq $SUCCESS ]; then
    mv "temp_$json_file" "$json_file"
  else
    echo_red "Failed to update top level $json_file file."
    rm "temp_$json_file"
    return $ERR_ADD_NPM_WORKSPACE
  fi

  echo_gray "Added a new NPM workspace declaration for '$module_name'"
}

function updateSdkModulesMap() {
  if [ ! -f "$SDK_MODULES_MAP_FILE_PATH" ]; then
    echo-red "Could not locate $SDK_MODULES_MAP_FILE_PATH"
    return $ERR_UPDATE_SDK_MODULES_MAP
  fi

  local new_module_json_obj="{\"name\": \"$module_name\"}"

  jq ". += [$new_module_json_obj]" $SDK_MODULES_MAP_FILE_PATH > temp.json

  if [ $? -eq $SUCCESS ]; then
    mv temp.json "$SDK_MODULES_MAP_FILE_PATH"
  else
    echo_red "Failed to update the SDK modules' map."
    return $ERR_UPDATE_SDK_MODULES_MAP
  fi

  echo_gray "Updated the SDK modules map with an entry for '$module_name'"
}

function createTypescriptDefinitionFile() {
  local output_file="$lib_dir/index.d.ts"
  local formatted_module_name=$(remove_dashes_and_capitalize "$module_name")

  cat << EOF > "$output_file"
declare module "@sentiance-react-native/$module_name" {
  import { EmitterSubscription } from "react-native";

  export interface Sentiance$formatted_module_name {
  }

  const Sentiance$formatted_module_name: Sentiance$formatted_module_name;
  export default Sentiance$formatted_module_name;
}
EOF

  if [ $? -ne $SUCCESS ]; then
    echo_red "Failed to create $output_file"
    return $ERR_CREATE_TYPESCRIPT_DEFINITION
  fi

  echo_gray "Created a Typescript definition file at $output_file"
}

echo_cyan "Performing necessary React Native setup..."
function runSetup() {
  createPackageJson || { code=$?; return $code; }
  createJsTestsFolder || { code=$?; return $code; }
  createLibFolder || { code=$?; return $code; }
  createReadmeFile || { code=$?; return $code; }
  addNewModuleToNpmWorkspaces || { code=$?; return $code; }
  updateSdkModulesMap || { code=$?; return $code; }
}

runSetup
exitCode=$?
exit $exitCode
