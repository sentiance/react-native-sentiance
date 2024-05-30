PACKAGES_FOLDER="packages"
ORG_NAME="@sentiance-react-native"
SDK_MODULES_MAP_FILE_PATH="./$SCRIPTS_FOLDER/sdk_modules_map.json"
GRADLE_SETTINGS_FILE_PATH="$PACKAGES_FOLDER/settings.gradle"
ANDROID_BASE_PACKAGE_NAME="com.sentiance.react.bridge"

# Exit codes
SUCCESS=0
ERR_PACKAGE_JSON=1
ERR_ADD_NPM_WORKSPACE=2
ERR_UPDATE_SDK_MODULES_MAP=3
ERR_CREATE_ANDROID_MAIN_FOLDER=4
ERR_CREATE_ANDROID_TEST_FOLDER=5
ERR_SETTINGS_GRADLE=6
ERR_ANDROID_MANIFEST=7
ERR_MODULE_BUILD_GRADLE=8
ERR_GENERATE_JAVA_CODE=9
ERR_CREATE_JS_TESTS_FOLDER=10
ERR_CREATE_NEW_MODULE_FOLDER=11
ERR_CREATE_JS_LIBS_FOLDER=12
ERR_CREATE_README=13
ERR_CREATE_TYPESCRIPT_DEFINITION=14
ERR_CREATE_JS_ENTRY_POINT=15

# Function to write a line to the output file and check for errors
append_to_file() {
    echo "$2" >> "$1"
    if [ $? -ne 0 ]; then
        exit 1
    fi
}

# Strip dashes from a string
function stripDashes() {
  echo "$1" | sed 's/-//g'
}

# Function to replace dashes with spaces and capitalize each word
replace_dashes_with_spaces_and_capitalize() {
  local input_string="$1"
  # Replace dashes with spaces
  local modified_string="${input_string//-/ }"
  # Capitalize the first letter of each word
  local capitalized_string=$(echo "$modified_string" | awk '{for(i=1;i<=NF;i++) $i=toupper(substr($i,1,1)) tolower(substr($i,2)); print}')
  echo "$capitalized_string"
}

# Function to remove dashes with spaces and capitalize each word
remove_dashes_and_capitalize() {
  # Convert to pascal case using perl
  local dash_less_pascal_case=$(echo "$1" | perl -pe 's/(^|-)([a-z])/\U$2/gi')
  echo $dash_less_pascal_case
}

# Color variables
RED="\033[0;31m"
CYAN="\033[0;36m"
YELLOW="\033[0;33m"
MAGENTA="\033[0;35m"
GREEN="\033[0;32m"
GRAY="\033[0;37m"

function echo_red() {
    echo -e "${RED}$1${RESET}"
}

function echo_cyan() {
    echo -e "${CYAN}$1${RESET}"
}

function echo_yellow() {
    echo -e "${YELLOW}$1${RESET}"
}

function echo_magenta() {
    echo -e "${MAGENTA}$1${RESET}"
}

function echo_green() {
    echo -e "${GREEN}$1${RESET}"
}

function echo_gray() {
    echo -e "${GRAY}$1${RESET}"
}
