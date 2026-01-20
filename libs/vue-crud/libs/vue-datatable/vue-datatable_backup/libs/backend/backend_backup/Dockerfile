FROM amazoncorretto:21-alpine AS builder

RUN apk add --no-cache gradle
RUN apk add --no-cache openjdk17

WORKDIR /app

# Backend aus Submodul kopieren
COPY libs/vue-datatable/libs/backend/ .

# Erweiterte Controller überschreiben
COPY backend-extensions/controller/SampleController.java src/main/java/at/ac/tgm/backend/controller/
COPY backend-extensions/controller/BoxController.java src/main/java/at/ac/tgm/backend/controller/
COPY backend-extensions/controller/BoxPosController.java src/main/java/at/ac/tgm/backend/controller/
COPY backend-extensions/repository/BoxPosRepository.java src/main/java/at/ac/tgm/backend/repository/


RUN gradle bootJar --no-daemon

FROM amazoncorretto:21-alpine

LABEL version="3.0"
RUN apk add --no-cache tzdata fontconfig freetype font-noto ttf-liberation busybox-extras curl
ENV TZ=Europe/Vienna

WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-Xms512M", "-Xmx1024M", "-jar", "app.jar"]
