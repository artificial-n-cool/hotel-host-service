FROM curlimages/curl:7.81.0 AS OTEL_AGENT
ARG OTEL_AGENT_VERSION="1.12.1"
RUN curl --silent --fail -L "https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v${OTEL_AGENT_VERSION}/opentelemetry-javaagent.jar" \
    -o "/tmp/opentelemetry-javaagent.jar"


FROM maven:3.9-eclipse-temurin-17-alpine AS build

WORKDIR /code

COPY pom.xml /code/pom.xml
RUN ["mvn", "dependency:resolve"]
RUN ["mvn", "verify"]

COPY ["src/main", "/code/src/main"]
RUN ["mvn", "package"]


FROM eclipse-temurin:17-jdk-alpine AS package

COPY --from=build /code/target/host-app.jar /
COPY --from=OTEL_AGENT /tmp/opentelemetry-javaagent.jar /otel-javaagent.jar
ENV JAVA_OPTS "-Dspring.config.location=src/main/resources/application.yml"

EXPOSE 8080
ENTRYPOINT exec java -javaagent:/otel-javaagent.jar -jar host-app.jar
#CMD ["java", "-jar", "/host-app.jar"]