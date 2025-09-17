# Use an official Maven image to build the app
FROM maven:3.9.4-eclipse-temurin-17 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the rest of the project
COPY src ./src

# Build the Spring Boot jar
RUN mvn clean package -DskipTests

# ----------------------
# Runtime image
# ----------------------
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy jar from the build stage
COPY --from=build /app/target/social-post-0.0.1-SNAPSHOT.jar app.jar

# Expose port (same as Spring Boot default)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java","-jar","app.jar"]
