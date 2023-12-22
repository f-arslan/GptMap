# Use a base image with the desired JDK version
FROM adoptopenjdk:11-jdk-hotspot

# Set the working directory inside the container
WORKDIR /app

# Copy the gradlew file and the gradle directory to the container
COPY gradlew .
COPY gradle ./gradle

# Give execute permission to gradlew
RUN chmod +x ./gradlew

# Copy the build.gradle.kts and settings.gradle.kts files to the container
COPY build.gradle.kts settings.gradle.kts ./

# Copy the source code to the container
COPY src ./src

# Copy the Google Cloud credentials to the container
COPY keyfile.json ./keyfile.json

ENV GOOGLE_APPLICATION_CREDENTIALS=/app/keyfile.json

# Build the application using Gradle
RUN ./gradlew build

# Expose the port that the application will listen on
EXPOSE 8080

# Start the application when the container starts
CMD ["java", "-jar", "build/libs/server-all.jar"]
