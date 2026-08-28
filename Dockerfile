# ---------- 빌드 스테이지 ----------
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace

COPY . .
RUN chmod +x gradlew && ./gradlew --no-daemon bootJar -x test

# ---------- 런타임 스테이지 ----------
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

RUN useradd -r -u 1001 spring
USER spring

COPY --from=build /workspace/build/libs/app.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
