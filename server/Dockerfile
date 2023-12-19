# Use a base image with the desired JDK version
FROM adoptopenjdk:11-jdk-hotspot

# Set the working directory inside the container
WORKDIR /server

# Copy the build.gradle.kts and settings.gradle.kts files to the container
COPY build.gradle.kts settings.gradle.kts /server/

# Copy the source code to the container
COPY src /server/src

# Build the application using Gradle
RUN ./gradlew build

# Expose the port that the application will listen on
EXPOSE 8080

# Start the application when the container starts
CMD ["java", "-jar", "build/libs/your-application.jar"]
