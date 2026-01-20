# Venlab - Vue.js Frontend für ReST-Backend

## Projektübersicht

Dieses Projekt implementiert ein Vue.js Frontend für die Venlab-Laborverwaltung. Es bietet CRUD-Funktionalität für Analysedaten, Proben, Boxen und Box-Positionen.

## Autoren

- David Schneider
- Lukas Kerndl
- Amr Buranici

| Technologie | Version | Beschreibung                         |
| ----------- | ------- | ------------------------------------ |
| Vue.js      | 3.4.x   | Frontend Framework (Composition API) |
| Vuetify     | 3.4.x   | Material Design UI Framework         |
| Axios       | 1.6.x   | HTTP Client                          |
| Vue Router  | 4.2.x   | SPA Routing                          |
| Vite        | 5.0.x   | Build Tool                           |
| Nginx       | Alpine  | Reverse Proxy & Static Hosting       |
| Docker      | -       | Containerisierung                    |

## Projektstruktur

```
├── frontend/                    # Vue.js Frontend
│   ├── src/
│   │   ├── api/                # Axios Service-Klassen
│   │   │   ├── axios.js        # Basis-Konfiguration
│   │   │   ├── AnalysisService.js
│   │   │   ├── SampleService.js
│   │   │   ├── BoxService.js
│   │   │   ├── BoxPosService.js
│   │   │   ├── LogService.js
│   │   │   └── ThresholdService.js
│   │   ├── views/              # Vue Komponenten
│   │   │   ├── DashboardView.vue
│   │   │   ├── AnalysisView.vue    # CRUD
│   │   │   ├── SampleView.vue      # CRUD
│   │   │   ├── BoxView.vue         # CRUD (inkl. BoxPos)
│   │   │   ├── LogView.vue         # Read-only
│   │   │   └── ThresholdView.vue   # Read-only
│   │   ├── router/
│   │   ├── App.vue
│   │   └── main.js
│   ├── Dockerfile
│   └── nginx.conf
├── src/main/java/              # Spring Boot Backend
├── backup/                     # Datenbank-Backup
├── prompts/                    # KI-Prompt Dokumentation
├── docker-compose.yml
└── README.md
```

## Schnellstart

### Voraussetzungen

- Docker & Docker Compose
- Git

### Installation & Start

```bash
# 1. Repository klonen
git clone <repository-url>
cd <project-folder>

# 2. Backend bauen
./gradlew clean build

# 3. Docker-Container starten
docker-compose up -d --build

# 4. Warten bis alle Container healthy sind (ca. 60 Sekunden)
docker-compose ps
```

### URLs

| Service      | URL                                         |
| ------------ | ------------------------------------------- |
| **Frontend** | http://localhost                            |
| Backend API  | http://localhost:8081/api                   |
| Swagger UI   | http://localhost:8081/swagger-ui/index.html |
| Adminer (DB) | http://localhost:8080                       |

## Funktionalitäten

### CRUD-Operationen

| Tabelle   | Create | Read | Update | Delete |
| --------- | ------ | ---- | ------ | ------ |
| Analysis  | ✅      | ✅    | ✅      | ✅      |
| Sample    | ✅      | ✅    | ✅      | ✅      |
| Box       | ✅      | ✅    | ✅      | ✅      |
| BoxPos    | ✅      | ✅    | ✅      | ✅      |
| Log       | ❌      | ✅    | ❌      | ❌      |
| Threshold | ❌      | ✅    | ❌      | ❌      |

### Nicht editierbare Felder

Bestimmte Felder sind bei der Bearbeitung gesperrt:

**Analysis:**

- `a_id` (ID)
- `s_id` (Sample ID)
- `s_stamp` (Sample Timestamp)
- `weight_bru` (vom Sample)
- `weight_net` (vom Sample)
- `name` (vom Sample)
- `box` (Box Position vom Sample)

**Sample:**

- `s_id` (Sample ID)
- `s_stamp` (Timestamp)

**Box:**

- `b_id` (Box ID)

**BoxPos:**

- `b_id` (Box ID)
- `bpos_id` (Position ID)

### Sortierbare Spalten (indiziert)

Folgende Spalten sind sortierbar, da sie in der Datenbank indiziert sind:

**Analysis:**

- `date_in` (idx_analysis_date_in)
- `a_flags` (idx_analysis_a_flags)

**Sample:**

- `name` (idx_sample_name)
- `date_crumbled` (idx_sample_date_crumbled)
- `s_flags` (idx_sample_s_flags)

### Erweiterte Ansichten

**Analysis-Tabelle zeigt zusätzlich:**

- Sample Name (`sample.name`)
- Sample Gewicht Brutto (`sample.weight_bru`)
- Sample Gewicht Netto (`sample.weight_net`)
- Box Position (`sample_boxpos.boxpos`)

**Sample-Tabelle zeigt:**

- Box Position aus View `sample_boxpos`

**Box-Ansicht:**

- Kombinierte Darstellung von Boxen und Positionen
- Aufklappbare Zeilen für Box-Positionen

## Backend-Filterung: Überlegungen

### Aktuelle Situation

Derzeit erfolgt die Filterung/Suche client-seitig im Frontend. Dies funktioniert gut für kleine Datenmengen, ist aber bei großen Datensätzen ineffizient.

### Empfohlene Backend-Implementierung

Für eine effiziente Backend-Filterung sollten folgende Parameter unterstützt werden:

#### 1. Allgemeine Filter-Parameter

```
GET /api/analysis?filter=<field>:<operator>:<value>
```

**Beispiele:**

```
GET /api/analysis?filter=pol:gt:10.5           # POL > 10.5
GET /api/analysis?filter=flags:eq:VALID        # Flags = VALID
GET /api/analysis?filter=dateIn:gte:2024-01-01 # Datum >= 01.01.2024
```

