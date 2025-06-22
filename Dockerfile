# Use official OpenJDK base image
FROM eclipse-temurin:17-jdk-alpine

# Set app directory
WORKDIR /app

# Copy and build your JAR
COPY target/*.jar app.jar

# Expose the port your app runs on
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]


