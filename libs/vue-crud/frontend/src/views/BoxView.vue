<template>
  <div>
    <!-- Header mit Actions -->
    <v-row class="mb-4">
      <v-col>
        <h1 class="text-h5 font-weight-bold">Boxen & Positionen</h1>
        <p class="text-subtitle-2 text-medium-emphasis">
          Verwaltung von Lagerboxen und ihren Positionen
        </p>
      </v-col>
      <v-col cols="auto">
        <v-btn color="primary" prepend-icon="mdi-plus" @click="openCreateBoxDialog" class="mr-2">
          Neue Box
        </v-btn>
        <v-btn color="secondary" prepend-icon="mdi-plus" @click="openCreateBoxPosDialog">
          Neue Position
        </v-btn>
      </v-col>
    </v-row>

    <!-- Data Table mit Expand -->
    <v-card>
      <v-card-title class="d-flex align-center">
        <v-text-field
          v-model="search"
          prepend-inner-icon="mdi-magnify"
          label="Suchen"
          single-line
          hide-details
          density="compact"
          class="max-width-300"
        ></v-text-field>
        <v-spacer></v-spacer>
        <v-btn icon="mdi-refresh" variant="text" @click="loadData" :loading="loading"></v-btn>
      </v-card-title>

      <v-data-table
        v-model:expanded="expanded"
        :headers="headers"
        :items="boxes"
        :loading="loading"
        :search="search"
        item-value="id"
        show-expand
        class="elevation-0"
      >
        <!-- ID Column -->
        <template v-slot:item.id="{ item }">
          <v-chip size="small" color="accent" variant="tonal">
            {{ item.id }}
          </v-chip>
        </template>

        <!-- Type Column -->
        <template v-slot:item.type="{ item }">
          <v-chip size="small" :color="getTypeColor(item.type)" variant="outlined">
            Typ {{ item.type ?? '-' }}
          </v-chip>
        </template>

        <!-- Max Number Column -->
        <template v-slot:item.maxNumber="{ item }">
          <span>{{ item.maxNumber ?? '-' }}</span>
          <span class="text-medium-emphasis ml-1">({{ getBoxPosCount(item.id) }} belegt)</span>
        </template>

        <!-- Actions -->
        <template v-slot:item.actions="{ item }">
          <v-btn icon="mdi-pencil" variant="text" size="small" @click.stop="editBox(item)"></v-btn>
          <v-btn icon="mdi-delete" variant="text" size="small" color="error" @click.stop="confirmDeleteBox(item)"></v-btn>
        </template>

        <!-- Expanded Row: BoxPos -->
        <template v-slot:expanded-row="{ columns, item }">
          <tr>
            <td :colspan="columns.length" class="pa-4 bg-grey-lighten-4">
              <div class="d-flex align-center mb-2">
                <span class="text-subtitle-2 font-weight-bold">Positionen in {{ item.id }}</span>
                <v-spacer></v-spacer>
                <v-btn size="small" color="secondary" variant="tonal" @click="openCreateBoxPosDialogForBox(item.id)">
                  <v-icon start>mdi-plus</v-icon>
                  Position hinzufügen
                </v-btn>
              </div>
              
              <v-table density="compact" v-if="getBoxPositions(item.id).length > 0">
                <thead>
                  <tr>
                    <th>Position</th>
                    <th>Sample ID</th>
                    <th>Sample Timestamp</th>
                    <th>Exportiert</th>
                    <th width="100">Aktionen</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="pos in getBoxPositions(item.id)" :key="`${pos.boxId}-${pos.posId}`">
                    <td>
                      <v-chip size="small" color="info" variant="outlined">
                        Pos {{ pos.posId }}
                      </v-chip>
                    </td>
                    <td>{{ pos.sampleId || '-' }}</td>
                    <td>{{ formatDateTime(pos.sampleStamp) }}</td>
                    <td>{{ formatDateTime(pos.dateExported) }}</td>
                    <td>
                      <v-btn icon="mdi-pencil" variant="text" size="x-small" @click="editBoxPos(pos)"></v-btn>
                      <v-btn icon="mdi-delete" variant="text" size="x-small" color="error" @click="confirmDeleteBoxPos(pos)"></v-btn>
                    </td>
                  </tr>
                </tbody>
              </v-table>
              <div v-else class="text-medium-emphasis text-center pa-4">
                Keine Positionen vorhanden
              </div>
            </td>
          </tr>
        </template>
      </v-data-table>
    </v-card>

    <!-- Box Create/Edit Dialog -->
    <v-dialog v-model="boxDialog" max-width="500" persistent>
      <v-card>
        <v-card-title>
          {{ isEditingBox ? 'Box bearbeiten' : 'Neue Box erstellen' }}
        </v-card-title>
        <v-divider></v-divider>
        <v-card-text>
          <v-form ref="boxForm" v-model="boxFormValid">
            <v-text-field
              v-model="editedBox.id"
              label="Box ID *"
              :rules="[rules.required, rules.maxLength4]"
              :disabled="isEditingBox"
              :hint="isEditingBox ? 'Kann nicht geändert werden' : 'Max. 4 Zeichen'"
              persistent-hint
              counter="4"
            ></v-text-field>
            <v-text-field
              v-model="editedBox.name"
              label="Name"
              class="mt-4"
            ></v-text-field>
            <v-text-field
              v-model.number="editedBox.maxNumber"
              label="Max. Positionen"
              type="number"
              :rules="[rules.minMax]"
              class="mt-4"
            ></v-text-field>
            <v-select
              v-model="editedBox.type"
              :items="[1, 2, 3, 4, 5, 6, 7, 8, 9]"
              label="Typ"
              class="mt-4"
            ></v-select>
            <v-textarea
              v-model="editedBox.comment"
              label="Kommentar"
              rows="2"
              class="mt-4"
            ></v-textarea>
          </v-form>
        </v-card-text>
        <v-divider></v-divider>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn variant="text" @click="boxDialog = false">Abbrechen</v-btn>
          <v-btn color="primary" @click="saveBox" :loading="saving" :disabled="!boxFormValid">
            Speichern
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- BoxPos Create/Edit Dialog -->
    <v-dialog v-model="boxPosDialog" max-width="500" persistent>
      <v-card>
        <v-card-title>
          {{ isEditingBoxPos ? 'Position bearbeiten' : 'Neue Position erstellen' }}
        </v-card-title>
        <v-divider></v-divider>
        <v-card-text>
          <v-form ref="boxPosForm" v-model="boxPosFormValid">
            <v-select
              v-model="editedBoxPos.boxId"
              :items="boxes.map(b => b.id)"
              label="Box ID *"
              :rules="[rules.required]"
              :disabled="isEditingBoxPos"
              :hint="isEditingBoxPos ? 'Kann nicht geändert werden' : ''"
              persistent-hint
            ></v-select>
            <v-text-field
              v-model.number="editedBoxPos.posId"
              label="Position ID *"
              type="number"
              :rules="[rules.required]"
              :disabled="isEditingBoxPos"
              :hint="isEditingBoxPos ? 'Kann nicht geändert werden' : ''"
              persistent-hint
              class="mt-4"
            ></v-text-field>
            <v-text-field
              v-model="editedBoxPos.sampleId"
              label="Sample ID"
              class="mt-4"
            ></v-text-field>
            <v-text-field
              v-model="editedBoxPos.sampleStamp"
              label="Sample Timestamp"
              type="datetime-local"
              class="mt-4"
            ></v-text-field>
          </v-form>
        </v-card-text>
        <v-divider></v-divider>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn variant="text" @click="boxPosDialog = false">Abbrechen</v-btn>
          <v-btn color="primary" @click="saveBoxPos" :loading="saving" :disabled="!boxPosFormValid">
            Speichern
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Delete Box Confirmation -->
    <v-dialog v-model="deleteBoxDialog" max-width="400">
      <v-card>
        <v-card-title class="text-h6">
          <v-icon icon="mdi-alert" color="error" class="mr-2"></v-icon>
          Box löschen
        </v-card-title>
        <v-card-text>
          Möchten Sie die Box <strong>{{ boxToDelete?.id }}</strong> wirklich löschen?
          <v-alert v-if="getBoxPosCount(boxToDelete?.id) > 0" type="warning" density="compact" class="mt-2">
            Diese Box enthält {{ getBoxPosCount(boxToDelete?.id) }} Positionen!
          </v-alert>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn variant="text" @click="deleteBoxDialog = false">Abbrechen</v-btn>
          <v-btn color="error" @click="deleteBox" :loading="deleting">Löschen</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Delete BoxPos Confirmation -->
    <v-dialog v-model="deleteBoxPosDialog" max-width="400">
      <v-card>
        <v-card-title class="text-h6">
          <v-icon icon="mdi-alert" color="error" class="mr-2"></v-icon>
          Position löschen
        </v-card-title>
        <v-card-text>
          Möchten Sie die Position <strong>{{ boxPosToDelete?.boxId }}/{{ boxPosToDelete?.posId }}</strong> wirklich löschen?
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn variant="text" @click="deleteBoxPosDialog = false">Abbrechen</v-btn>
          <v-btn color="error" @click="deleteBoxPos" :loading="deleting">Löschen</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, inject } from 'vue'