**Operatoren:**
| Operator | Bedeutung |
|----------|-----------|
| `eq` | Gleich (=) |
| `ne` | Ungleich (!=) |
| `gt` | Größer als (>) |
| `gte` | Größer oder gleich (>=) |
| `lt` | Kleiner als (<) |
| `lte` | Kleiner oder gleich (<=) |
| `like` | Enthält (LIKE %value%) |
| `in` | In Liste (IN) |

#### 2. Mehrfache Filter

```
GET /api/analysis?filter=pol:gt:10&filter=flags:eq:VALID
```

#### 3. Sortierung

```
GET /api/analysis?sort=dateIn,desc&sort=id,asc
```

#### 4. Pagination

```
GET /api/analysis?page=0&size=20
```

#### 5. Volltextsuche

```
GET /api/analysis?search=keyword
```

### Spring Boot Implementierung

```java
@GetMapping
public Page<Analysis> getAll(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "20") int size,
    @RequestParam(required = false) String sort,
    @RequestParam(required = false) List<String> filter,
    @RequestParam(required = false) String search
) {
    Specification<Analysis> spec = Specification.where(null);

    // Filter anwenden
    if (filter != null) {
        for (String f : filter) {
            String[] parts = f.split(":");
            String field = parts[0];
            String operator = parts[1];
            String value = parts[2];
            spec = spec.and(createSpecification(field, operator, value));
        }
    }

    // Volltextsuche
    if (search != null && !search.isEmpty()) {
        spec = spec.and(fullTextSearch(search));
    }

    // Sortierung
    Sort sortObj = Sort.unsorted();
    if (sort != null) {
        String[] parts = sort.split(",");
        sortObj = Sort.by(Sort.Direction.fromString(parts[1]), parts[0]);
    }

    return repo.findAll(spec, PageRequest.of(page, size, sortObj));
}
```

### Frontend-Anpassung

```javascript
// In AnalysisService.js
async getFiltered(page = 0, size = 20, filters = [], sort = 'id,asc', search = '') {
  const params = { page, size, sort }

  if (filters.length > 0) {
    params.filter = filters
  }

  if (search) {
    params.search = search
  }

  const response = await apiClient.get('/api/analysis', { params })
  return response.data
}
```

### Vorteile der Backend-Filterung

1. **Performance**: Nur benötigte Daten werden übertragen
2. **Skalierbarkeit**: Funktioniert auch mit Millionen von Datensätzen
3. **Datenbankindizes**: Können effizient genutzt werden
4. **Konsistenz**: Filterlogik ist zentral im Backend

## API-Endpunkte

### Analysis

```
GET    /api/analysis              # Alle (paginiert)
GET    /api/analysis/{id}         # Einzelne
POST   /api/analysis              # Erstellen
PUT    /api/analysis/{id}         # Aktualisieren
DELETE /api/analysis/{id}         # Löschen
```

### Sample

```
GET    /api/sample                        # Alle (paginiert)
GET    /api/sample/{sId}/{sStamp}         # Einzelne
POST   /api/sample                        # Erstellen
PUT    /api/sample/{sId}/{sStamp}         # Aktualisieren
DELETE /api/sample/{sId}/{sStamp}         # Löschen
```

### Box

```
GET    /api/box                   # Alle
GET    /api/box/{id}              # Einzelne
POST   /api/box                   # Erstellen
PUT    /api/box/{id}              # Aktualisieren
DELETE /api/box/{id}              # Löschen
```

### BoxPos

```
GET    /api/boxpos                        # Alle
GET    /api/boxpos/{boxId}/{posId}        # Einzelne
GET    /api/boxpos/box/{boxId}            # Alle einer Box
POST   /api/boxpos                        # Erstellen
PUT    /api/boxpos/{boxId}/{posId}        # Aktualisieren
DELETE /api/boxpos/{boxId}/{posId}        # Löschen
```

### Log (Read-only)

```
GET    /api/log                   # Alle
GET    /api/log/{id}              # Einzelner
```

### Threshold (Read-only)

```
GET    /api/threshold             # Alle
GET    /api/threshold/{id}        # Einzelner
```

## Konsistenzprüfungen

Das System führt folgende Konsistenzprüfungen durch:

### Bei Erstellung

- **Analysis**: Sample mit angegebener ID/Timestamp muss existieren
- **BoxPos**: Box mit angegebener ID muss existieren
- **Sample**: ID + Timestamp Kombination muss eindeutig sein
- **Box**: ID muss eindeutig sein

### Fehlermeldungen

Bei Fehlern werden sprechende Meldungen angezeigt:

- "Sample mit dieser ID und Timestamp existiert bereits"
- "Box mit ID XXX existiert nicht"
- "Sample nicht gefunden"
- "Pflichtfeld"

## Entwicklung

### Frontend lokal starten

```bash
cd frontend
npm install
npm run dev
```

Das Frontend ist dann unter http://localhost:3000 erreichbar.

### Backend lokal starten

```bash
./gradlew bootRun
```

## Troubleshooting

### Container startet nicht

```bash
# Logs prüfen
docker-compose logs backend-app
docker-compose logs frontend

# Neustart mit frischem Volume
docker-compose down -v
docker-compose up -d --build
```

### Datenbank-Fehler

```bash
# Datenbank-Logs
docker-compose logs db

# Manueller Zugriff
docker exec -it postgres13 psql -U venlab -d venlab
```

## Quellen

- [Vue.js Documentation](https://vuejs.org/)
- [Vuetify Documentation](https://vuetifyjs.com/)
- [Axios Documentation](https://axios-http.com/)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
