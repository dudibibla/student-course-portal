# Use an official OpenJDK runtime as a parent image
FROM eclipse-temurin:17-jdk-jammy

# Set the working directory to /app
WORKDIR /app

# Copy the executable jar to the container
COPY target/*.jar app.jar

# Expose port 8080 for the application
EXPOSE 8080

# Run the jar file 
ENTRYPOINT ["java","-jar","/app/app.jar"]