import { BoxService, BoxPosService } from '@/api'

const showNotification = inject('showNotification')

// State
const loading = ref(false)
const saving = ref(false)
const deleting = ref(false)
const search = ref('')
const expanded = ref([])

const boxes = ref([])
const boxPositions = ref([])

// Box Dialog
const boxDialog = ref(false)
const isEditingBox = ref(false)
const boxFormValid = ref(false)
const editedBox = ref({})
const defaultBox = { id: '', name: '', maxNumber: 40, type: 1, comment: '' }

// BoxPos Dialog
const boxPosDialog = ref(false)
const isEditingBoxPos = ref(false)
const boxPosFormValid = ref(false)
const editedBoxPos = ref({})
const defaultBoxPos = { boxId: '', posId: null, sampleId: '', sampleStamp: '' }

// Delete Dialogs
const deleteBoxDialog = ref(false)
const boxToDelete = ref(null)
const deleteBoxPosDialog = ref(false)
const boxPosToDelete = ref(null)

const rules = {
  required: v => !!v || v === 0 || 'Pflichtfeld',
  maxLength4: v => !v || v.length <= 4 || 'Max. 4 Zeichen',
  minMax: v => !v || (v > 0 && v < 1000) || 'Muss zwischen 1 und 999 sein'
}

const headers = [
  { title: '', key: 'data-table-expand' },
  { title: 'ID', key: 'id', sortable: true },
  { title: 'Name', key: 'name', sortable: true },
  { title: 'Max. Positionen', key: 'maxNumber', sortable: true },
  { title: 'Typ', key: 'type', sortable: true },
  { title: 'Kommentar', key: 'comment', sortable: false },
  { title: 'Aktionen', key: 'actions', sortable: false, width: '100px' }
]

