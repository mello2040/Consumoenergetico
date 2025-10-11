# Etapa 1: build (Maven já instalado)
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /src

COPY pom.xml .
RUN mvn -B -ntp -q dependency:go-offline

COPY src ./src
RUN mvn -B -ntp clean package -DskipTests

# Etapa 2: runtime (JRE)
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /src/target/*.jar /app/app.jar
EXPOSE 8080
ENV JAVA_OPTS=""
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]