# PWA Aufgabe - Schritt-für-Schritt Anleitung

## Übersicht: Was wir machen

| Schritt | Was | Status |
|---------|-----|--------|
| 1 | Repo klonen & Setup | ⬜ |
| 2 | PWA Konfiguration | ⬜ |
| 3 | Dark/Light Theme | ⬜ |
| 4 | Spalten-Auswahl | ⬜ |
| 5 | JWT Login | ⬜ |
| 6 | CI/CD Workflow | ⬜ |
| 7 | DigitalOcean Deploy | ⬜ |

---

## Schritt 1: Repo klonen & Setup

```bash
cd C:\Users\lukas.dumbo\Schule\2025

# Repo klonen (ersetze DEIN_REPO mit dem echten Namen)
git clone --recursive https://github.com/TGM-HIT/insy5-informationssysteme-vue-pwa-DEIN_REPO.git
cd insy5-informationssysteme-vue-pwa-DEIN_REPO

# Falls CRUD noch nicht als Submodul:
git submodule add https://github.com/TGM-HIT/insy5-informationssysteme-vue-crud-kerndl_vuedaterbase.git libs/vue-crud
git submodule update --init --recursive
```

---

## Schritt 2: PWA Konfiguration

### 2.1 Package.json aktualisieren

Füge in `frontend/package.json` diese Dependencies hinzu:

```json
{
  "devDependencies": {
    "vite-plugin-pwa": "^0.17.4",
    "workbox-window": "^7.0.0"
  }
}
```

### 2.2 vite.config.js anpassen

Ersetze `frontend/vite.config.js` mit der bereitgestellten Datei.

### 2.3 PWA Icons erstellen

Erstelle diese Dateien in `frontend/public/`:
- `pwa-192x192.png` (192x192 px)
- `pwa-512x512.png` (512x512 px)
- `apple-touch-icon.png` (180x180 px)

**Tipp:** Nutze https://favicon.io/ oder https://realfavicongenerator.net/

### 2.4 Testen

```bash
cd frontend
npm install
npm run build
npm run preview
```

Öffne Chrome DevTools → Application → Manifest um die PWA zu prüfen.

---

## Schritt 3: Dark/Light Theme

### 3.1 Theme Store erstellen

Kopiere `stores/theme.js` in `frontend/src/stores/`

### 3.2 ThemeToggle Komponente

Kopiere `components/ThemeToggle.vue` in `frontend/src/components/`

### 3.3 In App.vue integrieren

```vue
<template>
  <v-app :theme="themeStore.isDark ? 'dark' : 'light'">
    <!-- ... -->
    <ThemeToggle />
  </v-app>
</template>

<script setup>
import { useThemeStore } from '@/stores/theme'
import ThemeToggle from '@/components/ThemeToggle.vue'

const themeStore = useThemeStore()
</script>
```

---

## Schritt 4: Spalten-Auswahl

### 4.1 ColumnSelector Komponente

Kopiere `components/ColumnSelector.vue` in `frontend/src/components/`

### 4.2 In Views integrieren (z.B. AnalysisView.vue)

```vue
<template>
  <v-container>
    <div class="d-flex mb-4">
      <ColumnSelector
        :columns="allColumns"
        storage-key="analysis-columns"
        @update:visible-columns="visibleColumns = $event"
      />
    </div>
    
    <v-data-table
      :headers="visibleColumns"
      :items="items"
    />
  </v-container>
</template>

<script setup>
import { ref } from 'vue'
import ColumnSelector from '@/components/ColumnSelector.vue'

const allColumns = [
  { key: 'id', title: 'ID' },
  { key: 'sampleId', title: 'Sample ID' },
  { key: 'dateIn', title: 'Datum Ein' },
  // ... weitere Spalten
]

const visibleColumns = ref([...allColumns])
</script>
```

---

## Schritt 5: JWT Login

### 5.1 Backend: Security hinzufügen

Kopiere diese Dateien in dein Backend:
- `security/SecurityConfig.java`
- `security/JwtUtils.java`
- `security/JwtAuthenticationFilter.java`
- `controller/AuthController.java`
- `dto/LoginRequest.java`
- `dto/LoginResponse.java`

