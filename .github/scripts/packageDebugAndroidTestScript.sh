#!/bin/bash

modulesWithTests=()

basePath=$(pwd)

# Normalize basePath to ensure no trailing slash
basePath=${basePath%/}

# Find all modules with androidTest implemented in Kotlin
while IFS= read -r moduleDir; do
    # Determine if the androidTest directory contains Kotlin files
    if [ "$(find "$moduleDir" -type f -name "*.kt" | wc -l)" -gt 0 ]; then
        # Extract the module path relative to the project root, ensuring no leading './'
        relativePath="${moduleDir#$basePath/}"
        relativePath="${relativePath#./}" # Remove leading './' if present

        # This replaces '/' with ':' and removes the 'src/androidTest/kotlin' part
        moduleName="${relativePath%/src/androidTest/kotlin}" # Ensure to remove the suffix
        moduleName="${moduleName//\//:}"    # Replace all slashes with colons

        # Add the module name to the list
        modulesWithTests+=("$moduleName")
    fi
done < <(find . -type d -path "*/src/androidTest/kotlin" -not -path "*/build/*")

if [ ${#modulesWithTests[@]} -eq 0 ]; then
    echo "No instrumented tests to run."
else
    echo "Found instrumented tests in the following modules, running 'packageDebugAndroidTest':"
    for moduleName in "${modulesWithTests[@]}"; do
        echo "- Module: $moduleName"
        echo "  Command: ./gradlew ${moduleName}:packageDebugAndroidTest"
    done

    for moduleName in "${modulesWithTests[@]}"; do
        echo "Packaging AndroidTest APK for module: $moduleName"
        ./gradlew "${moduleName}:packageDebugAndroidTest"
    done
fi
