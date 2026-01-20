# GK911 Informationssysteme "ReST Backend"

Autoren: Timon Polster, Lukas kerndl, Jifan Zeitouni

### Projekt aufbau

Bevor wir mit der Aufgabe starten kann muss eine Spring application erstellt werden und mit folgenden addons ausgestattet sein [1]:

```
✔ Spring Web
✔ Spring Data JPA
✔ PostgreSQL Driver
```

Diese sind für die Aufgabe Essenziell sonnst können wir Spring nicht richtig benutzten.

### Die eigentliche Aufgabe

Zuerst erstelle ich die docker-compose Datei, sodass sie das Backend auch als eigenständiges Image erstellt mit eigenständiger IP.

docker-compose [2][3]:

```yaml
services:
  db:
    image: postgres:13
    container_name: postgres13
    restart: always
    env_file:
      - .env
    ports:
      - "5432:5432"
    volumes:
      - ./backup/restore.sql:/docker-entrypoint-initdb.d/restore.sql
      - ./backup:/backup
      - pgdata:/var/lib/postgresql/data
    networks:
      backend:
        ipv4_address: 172.28.0.10

  adminer:
    image: adminer:4
    container_name: adminer
    restart: unless-stopped
    ports:
      - "8080:8080"
    environment:
      ADMINER_DEFAULT_SERVER: db
    networks:
      backend:
        ipv4_address: 172.28.0.20

  backend-app:
    build:
      context: ./backend
      dockerfile: Dockerfile
    container_name: rest-backend
    restart: unless-stopped
    depends_on:
      - db
    env_file:
      - .env
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/${POSTGRES_DB}
      SPRING_DATASOURCE_USERNAME: ${POSTGRES_USER}
      SPRING_DATASOURCE_PASSWORD: ${POSTGRES_PASSWORD}
      SPRING_JPA_HIBERNATE_DDL_AUTO: none
    ports:
      - "8081:8080"
    networks:
      backend:
        ipv4_address: 172.28.0.30

volumes:
  pgdata:

networks:
  backend:
    driver: bridge
    ipam:
      config:
        - subnet: 172.28.0.0/24
```

Der backup Ordner wird in /backup gemounted die Datenbank in data. Da der Data Ordner nicht mehr als eines halten kann.

Das Backend hat für den späteren zugriff mittels swager den Port 8081:8080

Daher der backup Odner von der letzten Aufgabe schon alles richtig hat (restore update etc.) brauchen wir an dieser nichts mehr zu ändern.

Dann verändern wir die ApplicationPropertier in src/main/recources folgend, um die verbindung mit der Datenbank herzustellen [1]:

```properties
spring.application.name=backend

spring.datasource.url=jdbc:postgresql://db:5432/venlab
spring.datasource.username=${POSTGRES_USER}
spring.datasource.password=${POSTGRES_PASSWORD}

spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

Dann noch ein DOCKERFILE direkt im Hauptordner erstellen vorallem um die jar datein einzubauen.

```
FROM amazoncorretto:25-alpine-jdk
LABEL version="1.0"

RUN apk add --no-cache tzdata fontconfig freetype font-noto ttf-liberation busybox-extras curl
ENV TZ=Europe/Vienna

