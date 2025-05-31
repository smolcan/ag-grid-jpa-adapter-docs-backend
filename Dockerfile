
# BUILD STAGE
FROM openjdk:21-jdk-slim as build
WORKDIR /app
ADD . /app
#ENV JAVA_TOOL_OPTIONS="-XX:UseSVE=0"
RUN --mount=type=cache,target=/root/.m2 ./mvnw -U -f /app/pom.xml clean package

# run stage
FROM openjdk:21-jdk-slim
WORKDIR /app
# Copy the pre-built JAR file from the host machine
COPY --from=build /app/target/ag-grid-jpa-adapter-docs-backend-0.0.1-SNAPSHOT.jar app.jar
# Expose the default Spring Boot port
EXPOSE 8080
# Set the default command to run the Spring Boot application
#CMD ["java", "-XX:UseSVE=0", "-jar", "app.jar"]
CMD ["java", "-jar", "app.jar"]