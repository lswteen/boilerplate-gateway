# Use Amazon Corretto 17
FROM amazoncorretto:17

# Set the working directory in the Docker container
WORKDIR /app

# Copy the built artifact into the container
COPY ./build/libs/*.jar app.jar

# Command to run the application
CMD ["java", "-jar", "app.jar"]