FROM eclipse-temurin:21-jdk AS build
WORKDIR /src
RUN apt-get update && apt-get install -y --no-install-recommends maven && rm -rf /var/lib/apt/lists/*
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src src
RUN mvn -B -q package -DskipTests

FROM eclipse-temurin:21-jre
RUN useradd --system --uid 10001 app
WORKDIR /app
COPY --from=build /src/target/artifactory-manager-*.jar app.jar
USER app
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "/app/app.jar"]
