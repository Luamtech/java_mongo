# syntax=docker/dockerfile:1.6

### Etapa 1: Compilación de la app con Maven (Azul Zulu)
FROM csanchez/maven:4.0-azulzulu-17 AS builder

RUN addgroup --system --gid 1000 builduser && \
    adduser --system --uid 1000 --ingroup builduser builduser

WORKDIR /app

COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline

COPY src src
COPY config config

RUN chown -R builduser:builduser /app
USER builduser

RUN mvn clean package -DskipTests -Dmaven.javadoc.skip=true


### Etapa 2: Generación de runtime mínimo con jlink
FROM amazoncorretto:17-alpine3.21-jdk AS jlink

WORKDIR /jre

RUN jlink \
  --output /jre/custom \
  --add-modules java.base,java.logging,java.management,java.naming,java.net.http \
  --no-header-files \
  --no-man-pages \
  --strip-debug \
  --compress=2


### Etapa 3: Imagen final súper liviana con Alpine puro
FROM alpine:3.21.3

ENV SPRING_PROFILES_ACTIVE=prod \
    TZ=UTC \
    LANG=en_US.UTF-8 \
    JVM_ADD_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=25.0 -XX:+AlwaysActAsServerClassMachine"

RUN addgroup -g 1000 appuser && \
    adduser -u 1000 -G appuser -s /sbin/nologin -D appuser && \
    apk add --no-cache tini curl tzdata && \
    cp /usr/share/zoneinfo/${TZ} /etc/localtime && \
    echo "${TZ}" > /etc/timezone && \
    mkdir -p /app/logs && \
    chown -R 1000:1000 /app

# Copiamos el runtime Java optimizado
COPY --from=jlink /jre/custom /opt/java
ENV PATH="/opt/java/bin:$PATH"

WORKDIR /app

# Copiamos el JAR compilado
COPY --from=builder --chown=1000:1000 /app/target/*.jar app.jar

VOLUME ["/app/logs", "/tmp"]
EXPOSE 8083 9779

HEALTHCHECK --interval=30s --timeout=5s --start-period=45s --retries=3 \
  CMD curl -fsSk http://localhost:8083/actuator/health | grep -q '"status":"UP"' || exit 1

ENTRYPOINT ["tini", "--"]

CMD exec java ${JVM_ADD_OPTS:-"-XX:+UseContainerSupport"} \
  -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} \
  -Djava.security.egd=file:/dev/./urandom \
  -Dlogging.file.path=/app/logs \
  -jar app.jar

USER 1000:1000