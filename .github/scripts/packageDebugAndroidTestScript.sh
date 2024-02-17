#!/bin/bash

# Base path for androidTest directories
basePath="./"

# Array to hold modules with tests
modulesWithTests=()

# Find all modules with androidTest implemented in Kotlin
while IFS= read -r moduleDir; do
    # Check if the androidTest directory is not empty
    if [ "$(find "$moduleDir" -type f -name "*.kt" | wc -l)" -gt 0 ]; then
        # Extract the module name from the path, remove leading './', and replace slashes with colons
        moduleName="${moduleDir#$basePath}" # Remove base path prefix
        moduleName="${moduleName//\//:}"    # Replace all slashes with colons

        # Add the module name to the array
        modulesWithTests+=("$moduleName")
    fi
done < <(find . -type d -path "*/src/androidTest/kotlin" -not -path "*/build/*")

# Check if any modules were found
if [ ${#modulesWithTests[@]} -eq 0 ]; then
    echo "No instrumented tests to run."
else
    echo "Found instrumented tests in the following modules:"
    for moduleName in "${modulesWithTests[@]}"; do
        echo "- $moduleName"
    done

    # Iterate over the modules and execute Gradle tasks
    for moduleName in "${modulesWithTests[@]}"; do
        echo "Packaging $moduleName"
        ./gradlew "${moduleName}:packageDebug" "${moduleName}:packageDebugAndroidTest"

        echo "Running tests for $moduleName"
        ./gradlew "${moduleName}:connectedDebugAndroidTest"
    done
fi
