# VenLab PWA

Progressive Web Application für Labor-Datenmanagement.

![Coverage](.github/badges/jacoco.svg)
![Branches](.github/badges/branches.svg)

## Voraussetzungen

- Docker & Docker Compose
- Node.js 20+ (für lokale Entwicklung)
- Git

## Installation

### 1. Repository klonen

```bash
git clone --recursive <repository-url>
cd insy5-informationssysteme-vue-pwa-<name>
```

### 2. Environment einrichten

```bash
cp .env.example .env
```

Bearbeite `.env` und setze sichere Passwörter:

```env
POSTGRES_USER=postgres
POSTGRES_PASSWORD=dein_sicheres_passwort
POSTGRES_DB=venlab
```

**⚠️ WICHTIG:** Niemals `.env` committen!

### 3. Docker starten

```bash
docker-compose up -d
```

### 4. Datenbank initialisieren

```bash
# Backup einspielen (falls vorhanden)
docker exec -it venlab-db pg_restore -U postgres -d venlab --no-owner /backup
```

### 5. Zugriff

| Service     | URL                                   |
| ----------- | ------------------------------------- |
| Frontend    | http://localhost                      |
| Backend API | http://localhost:8081/api             |
| Adminer     | http://localhost:8080                 |
| Swagger     | http://localhost:8081/swagger-ui.html |

## Login

| Benutzer   | Passwort    | Rolle      |
| ---------- | ----------- | ---------- |
| admin      | admin123    | Admin      |
| user       | user123     | Reader     |
| researcher | research123 | Researcher |

## PWA Installation

1. Öffne http://localhost im Browser
2. Klicke auf das Install-Icon in der Adressleiste
3. Die App wird als eigenständige Anwendung installiert

## Entwicklung

### Frontend

```bash
cd frontend
npm install
npm run dev
```

### Backend Tests

```bash
cd libs/vue-crud/libs/vue-datatable/libs/backend
./gradlew test jacocoTestReport
```

Test-Reports: `build/reports/tests/test/index.html`
Coverage-Reports: `build/reports/jacoco/test/html/index.html`

## CI/CD

Die Pipeline läuft automatisch bei Push auf `main`:

1. **Backend Tests** - JUnit Tests mit JaCoCo Coverage
2. **Frontend Build** - Vite PWA Build
3. **Deployment Info** - Summary mit Status

Coverage-Badges werden automatisch aktualisiert.

## Cloud Deployment (DigitalOcean)

1. Gehe zu [DigitalOcean App Platform](https://cloud.digitalocean.com/apps)
2. "Create App" → GitHub Repository verbinden
3. Environment Variables setzen (aus `.env.example`)
4. Deploy!

## Projektstruktur

```
├── frontend/                    # Vue.js PWA
│   ├── src/
│   │   ├── components/         # Vue Components
│   │   ├── views/              # Page Views
│   │   ├── stores/             # Pinia Stores
│   │   ├── router/             # Vue Router
│   │   └── plugins/            # Vuetify Config
│   └── public/                 # Static Assets
├── libs/
│   └── vue-crud/               # Submodule
│       └── libs/vue-datatable/
│           └── libs/backend/   # Spring Boot Backend
├── .github/workflows/          # CI/CD Pipeline
├── docker-compose.yml          # Docker Services
├── .env.example                # Environment Template
└── README.md
```

## Changelog

Siehe [CHANGELOG.md](CHANGELOG.md)

## Autoren

- Projekt für TGM HIT Informationssysteme
