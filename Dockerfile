# ---------- BUILD STAGE ----------
FROM maven:3.9.4-amazoncorretto-21 AS builder

WORKDIR /workspace

# Копируем pom.xml — кешируются зависимости
COPY pom.xml .
RUN mvn -B -f pom.xml dependency:go-offline

# Копируем исходники
COPY src ./src

# Собираем приложение
RUN mvn -B -DskipTests package


# ---------- RUN STAGE ----------
FROM amazoncorretto:21-alpine

WORKDIR /app

# Копируем собранный JAR
COPY --from=builder /workspace/target/*.jar app.jar

# Ограничения по памяти (Render/железо любят)
ENV JAVA_OPTS="-Xms256m -Xmx512m"

EXPOSE 8080

# Если используешь Spring Actuator — это правильный healthcheck
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
