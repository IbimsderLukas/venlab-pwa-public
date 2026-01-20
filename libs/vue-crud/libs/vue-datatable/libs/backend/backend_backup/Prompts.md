# Promptverzeichnis "GBT-5 Thinking"

## Prompt 1

**Input**

> gebe mir den sed befehl für die datei sodass ich von /var/lib/postgresql/data/backup zu den was wir brauchen alles ersetzt

**Output**

```bash
sed -i 's|/var/lib/postgresql/data/backup|/backup|g' restore.sql
```

## Prompt 2

**Input**

> Passt der so? 
> sed -i 's|/var/lib/postgresql/data/backup|/backup|g' restore.sql

**Output** 
Ja, der Befehl passt **exakt**. 
Er ersetzt **alle Vorkommen** von `/var/lib/postgresql/data/backup` durch `/backup` in der Datei `restore.sql`.

## Prompt 3

**Input**

> Was ist jetzt wenn mein container down ist, ist dann alles weg oder bleibt alles, also kann ich ihn dann einfach wieder up machen?

**Output** 
Kurzfassung: **Nein, es ist nicht alles weg.**

- `docker-compose down` stoppt und entfernt **Container und Netzwerk**
  
- **Volumes bleiben erhalten**, solange du sie nicht explizit löschst
  
- Beim nächsten `docker-compose up` werden die Container **neu erstellt**, aber mit **denselben Daten
  

## Prompt 4

**Input**

> Ganz sicher das ich BoxPos.java und so die ganzen anderen neuen auch brauche?

**Output** 
Nein, **nicht zwingend sofort**.

Für die **Mindestanforderung** brauchst du **nur**:

- Entity
  
- Repository
  
- Controller
  

für **Analysis**.

Die anderen Tabellen (`Sample`, `Box`, `BoxPos`, `Log`) sind **für die bessere Bewertung**, nicht für das absolute Minimum.

## Prompt 5

**Input**

> bei schritt 3 wird nichts angezeigt, die website ist nicht erreichbar, sicher das es auf 8090 läuft?

**Output** 
Nein. 
Dein Container läuft auf **8081**, nicht auf 8090.

Aus `docker ps`:

```
0.0.0.0:8081 -> 8080
```

Richtig ist also:

- `http://localhost:8081`
  
- Swagger: `http://localhost:8081/swagger-ui.html`
  

## Prompt 6

##### Input

> package at.ac.tgm.backend.repository;
> import at.ac.tgm.backend.model.Analysis;
> import org.springframework.data.jpa.repository.JpaRepository;
> public interface AnalysisRepository extends JpaRepository<Analysis, Long> {
> }
> Passt das?

##### Output

Ja – **das passt so** 
Dein `AnalysisRepository` ist korrekt und genau so, wie es für ein einfaches CRUD-Repository in Spring Boot aussehen soll:

```java
package at.ac.tgm.backend.repository;

import at.ac.tgm.backend.model.Analysis;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnalysisRepository extends JpaRepository<Analysis, Long> {
}
```

###### Warum ist das okay?

- `Analysis` hat laut deiner DB-Definition **einen Primary Key (`a_id` vom Typ BIGINT)** → `JpaRepository<Analysis, Long>` ist richtig.
  
- Ein Repository in Spring Data braucht **keine Annotation (`@Repository`)**, weil Spring es automatisch findet.
