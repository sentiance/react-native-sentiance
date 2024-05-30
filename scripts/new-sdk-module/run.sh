#!/bin/bash

# This script provides a step-by-step configuration for setting up a new SDK module.

SCRIPTS_FOLDER="scripts"
source "$SCRIPTS_FOLDER/new-sdk-module/shared.sh"

# Helper setup scripts
REACT_NATIVE_SETUP="$SCRIPTS_FOLDER/new-sdk-module/react-native-setup.sh"
ANDROID_SETUP="$SCRIPTS_FOLDER/new-sdk-module/android-setup.sh"

# Check if the packages folder exists in the current directory
if [ ! -d "$PACKAGES_FOLDER" ]; then
    echo-red "You must run this script from the root of the project."
    exit 1
fi

echo ""
echo ""
echo ""
echo_red "███╗   ██╗███████╗██╗    ██╗    ███████╗██████╗ ██╗  ██╗    ███╗   ███╗ ██████╗ ██████╗ ██╗   ██╗██╗     ███████╗"
echo_red "████╗  ██║██╔════╝██║    ██║    ██╔════╝██╔══██╗██║ ██╔╝    ████╗ ████║██╔═══██╗██╔══██╗██║   ██║██║     ██╔════╝"
echo_red "██╔██╗ ██║█████╗  ██║ █╗ ██║    ███████╗██║  ██║█████╔╝     ██╔████╔██║██║   ██║██║  ██║██║   ██║██║     █████╗  "
echo_red "██║╚██╗██║██╔══╝  ██║███╗██║    ╚════██║██║  ██║██╔═██╗     ██║╚██╔╝██║██║   ██║██║  ██║██║   ██║██║     ██╔══╝  "
echo_red "██║ ╚████║███████╗╚███╔███╔╝    ███████║██████╔╝██║  ██╗    ██║ ╚═╝ ██║╚██████╔╝██████╔╝╚██████╔╝███████╗███████╗"
echo_red "╚═╝  ╚═══╝╚══════╝ ╚══╝╚══╝     ╚══════╝╚═════╝ ╚═╝  ╚═╝    ╚═╝     ╚═╝ ╚═════╝ ╚═════╝  ╚═════╝ ╚══════╝╚══════╝"
echo ""
echo ""
echo_gray ""

read -p "Enter the name of the new SDK module: " module_name

# Check if the packages folder already has a package with the provided module name
if [ -d "$PACKAGES_FOLDER/$module_name" ]; then
  echo_red "A package with the same name already exists, aborting."
  exit 2
fi

new_module_dir="$PACKAGES_FOLDER/$module_name"
mkdir "$new_module_dir"
res=$?
if [ $res -ne $SUCCESS ]; then
  echo_red "Failed to create directory: $new_module_dir"
  exit $ERR_CREATE_NEW_MODULE_FOLDER
fi

$REACT_NATIVE_SETUP $new_module_dir $module_name
res=$?
if [ $res -eq $SUCCESS ]; then
  echo_green "React Native and JS related module set up. ✅  "
else
  echo_red "React Native setup failed with code: $res"
  exit $res
fi

$ANDROID_SETUP $new_module_dir $module_name
res=$?
if [ $res -eq $SUCCESS ]; then
  echo_green "Android module set up. ✅  "
else
  echo_red "Android module setup failed with code: $res"
  exit $res
fi

echo_magenta "$module_name has been successfully set up !"
