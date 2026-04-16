# build stage
FROM maven:3.9.14-eclipse-temurin-21 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# runtime stage
FROM amazoncorretto:21
ARG PROFILE=dev
ARG APP_VERSION=0.0.4

WORKDIR /app
COPY --from=build /build/target/labo-gestion-*.jar /app/app.jar

EXPOSE 8089

ENV DB_URL=jdbc:postgresql://postgres-sql-labo:5432/labo_gestion
ENV ACTIVE_PROFILE=${PROFILE}

CMD java -Dspring.profiles.active=${ACTIVE_PROFILE} -Dspring.datasource.url=${DB_URL} -jar app.jar