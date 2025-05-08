#!/bin/bash
# set -x
#Copyright (c) 2024 Broadcom, Inc. All Rights Reserved.

# Default values
VERSION="1.1"
BUILD_DIR="."
OUTPUT_DIR="."
VERBOSE=false

# internal variables
QUIET="-q"
GIT_VERBOSE=">/dev/null 2>&1"
PLUGIN_VERSION="2.7.11"
PROGRESS_CHAR=">"

# Function to display help message
display_help() {
  echo
  echo "Version: "$VERSION
  echo
  echo "Collect data to run spring health assessment"
  echo
  echo "Usage: $0 [options]"
  echo
  echo "Options:"
  echo "  -b, --build-dir      Project build directory (default - current directory)"
  echo "  -o, --output-dir     Output directory location (default - current directory/assessment)"
  echo "  -v, --verbose        Show verbose logs for troubleshooting"
  exit 0
}


progress_bar() {
  if [ "$VERBOSE" = false ]; then
    local count="$1"
    local str=$PROGRESS_CHAR
    for ((i = 0; i < count; i++)); do
        str=$str$PROGRESS_CHAR
    done
    echo -ne $str"[$count%]\r"
    sleep 1
  fi

}

# Function to print/log messages when verbose is enabled
verbose_log() {
  echo "$(date '+%Y-%m-%d %H:%M:%S'): $@" >> "$LOG_NAME"
  if [ "$VERBOSE" = true ]; then
    echo "$(date '+%Y-%m-%d %H:%M:%S'): $@"
  fi
}

#verify command installed
command_exists() {
  command -v "$1" >/dev/null 2>&1
}

# log and execute
log_and_execute() {
  verbose_log "Executing command: $@"
  if [ "$VERBOSE" = true ]; then
    $@
  else
    $@ 2>>"$LOG_NAME" >&2
  fi

  if [ $? -ne 0 ]; then
     if [[ "$@" = *"org.spdx:spdx-maven-plugin:$PLUGIN_VERSION:createSPDX"* ]]; then
        echo "Not able to find the maven dependencies. Please build the project and re-run the script again."
      else
        echo "$(date '+%Y-%m-%d %H:%M:%S'): Failed to run: $@" | tee -a "$LOG_NAME"
      fi
     exit 1
  fi
}

# log and run commands
log_and_command(){
  command_string=$1
  shift
  verbose_log "Executing command: $@"
  command_value=$($@)
  eval "$command_string=$command_value"
}


# Parse command line options
while [ "$#" -gt 0 ]; do
  case "$1" in
    -b|--build-dir)
      BUILD_DIR="$2"
      shift 2
      ;;
    -o|--output-dir)
      OUTPUT_DIR="$2"
      shift 2
      ;;
    -v|--verbose)
      VERBOSE=true
      QUIET=""
      GIT_VERBOSE=""
      shift
      ;;
    -h|--help)
      display_help
      ;;
    *)
      echo "Unknown option: $1"
      echo "Use $0 --help to display available options."
      exit 1
      ;;
  esac
done

if [ "$BUILD_DIR" = "." ]; then
  BUILD_DIR=$(pwd)
fi

if [ "$OUTPUT_DIR" = "." ]; then
  OUTPUT_DIR=$(pwd)
fi

OUTPUT_DIR="$OUTPUT_DIR/assessment"
mkdir -p $OUTPUT_DIR
cd $OUTPUT_DIR
OUTPUT_DIR=$(pwd)
LOG_NAME="$OUTPUT_DIR/assessment.log"

verbose_log "*****************************************************************************"
verbose_log "*                     Started SBOM Collection                               *"
verbose_log "*****************************************************************************"

# Check if Maven is not installed
if ! command_exists "mvn"; then
  echo "Maven is not installed. Please install Maven and try again."
  exit 1
fi

# Check if Git is not installed
if ! command_exists "git"; then
  echo "Git is not installed. Please install Git and try again."
  exit 1
fi

progress_bar 20
log_and_execute cd "$BUILD_DIR"
pom_files=$(find . -name 'pom.xml' -type f)
if [ -z "$pom_files" ]; then
  echo "No build file (pom.xml) found in current directory. Please verify the project build directory"
  exit 1
fi

echo "$(date '+%Y-%m-%d %H:%M:%S'): Assessment data file generation started..."
progress_bar 30
directory=$OUTPUT_DIR"/sbom"
log_and_execute mvn "$QUIET" org.cyclonedx:cyclonedx-maven-plugin:$PLUGIN_VERSION:makeAggregateBom -DoutputDirectory=$directory -DoutputName=\$\{project.artifactId\} -DschemaVersion=1.4 -Dcyclonedx.skipAttach=true -Dcyclonedx.skipNotDeployed=false -DoutputFormat=json
progress_bar 80


# Iterate over each file in the directory
for file in "$directory"/*; do
    # Check if it's a regular file (not a directory)
    output_file=$OUTPUT_DIR/$(basename "$file")
    if [ -f "$file" ]; then
        echo '{"sbom":' > "$output_file"
        cat "$file" >> "$output_file"
        echo '}' >> "$output_file"
    fi
done

progress_bar 90
log_and_execute rm -rf "$directory"
progress_bar 100
echo -ne '\n'

echo "$(date '+%Y-%m-%d %H:%M:%S'): The assessment data file is generated at $OUTPUT_DIR. Upload this file to assessment portal."