// Helper functions
const formatDateTime = (dateStr) => {
  if (!dateStr) return '-'
  try {
    return new Date(dateStr).toLocaleString('de-AT')
  } catch {
    return dateStr
  }
}

const getTypeColor = (type) => {
  const colors = ['grey', 'primary', 'secondary', 'accent', 'info', 'success', 'warning', 'error', 'purple']
  return colors[type] || 'grey'
}

const getBoxPositions = (boxId) => {
  return boxPositions.value.filter(p => p.boxId === boxId)
}

const getBoxPosCount = (boxId) => {
  if (!boxId) return 0
  return boxPositions.value.filter(p => p.boxId === boxId).length
}

// Data loading
const loadData = async () => {
  loading.value = true
  try {
    const [boxData, posData] = await Promise.all([
      BoxService.getAll(),
      BoxPosService.getAll()
    ])
    boxes.value = boxData
    boxPositions.value = posData
  } catch (error) {
    console.error('Fehler beim Laden:', error)
    showNotification('Fehler beim Laden der Daten', 'error')
  } finally {
    loading.value = false
  }
}

// Box CRUD
const openCreateBoxDialog = () => {
  isEditingBox.value = false
  editedBox.value = { ...defaultBox }
  boxDialog.value = true
}

const editBox = (item) => {
  isEditingBox.value = true
  editedBox.value = { ...item }
  boxDialog.value = true
}

