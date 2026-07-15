FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /app

COPY . .

RUN mvn install:install-file \
    -Dfile=/app/libs/spring-iobox-starter-1.0.0.jar \
    -DgroupId=org.jedi_bachelor \
    -DartifactId=spring-iobox-starter \
    -Dversion=1.0.0 \
    -Dpackaging=jar \
    -DgeneratePom=true

RUN mvn install:install-file \
    -Dfile=/app/libs/spring-iobox-starter-0.0.1-SNAPSHOT.jar \
    -DgroupId=org.jedi_bachelor \
    -DartifactId=spring-iobox-starter \
    -Dversion=0.0.1-SNAPSHOT \
    -Dpackaging=jar \
    -DgeneratePom=true

RUN mvn clean package -DskipTests -pl config-server -am

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/config-server/target/*.jar app.jar

COPY --from=builder /app/config-server/src/main/resources/config /config

RUN addgroup -S spring && adduser -S spring -G spring
USER spring

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]