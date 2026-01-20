FROM amazoncorretto:17-alpine-jdk
LABEL version="1.0"

RUN apk add --no-cache tzdata
ENV TZ=Europe/Vienna

COPY build/libs/backend-0.0.1-SNAPSHOT.jar /jar/backend.jar

ENTRYPOINT ["java", "-jar", "/jar/backend.jar"]