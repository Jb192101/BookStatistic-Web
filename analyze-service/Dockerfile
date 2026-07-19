FROM eclipse-temurin:21-jre

WORKDIR /app

RUN mkdir -p /app/models

RUN addgroup --system spring && adduser --system spring --ingroup spring

COPY target/*.jar app.jar

RUN chown -R spring:spring /app

USER spring

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]