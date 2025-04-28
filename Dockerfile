FROM maven:3.9-eclipse-temurin-21-alpine AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create logs directory
RUN mkdir -p /app/logs

# Copy the built JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Environment variables with default values
ENV DATABASE_URL=$DATABASE_URL
ENV DATABASE_USERNAME=$DATABASE_USERNAME
ENV DATABASE_PASSWORD=$DATABASE_PASSWORD
ENV JWT_SECRET_KEY=$JWT_SECRET_KEY
ENV JWT_EXPIRATION_TIME=900000
ENV ALLOWED_ORIGINS=http://localhost:5173

# Expose the port the app runs on
EXPOSE 8080

# Command to run the application
CMD ["java", "-jar", "app.jar"]