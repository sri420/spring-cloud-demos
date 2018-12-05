# Start with a base image containing Java runtime
FROM openjdk:8-jdk-alpine
# Add Maintainer Info
LABEL maintainer="gsamartian@cheerful.com"
# Add a volume pointing to /tmp
VOLUME /tmp
# Make port 8080 available to the world outside this container
EXPOSE 9080
# The application's jar file
ARG JAR_FILE=target/Billing-0.0.1-SNAPSHOT.jar
# Add the application's jar to the container
ADD ${JAR_FILE} billing-service.jar
# Run the jar file 
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/billing-service.jar"]

