FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build

ARG OTEL_AGENT_VERSION=2.32.0
RUN curl -fsSL -o /build/opentelemetry-javaagent.jar \
    https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v${OTEL_AGENT_VERSION}/opentelemetry-javaagent.jar

COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app

RUN useradd --system --create-home appuser
COPY --from=build /build/opentelemetry-javaagent.jar /opt/otel/opentelemetry-javaagent.jar
COPY --from=build /build/target/temperature-conversion-service-*.jar app.jar
USER appuser

ENV JAVA_TOOL_OPTIONS="-javaagent:/opt/otel/opentelemetry-javaagent.jar" \
    OTEL_SERVICE_NAME=temperature-conversion-service

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