const saveBox = async () => {
  saving.value = true
  try {
    if (isEditingBox.value) {
      await BoxService.update(editedBox.value.id, editedBox.value)
      showNotification('Box erfolgreich aktualisiert', 'success')
    } else {
      await BoxService.create(editedBox.value)
      showNotification('Box erfolgreich erstellt', 'success')
    }
    boxDialog.value = false
    loadData()
  } catch (error) {
    const message = error.response?.data?.message || 'Fehler beim Speichern'
    showNotification(message, 'error')
  } finally {
    saving.value = false
  }
}

const confirmDeleteBox = (item) => {
  boxToDelete.value = item
  deleteBoxDialog.value = true
}

const deleteBox = async () => {
  deleting.value = true
  try {
    await BoxService.delete(boxToDelete.value.id)
    showNotification('Box erfolgreich gelöscht', 'success')
    deleteBoxDialog.value = false
    loadData()
  } catch (error) {
    const message = error.response?.data?.message || 'Fehler beim Löschen'
    showNotification(message, 'error')
  } finally {
    deleting.value = false
  }
}

// BoxPos CRUD
const openCreateBoxPosDialog = () => {
  isEditingBoxPos.value = false
  editedBoxPos.value = { ...defaultBoxPos }
  boxPosDialog.value = true
}

const openCreateBoxPosDialogForBox = (boxId) => {
  isEditingBoxPos.value = false
  editedBoxPos.value = { ...defaultBoxPos, boxId }
  boxPosDialog.value = true
}

const editBoxPos = (item) => {
  isEditingBoxPos.value = true
  editedBoxPos.value = { ...item }
  if (editedBoxPos.value.sampleStamp) {
    editedBoxPos.value.sampleStamp = editedBoxPos.value.sampleStamp.slice(0, 16)
  }
  boxPosDialog.value = true
}

const saveBoxPos = async () => {
  saving.value = true
  try {
    const data = { ...editedBoxPos.value }
    if (data.sampleStamp && typeof data.sampleStamp === 'string' && data.sampleStamp.length > 0) {
      data.sampleStamp = new Date(data.sampleStamp).toISOString()
    } else {
      data.sampleStamp = null
    }

    if (isEditingBoxPos.value) {
      await BoxPosService.update(editedBoxPos.value.boxId, editedBoxPos.value.posId, data)
      showNotification('Position erfolgreich aktualisiert', 'success')
    } else {
      await BoxPosService.create(data)
      showNotification('Position erfolgreich erstellt', 'success')
    }
    boxPosDialog.value = false
    loadData()
  } catch (error) {
    const message = error.response?.data?.message || 'Fehler beim Speichern'
    showNotification(message, 'error')
  } finally {
    saving.value = false
  }
}

const confirmDeleteBoxPos = (item) => {
  boxPosToDelete.value = item
  deleteBoxPosDialog.value = true
}

const deleteBoxPos = async () => {
  deleting.value = true
  try {
    await BoxPosService.delete(boxPosToDelete.value.boxId, boxPosToDelete.value.posId)
    showNotification('Position erfolgreich gelöscht', 'success')
    deleteBoxPosDialog.value = false
    loadData()
  } catch (error) {
    const message = error.response?.data?.message || 'Fehler beim Löschen'
    showNotification(message, 'error')
  } finally {
    deleting.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.max-width-300 {
  max-width: 300px;
}
</style>
