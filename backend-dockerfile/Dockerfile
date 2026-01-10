# Build Stage
FROM gradle:8-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle build -x test --no-daemon

# Production Stage
FROM amazoncorretto:17-alpine
WORKDIR /app

RUN apk add --no-cache tzdata
ENV TZ=Europe/Vienna

COPY --from=build /app/build/libs/*.jar /app/backend.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Xms512m", "-Xmx1024m", "-jar", "/app/backend.jar"]