### 5.2 build.gradle aktualisieren

Füge diese Dependencies hinzu:

```gradle
implementation 'org.springframework.boot:spring-boot-starter-security'
implementation 'io.jsonwebtoken:jjwt-api:0.11.5'
runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.11.5'
runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.11.5'
```

### 5.3 Frontend: Auth Store & Login

Kopiere:
- `stores/auth.js` → `frontend/src/stores/`
- `views/LoginView.vue` → `frontend/src/views/`
- `router/index.js` → `frontend/src/router/`

### 5.4 Testen

```bash
# Backend starten
cd backend
./gradlew bootRun

# Frontend starten
cd frontend
npm run dev
```

Login mit: `admin` / `admin123`

---

## Schritt 6: CI/CD Workflow

### 6.1 GitHub Actions

Kopiere `.github/workflows/ci-cd.yml` in dein Repo.

### 6.2 Secrets konfigurieren

Gehe zu GitHub → Repo → Settings → Secrets:
- `DIGITALOCEAN_ACCESS_TOKEN`: Dein DO API Token
- `DIGITALOCEAN_APP_ID`: Wird nach App-Erstellung gesetzt

---

## Schritt 7: DigitalOcean Deployment

### 7.1 GitHub Student Pack aktivieren

1. Gehe zu https://education.github.com/pack
2. Aktiviere DigitalOcean Benefit ($200 Credit)

### 7.2 DigitalOcean Account erstellen

1. Gehe zu https://cloud.digitalocean.com
2. Registriere dich mit GitHub
3. Löse Student Credit ein

### 7.3 App erstellen

1. Gehe zu https://cloud.digitalocean.com/apps
2. Klicke "Create App"
3. Wähle "GitHub" als Source
4. Wähle dein Repo
5. Konfiguriere:
   - **Backend**: Docker, Port 8080
   - **Frontend**: Docker, Port 80
   - **Database**: PostgreSQL Dev ($0/Monat im Student Plan)

### 7.4 Environment Variables setzen

Im DigitalOcean Dashboard:
```
SPRING_DATASOURCE_URL: ${db.JDBC_DATABASE_URL}
SPRING_DATASOURCE_USERNAME: ${db.USERNAME}
SPRING_DATASOURCE_PASSWORD: ${db.PASSWORD}
JWT_SECRET: [Generiere einen sicheren Key]
```

### 7.5 Datenbank initialisieren

Nach dem ersten Deploy:
1. Öffne die Console im DO Dashboard
2. Führe das SQL-Backup aus

### 7.6 Domain (optional)

DigitalOcean gibt dir eine URL wie: `venlab-pwa-xxxxx.ondigitalocean.app`

---

## Checkliste Grundanforderungen

- [ ] PWA installierbar auf Handy (Icon auf Homescreen)
- [ ] Offline-Indikator sichtbar
- [ ] Dark/Light Theme Switch funktioniert
- [ ] Theme wird gespeichert (localStorage)
- [ ] Spalten-Auswahl bei allen Tabellen
- [ ] Login-Seite vorhanden
- [ ] JWT Token wird gespeichert
- [ ] API-Zugriff nur mit Token
- [ ] CI/CD Pipeline läuft (grüne Checks)
- [ ] App auf DigitalOcean erreichbar

---

## Lokales Testen

```bash
# Alles starten
docker-compose up --build

# Nur Backend + DB
docker-compose up db backend

# Mit Adminer
docker-compose --profile tools up
```

**URLs:**
- Frontend: http://localhost
- Backend: http://localhost:8081
- Swagger: http://localhost:8081/swagger-ui.html
- Adminer: http://localhost:8080

---

## Troubleshooting

### PWA wird nicht installiert
- HTTPS erforderlich (oder localhost)
- Manifest muss gültig sein
- Service Worker muss registriert sein

### Login funktioniert nicht
- Prüfe Backend Logs
- Prüfe Network Tab in DevTools
- Prüfe JWT_SECRET ist gesetzt

### DigitalOcean Build schlägt fehl
- Prüfe Dockerfile Syntax
- Prüfe dass alle Dependencies installiert sind
- Prüfe Build Logs im DO Dashboard