# JAR von Gradle Build kopieren (egal wie sie heißt)
COPY build/libs/*.jar /jar/backend.jar

ENTRYPOINT ["java", "-Xms1024M", "-Xmx2048M", "-jar", "/jar/backend.jar"]
```

**WICHTIG**

Bevor jetzt aber mit dem Bau des Backend begonnen wird müssen noch folgden Despendencies in die gradle.build hinzugefuegt werden(falls noch nicht vorhanden):

```
implementation 'org.springframework.boot:spring-boot-starter-validation'
implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0'
compileOnly 'org.projectlombok:lombok'
annotationProcessor 'org.projectlombok:lombok'
```

Jetzt können wir mit dem Bau des Backend starten.

Zuerst bauen wir die allgemaine Struktur des Backend auf, daher wir mvc verwenden sieht das dann in dem src ordern schlussendlich folgendermasen aus (Natürlich sind am Anfang nur die Ordner zu erstellen):

```
- config/
- controller/
- model/
- service/
- repository/
BackendApplication.java
```

```
├── config
│   └── WebConfig.java
├── controller
│   ├── AnalysisController.java
│   ├── BoxController.java
│   ├── BoxPosController.java
│   ├── LogController.java
│   ├── SampleController.java
│   └── ThresholdController.java
├── model
│   ├── Analysis.java
│   ├── Box.java
│   ├── BoxPos.java
│   ├── BoxPosId.java
│   ├── LogEntry.java
│   ├── Sample.java
│   ├── SampleBoxPos.java
│   ├── SampleId.java
│   └── Threshold.java
├── repository
│   ├── AnalysisRepository.java
│   ├── BoxPosRepository.java
│   ├── BoxRepository.java
│   ├── LogRepository.java
│   ├── SampleRepository.java
│   └── ThresholdRepository.java
└── service
    ├── AnalysisService.java
    └── BackendApplication.java
```

Das sind all die Java klassen die wir für ein saubere umsetztung des Backends erstellen müssen.

Hier eine kruze erklärung aller Ordner und ein beispiel eines der Datein, daher sonnst dieses Readme viel zu lang wäre:

##### config

Hier liegen die grundlegenden Einstellungen für die Anwendung. Dazu gehört die Konfiguration für die automatische API-Dokumentation (Swagger) und die WebConfig, die regelt, dass dein Frontend überhaupt Anfragen an das Backend schicken darf (CORS-Freigabe).

Webconfig.java

```java
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
    }
}
```

##### controller

Dieser Ordner bildet die Schnittstelle nach außen. Die Klassen hier nehmen die HTTP-Anfragen vom Frontend entgegen (z.B. unter /api/analysis), validieren kurz die Eingaben und geben sie dann an die interne Logik weiter. Page und Pageable sind nur für das Frontend dann wichtig.

AnalysisControler.java [2]

```java
import at.ac.tgm.backend.model.Analysis;
import at.ac.tgm.backend.service.AnalysisService;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RestController
@RequestMapping("/api/analysis")
public class AnalysisController {

    private final AnalysisService service;

    public AnalysisController(AnalysisService service) {
        this.service = service;
    }

    @GetMapping
    public Page<Analysis> findAll(Pageable pageable) {
        return service.findAll(pageable);
    }

   // Andere Crud Operatoren
}
```

##### model

Hier wird die Datenbankstruktur in Java abgebildet. Jede Klasse, wie Analysis oder Box, entspricht direkt einer Tabelle in der Datenbank. Zusätzlich liegen hier Hilfsklassen für Tabellen, die einen zusammengesetzten Primärschlüssel benötigen (wie SampleId).

analysis.java

```java
@Entity
@Table(name = "analysis", schema = "venlab")
@Data
public class Analysis {

    @Id
    @Column(name = "a_id")
    private Long id;

    @Column(name = "s_id")
    private String sampleId;

    @Column(name = "s_stamp")
    private LocalDateTime sampleStamp;

    private Double pol;
    private Double nat;
    private Double kal;
    private Double an;
    private Double glu;
    private Double dry;

    @Column(name = "date_in")
    private LocalDateTime dateIn;

    @Column(name = "date_out")
    private LocalDateTime dateOut;
    
    //Gewicht density etc. alle Spalten hinzufügen

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "s_id", referencedColumnName = "s_id", insertable = false, updatable = false),
            @JoinColumn(name = "s_stamp", referencedColumnName = "s_stamp", insertable = false, updatable = false)
    })
    private Sample associatedSample;

}
```

Wichtig ist hierbei die @Data annotation, damit die getter und setter automatisch generiert werden und nicht geschrieben werden müssen.

##### repository

Diese Dateien managen den direkten Zugriff auf die Datenbank. Sie stellen automatisch Methoden zum Speichern, Suchen und Löschen bereit, ohne dass man SQL-Code schreiben muss, da sie auf Spring Data JPA basieren.

AnalysisRepository.java

```java
package at.ac.tgm.backend.repository;

import at.ac.tgm.backend.model.Analysis;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnalysisRepository extends JpaRepository<Analysis, Long> {
}
```

##### service

In diesem Bereich liegt die eigentliche Geschäftslogik. Der Service sitzt zwischen dem Controller und der Datenbank. Er prüft beispielsweise, ob Daten logisch zusammenpassen (Konsistenzprüfung), bevor sie tatsächlich gespeichert werden.

analysisService.java

```java
import java.util.List;

@Service
public class AnalysisService {

    private final AnalysisRepository repo;
    private final SampleRepository sampleRepo;

    public AnalysisService(AnalysisRepository repo, SampleRepository sampleRepo) {
        this.repo = repo;
        this.sampleRepo = sampleRepo;
    }

    //find All und findbyID wie auch davor

    public Analysis save(Analysis a) {
        if (a.getSampleId() != null) {
            SampleId checkId = new SampleId();
            checkId.setId(a.getSampleId());
            checkId.setStamp(a.getSampleStamp());

            if (!sampleRepo.existsById(checkId)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Es existiert kein Sample mit der ID " + a.getSampleId()
                );
            }
        }
        return repo.save(a);
    }

    //Delete
}
```

##### BackendApplication.java

Diese Datei liegt direkt im Hauptpaket und ist der Startpunkt der Anwendung. Sie fährt den gesamten Server und alle Komponenten hoch.

BackendApplication.java

```java
package at.ac.tgm.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "at.ac.tgm.backend")
@EnableJpaRepositories(basePackages = "at.ac.tgm.backend.repository")
@EntityScan(basePackages = "at.ac.tgm.backend.model")
public class BackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }
}
```

#### Wichtig

Die Test class kann fürs erste gelöscht werden daher diese fehler inizieren kann.

Die REST-API Dokumentation wurde mittels SpringDoc OpenAPI realisiert. Um das zu machen muss zum einen wie oben schon gesehen in den build.gradle folgender import gemacht werden.

```
implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0'
```

Um zum anderen in dem Config ordner ein weitere Java klasse hinzugefügt werden.

swaggerConfig.java

```java
package at.ac.tgm.backend.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("Venlab REST API")
                        .version("1.0.0")
                        .description("CRUD API für Analysis, Sample, Box, BoxPos, Log, Threshold"));
    }
}
```

#### Ausfuehrung

Um den Container und das Backend zu starten/bauen folgende befehle ausfueren

```
./gradlew clean build
docker-compose up -d --build
```

Swagger ist dann hier erreichbar:

http://localhost:8081/swagger-ui/index.html

# Literaturverzeichnis

[1] Was ist der Unterschied zwischen MariaDB und PostgreSQL?; AWS; Seattle; zuletzt abgerufen am 2025-12-19; Verfügbar unter: https://aws.amazon.com/de/compare/the-difference-between-mariadb-and-postgresql/

[2] PostgreSQL 18.1 Documentation: INSERT; PostgreSQL Global Development Group; Online; zuletzt abgerufen am 2025-12-19; Verfügbar unter: https://www.postgresql.org/docs/current/sql-insert.html

[3] Adminer - Official Docker Image; Docker Inc.; Online; zuletzt abgerufen am 2025-12-19; Verfügbar unter: https://hub.docker.com/_/adminer/
