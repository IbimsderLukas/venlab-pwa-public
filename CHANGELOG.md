# Changelog

Alle wichtigen Änderungen an diesem Projekt werden in dieser Datei dokumentiert.

Das Format basiert auf [Keep a Changelog](https://keepachangelog.com/de/1.0.0/).

## [1.0.0] - 2025-01-10

### Hinzugefügt

#### PWA Konfiguration
- Service Worker für Offline-Funktionalität
- Web App Manifest für Installation
- Caching-Strategien für API-Requests
- Vollbild-Modus ohne Browser-UI
- App-Icons für verschiedene Plattformen

#### Login System
- Einfache Frontend-Authentifizierung
- Demo-Benutzer: admin, user, researcher
- Session-Persistenz via LocalStorage
- Logout-Funktionalität

#### Dark/Light Theme
- Umschaltbarer Theme-Switch
- Persistente Theme-Einstellung
- Vuetify Theme-Integration

#### Spaltenauswahl
- Konfigurierbare Tabellenspalten
- Pro-Tabelle Einstellungen
- Persistente Spalten-Konfiguration
- "Alle/Keine/Standard" Aktionen

#### CI/CD Pipeline
- GitHub Actions Workflow
- Backend JUnit Tests
- JaCoCo Coverage Reports
- Automatische Badge-Generierung
- Frontend PWA Build

### Technologien
- Vue.js 3.4
- Vuetify 3.5
- Vite 5 mit PWA Plugin
- Pinia (State Management)
- Spring Boot 3 (Backend)
- PostgreSQL 13
- Docker & Docker Compose
- GitHub Actions
