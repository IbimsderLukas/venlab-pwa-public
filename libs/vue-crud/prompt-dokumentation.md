# Prompt-Dokumentation - Vue CRUD

Dokumentation der verwendeten KI-Prompts während der Entwicklung.

---

## Prompt 1: CRUD Dialog Struktur

**Frage:**
Ich muss jetzt CRUD für meine Datatables implementieren. Wie mache ich am besten einen Dialog zum Erstellen und Bearbeiten? Soll der gleiche Dialog für beides sein oder zwei verschiedene?

**Antwort (Zusammenfassung):**
Ein Dialog für beides ist üblich - mit einer `isEditing` Flag unterscheiden. Beim Erstellen sind alle Felder leer, beim Bearbeiten werden die Daten vorausgefüllt. Titel und Button-Text ändern sich je nach Modus.

---

## Prompt 2: Felder nicht editierbar machen

**Frage:**
Laut Aufgabenstellung dürfen bestimmte Felder wie die ID nicht geändert werden beim Bearbeiten. Wie mache ich das in Vuetify?

**Antwort:**

```vue
<v-text-field
  v-model="editedItem.id"
  label="ID *"
  :disabled="isEditing"
  :bg-color="isEditing ? 'grey-lighten-3' : undefined"
  hint="Kann nicht geändert werden"
  persistent-hint
></v-text-field>
```

Bei `isEditing = true` ist das Feld disabled und grau hinterlegt.

---

## Prompt 3: Delete Bestätigung

**Frage:**
Ich brauch einen Bestätigungs-Dialog bevor was gelöscht wird. Wie mach ich das sauber?

**Antwort (Zusammenfassung):**
Separater v-dialog mit Warnung, "Abbrechen" und "Löschen" Buttons. Das zu löschende Item wird in einer ref gespeichert. Erst bei Bestätigung wird die delete-Funktion aufgerufen.

---

## Prompt 4: API Service erweitern

**Frage:**
Mein BoxService hat nur getAll und getById. Kannst du mir create, update und delete dazu schreiben?

**Antwort:**

```javascript
async create(box) {
  const response = await apiClient.post('/api/box', box)
  return response.data
}

async update(id, box) {
  const response = await apiClient.put(`/api/box/${id}`, box)
  return response.data
}

async delete(id) {
  await apiClient.delete(`/api/box/${id}`)
}
```

---

## Prompt 5: Fehlerbehandlung mit Validierung

**Frage:**
Das Backend wirft Fehler wenn ich ungültige Daten schicke. Wie zeige ich die Fehlermeldung vom Backend im Frontend an?

**Antwort (Zusammenfassung):**
Im catch-Block `error.response?.data?.message` auslesen. Falls vorhanden diese Nachricht anzeigen, sonst generische Fehlermeldung. Mit Snackbar oder Alert anzeigen.

---

## Prompt 6: Box und BoxPos kombinieren

**Frage:**
Die Aufgabe sagt Box und BoxPos sollen kombiniert angezeigt werden. Wie mache ich das am besten? Sollen die BoxPos als Unterliste bei jeder Box erscheinen?

**Antwort (Zusammenfassung):**
Vuetify v-data-table hat `show-expand` und `expanded-row` Slot. Jede Box kann aufgeklappt werden und zeigt dann die zugehörigen BoxPos-Einträge. Beide Datensets werden parallel geladen.

---

## Prompt 7: Backend Controller für CRUD

**Frage:**
Mein BoxController hat nur GET. Kannst du mir POST, PUT und DELETE Endpunkte dazu schreiben mit Validierung?

**Antwort:**

```java
@PostMapping
public Box create(@RequestBody Box box) {
    if (box.getId() == null || box.getId().isBlank()) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
            "Box ID ist ein Pflichtfeld");
    }
    if (repo.existsById(box.getId())) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, 
            "Box mit dieser ID existiert bereits");
    }
    return repo.save(box);
}

@PutMapping("/{id}")
public Box update(@PathVariable String id, @RequestBody Box box) {
    if (!repo.existsById(id)) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
            "Box nicht gefunden");
    }
    box.setId(id);
    return repo.save(box);
}

@DeleteMapping("/{id}")
public ResponseEntity<Void> delete(@PathVariable String id) {
    if (!repo.existsById(id)) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
            "Box nicht gefunden");
    }
    repo.deleteById(id);
    return ResponseEntity.noContent().build();
}
```

---

## Prompt 8: Sample mit zusammengesetztem Schlüssel

**Frage:**
Sample hat einen zusammengesetzten Primary Key (s_id und s_stamp). Wie mache ich die API-Calls dafür? Der URL wird ja komplizierter.

**Antwort (Zusammenfassung):**
URL-Pfad mit beiden Werten: `/api/sample/{sId}/{sStamp}`. Im Frontend encodeURIComponent für Sonderzeichen verwenden. Timestamp im ISO-Format übergeben.

---

## Prompt 9: Views anzeigen (sample_boxpos)

**Frage:**
Ich soll die sample_boxpos View bei Sample und Analysis anzeigen. Die Daten kommen verschachtelt vom Backend. Wie zeige ich das in der Tabelle an?

**Antwort (Zusammenfassung):**
Custom column template verwenden. Mit optional chaining auf verschachtelte Daten zugreifen: `item.boxPosView?.boxPos`. Falls null, Fallback anzeigen.

```vue
<template v-slot:item.boxPosView="{ item }">
  <v-chip v-if="item.boxPosView?.boxPos" size="small">
    {{ item.boxPosView.boxPos }}
  </v-chip>
  <span v-else>-</span>
</template>
```

---

## Prompt 10: Sortierung für indizierte Spalten

**Frage:**
Die Aufgabe sagt indizierte Spalten sollen sortierbar sein. Wie finde ich raus welche Spalten indiziert sind und wie mache ich nur die sortierbar?

**Antwort (Zusammenfassung):**
Indizes stehen in der restore.sql (z.B. `idx_sample_name`). In den headers nur bei diesen Spalten `sortable: true` setzen. Bei serverseitigem Sortieren den sort-Parameter an das Backend übergeben.

---

## Prompt 11: Überlegungen zur Backend-Filterung

**Frage:**
Die Aufgabe fragt nach Überlegungen zur Backend-Filterung. Was für Parameter müsste ich übergeben wenn ich das implementieren wprde?

**Antwort (Zusammenfassung):**
Filter-Parameter als Query-String: `?filter=field:operator:value`. Operatoren wie eq, gt, lt, gte, lte, like. Mehrere Filter kombinierbar. Im Backend mit Spring Data Specifications oder QueryDSL umsetzen.

---

## Verwendete Quellen

- Vuetify Data Tables: https://vuetifyjs.com/en/components/data-tables/
- Vue.js Reactivity: https://vuejs.org/guide/essentials/reactivity-fundamentals.html
- Spring Boot REST: https://spring.io/guides/gs/rest-service/
- Spring Data JPA: https://docs.spring.io/spring-data/jpa/docs/current/reference/html/
