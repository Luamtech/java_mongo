# syntax=docker/dockerfile:1.4
# Forzamos el uso de BuildKit para mejoras de rendimiento y características avanzadas

# Stage 1: Build optimizado
FROM csanchez/maven:4.0-azulzulu-17 AS builder

# Creamos usuario no-root para la etapa de construcción
RUN addgroup --system --gid 1000 builduser && \
    adduser --system --uid 1000 --ingroup builduser builduser

WORKDIR /app

# Copiamos primero las dependencias para aprovechar la caché
COPY pom.xml .

# Usamos cacheo de Maven con BuildKit
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline

COPY src src
COPY config config

# Aseguramos permisos antes de ejecutar Maven
RUN chown -R builduser:builduser /app

USER builduser

# Build paralelo con detección automática de núcleos
RUN CORES=$(nproc --all) && \
    mvn clean package -DskipTests -T ${CORES}C \
    -Dmaven.javadoc.skip=true

# Stage 2: Runtime optimizado para producción
FROM amazoncorretto:17-minimal

# Variables de configuración ajustables
ENV SPRING_PROFILES_ACTIVE=prod \
    JVM_ADD_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=25.0 -XX:+AlwaysActAsServerClassMachine" \
    TZ=UTC \
    LANG=en_US.UTF-8

# Usamos UID/GID numéricos para compatibilidad con Kubernetes
RUN addgroup --system --gid 1000 appuser && \
    adduser --system --uid 1000 --ingroup appuser --no-create-home appuser && \
    mkdir -p /app/logs && \
    chown -R 1000:1000 /app && \
    microdnf install tini curl && \
    microdnf clean all

WORKDIR /app

# Copiamos el artefacto de construcción
COPY --from=builder --chown=1000:1000 /app/target/*.jar app.jar

# Configuración de logging y métricas
VOLUME ["/app/logs", "/tmp"]
EXPOSE 8083 9779

# Healthcheck mejorado
HEALTHCHECK --interval=30s --timeout=5s --start-period=45s --retries=3 \
  CMD curl -fsSk http://localhost:8083/actuator/health | grep -q '"status":"UP"' || exit 1

# Usamos tini como init para manejo adecuado de señales
ENTRYPOINT ["tini", "--"]

# Ejecución final con parámetros dinámicos, con valores por defecto en JVM_ADD_OPTS
CMD exec java ${JVM_ADD_OPTS:-"-XX:+UseContainerSupport"} \
    -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} \
    -Djava.security.egd=file:/dev/./urandom \
    -Dlogging.file.path=/app/logs \
    -jar app.jar

USER 1000:1000
