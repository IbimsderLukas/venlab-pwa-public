<template>
  <div>
    <!-- Header mit Actions -->
    <v-row class="mb-4">
      <v-col>
        <h1 class="text-h5 font-weight-bold">Analysen</h1>
        <p class="text-subtitle-2 text-medium-emphasis">
          Verwaltung aller Analysedaten
        </p>
      </v-col>
      <v-col cols="auto">
        <v-btn color="primary" prepend-icon="mdi-plus" @click="openCreateDialog">
          Neue Analyse
        </v-btn>
      </v-col>
    </v-row>

    <!-- Data Table -->
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

      <v-data-table-server
        v-model:items-per-page="itemsPerPage"
        :headers="headers"
        :items="items"
        :items-length="totalItems"
        :loading="loading"
        :search="search"
        @update:options="loadData"
        class="elevation-0"
      >
        <!-- ID Column -->
        <template v-slot:item.id="{ item }">
          <v-chip size="small" color="primary" variant="tonal">
            {{ item.id }}
          </v-chip>
        </template>

        <!-- Sample ID Column -->
        <template v-slot:item.sampleId="{ item }">
          <v-chip v-if="item.sampleId" size="small" color="secondary" variant="outlined">
            {{ item.sampleId }}
          </v-chip>
          <span v-else>-</span>
        </template>

        <!-- Sample Timestamp Column -->
        <template v-slot:item.sampleStamp="{ item }">
          {{ formatDateTime(item.sampleStamp) }}
        </template>

        <!-- Sample Name (from associatedSample) -->
        <template v-slot:item.associatedSample.name="{ item }">
          {{ item.associatedSample?.name || '-' }}
        </template>

        <!-- Sample Weight Brutto (from associatedSample) -->
        <template v-slot:item.associatedSample.weightBrutto="{ item }">
          {{ formatNumber(item.associatedSample?.weightBrutto) }}
        </template>

        <!-- Sample Weight Net (from associatedSample) -->
        <template v-slot:item.associatedSample.weightNet="{ item }">
          {{ formatNumber(item.associatedSample?.weightNet) }}
        </template>

        <!-- Box Position (from associatedSample.boxPosView) -->
        <template v-slot:item.associatedSample.boxPosView="{ item }">
          <v-chip v-if="item.associatedSample?.boxPosView?.boxPos" size="small" color="info" variant="outlined">
            {{ item.associatedSample.boxPosView.boxPos }}
          </v-chip>
          <span v-else>-</span>
        </template>

        <!-- Date In - SORTIERBAR (indiziert: idx_analysis_date_in) -->
        <template v-slot:item.dateIn="{ item }">
          {{ formatDateTime(item.dateIn) }}
        </template>

        <!-- Date Out -->
        <template v-slot:item.dateOut="{ item }">
          {{ formatDateTime(item.dateOut) }}
        </template>

        <!-- Flags - SORTIERBAR (indiziert: idx_analysis_a_flags) -->
        <template v-slot:item.flags="{ item }">
          <v-chip v-if="item.flags" size="small" :color="getFlagColor(item.flags)" variant="outlined">
            {{ item.flags }}
          </v-chip>
          <span v-else>-</span>
        </template>

        <!-- Numeric Columns -->
        <template v-slot:item.pol="{ item }">
          {{ formatNumber(item.pol) }}
        </template>

        <!-- Actions -->
        <template v-slot:item.actions="{ item }">
          <v-btn icon="mdi-eye" variant="text" size="small" @click="viewItem(item)"></v-btn>
          <v-btn icon="mdi-pencil" variant="text" size="small" @click="editItem(item)"></v-btn>
          <v-btn icon="mdi-delete" variant="text" size="small" color="error" @click="confirmDelete(item)"></v-btn>
        </template>
      </v-data-table-server>
    </v-card>

    <!-- Create/Edit Dialog -->
    <v-dialog v-model="dialog" max-width="900" persistent>
      <v-card>
        <v-card-title class="d-flex align-center">
          <v-icon :icon="isEditing ? 'mdi-pencil' : 'mdi-plus'" class="mr-2"></v-icon>
          {{ isEditing ? 'Analyse bearbeiten' : 'Neue Analyse erstellen' }}
        </v-card-title>
        <v-divider></v-divider>
        <v-card-text>
          <v-form ref="form" v-model="formValid">
            <!-- Nicht editierbare Felder (grau hinterlegt) -->
            <v-alert type="info" variant="tonal" density="compact" class="mb-4">
              <strong>Hinweis:</strong> Grau hinterlegte Felder können nicht geändert werden.
            </v-alert>

            <v-row>
              <v-col cols="12" md="4">
                <v-text-field
                  v-model.number="editedItem.id"
                  label="ID *"
                  type="number"
                  :rules="[rules.required]"
                  :disabled="isEditing"
                  :bg-color="isEditing ? 'grey-lighten-3' : undefined"
                ></v-text-field>
              </v-col>
              <v-col cols="12" md="4">
                <v-text-field
                  v-model="editedItem.sampleId"
                  label="Sample ID"
                  disabled
                  bg-color="grey-lighten-3"
                  hint="Nicht editierbar"
                  persistent-hint
                ></v-text-field>
              </v-col>
              <v-col cols="12" md="4">
                <v-text-field
                  v-model="editedItem.sampleStamp"
                  label="Sample Timestamp"
                  disabled
                  bg-color="grey-lighten-3"
                  hint="Nicht editierbar"
                  persistent-hint
                ></v-text-field>
              </v-col>
            </v-row>

            <!-- Sample-Informationen (Read-only) -->
            <v-card variant="outlined" class="mb-4 pa-3" v-if="isEditing && editedItem.associatedSample">
              <p class="text-subtitle-2 font-weight-bold mb-2">Sample-Informationen (nicht editierbar)</p>
              <v-row>
                <v-col cols="6" md="3">
                  <v-text-field
                    :model-value="editedItem.associatedSample?.name || '-'"
                    label="Name"
                    disabled
                    bg-color="grey-lighten-3"
                    density="compact"
                  ></v-text-field>
                </v-col>
                <v-col cols="6" md="3">
                  <v-text-field
                    :model-value="formatNumber(editedItem.associatedSample?.weightBrutto)"
                    label="Gewicht Brutto"
                    disabled
                    bg-color="grey-lighten-3"
                    density="compact"
                  ></v-text-field>
                </v-col>
                <v-col cols="6" md="3">
                  <v-text-field
                    :model-value="formatNumber(editedItem.associatedSample?.weightNet)"
                    label="Gewicht Netto"
                    disabled
                    bg-color="grey-lighten-3"
                    density="compact"
                  ></v-text-field>
                </v-col>
                <v-col cols="6" md="3">
                  <v-text-field
                    :model-value="editedItem.associatedSample?.boxPosView?.boxPos || '-'"
                    label="Box Position"
                    disabled
                    bg-color="grey-lighten-3"
                    density="compact"
                  ></v-text-field>
                </v-col>
              </v-row>
            </v-card>

            <v-divider class="my-4"></v-divider>
            <p class="text-subtitle-2 font-weight-bold mb-2">Editierbare Messwerte</p>

            <v-row>
              <v-col cols="6" md="4">
                <v-text-field
                  v-model.number="editedItem.pol"
                  label="POL"
                  type="number"
                  step="0.01"
                ></v-text-field>
              </v-col>
              <v-col cols="6" md="4">
                <v-text-field
                  v-model.number="editedItem.nat"
                  label="NAT"
                  type="number"
                  step="0.01"
                ></v-text-field>
              </v-col>
              <v-col cols="6" md="4">
                <v-text-field
                  v-model.number="editedItem.kal"
                  label="KAL"
                  type="number"
                  step="0.01"
                ></v-text-field>
              </v-col>
              <v-col cols="6" md="4">
                <v-text-field
                  v-model.number="editedItem.an"
                  label="AN"
                  type="number"
                  step="0.01"
                ></v-text-field>
              </v-col>
              <v-col cols="6" md="4">
                <v-text-field
                  v-model.number="editedItem.glu"
                  label="GLU"
                  type="number"
                  step="0.01"
                ></v-text-field>
              </v-col>
              <v-col cols="6" md="4">
                <v-text-field
                  v-model.number="editedItem.dry"
                  label="DRY"
                  type="number"
                  step="0.01"
                ></v-text-field>
              </v-col>
            </v-row>

            <v-divider class="my-4"></v-divider>
            <p class="text-subtitle-2 font-weight-bold mb-2">Editierbare Gewichte & Zeiten</p>

            <v-row>
              <v-col cols="6" md="3">
                <v-text-field
                  v-model.number="editedItem.weightMeasured"
                  label="Gemessen"
                  type="number"
                  step="0.01"
                ></v-text-field>
              </v-col>
              <v-col cols="6" md="3">
                <v-text-field
                  v-model.number="editedItem.weightNormal"
                  label="Normal"
                  type="number"
                  step="0.01"
                ></v-text-field>
              </v-col>
              <v-col cols="6" md="3">
                <v-text-field
                  v-model.number="editedItem.weightCurrent"
                  label="Aktuell"
                  type="number"
                  step="0.01"
                ></v-text-field>
              </v-col>
              <v-col cols="6" md="3">
                <v-text-field
                  v-model.number="editedItem.weightDifference"
                  label="Differenz"
                  type="number"
                  step="0.01"
                ></v-text-field>
              </v-col>
            </v-row>

            <v-row>
              <v-col cols="12" md="4">
                <v-text-field
                  v-model.number="editedItem.density"
                  label="Dichte"
                  type="number"
                  step="0.001"
                ></v-text-field>
              </v-col>
              <v-col cols="12" md="4">
                <v-text-field
                  v-model="editedItem.dateIn"
                  label="Datum Ein"
                  type="datetime-local"
                ></v-text-field>
              </v-col>
              <v-col cols="12" md="4">
                <v-text-field
                  v-model="editedItem.dateOut"
                  label="Datum Aus"
                  type="datetime-local"
                ></v-text-field>
              </v-col>
            </v-row>

            <v-row>
              <v-col cols="12" md="4">
                <v-text-field
                  v-model="editedItem.flags"
                  label="Flags"
                ></v-text-field>
              </v-col>
              <v-col cols="12" md="4">
                <v-text-field
                  v-model.number="editedItem.lane"
                  label="Lane"
                  type="number"
                ></v-text-field>
              </v-col>
            </v-row>

            <v-row>
              <v-col cols="12">
                <v-textarea
                  v-model="editedItem.comment"
                  label="Kommentar"
                  rows="2"
                ></v-textarea>
              </v-col>
            </v-row>
          </v-form>
        </v-card-text>
        <v-divider></v-divider>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn variant="text" @click="closeDialog">Abbrechen</v-btn>
          <v-btn color="primary" variant="flat" @click="saveItem" :loading="saving" :disabled="!formValid">
            Speichern
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- View Dialog -->
    <v-dialog v-model="viewDialog" max-width="800">
      <v-card>
        <v-card-title class="d-flex align-center">
          <v-icon icon="mdi-eye" class="mr-2"></v-icon>
          Analyse Details
        </v-card-title>
        <v-divider></v-divider>
        <v-card-text>
          <v-table density="compact">
            <tbody>
              <tr v-for="(value, key) in flattenedViewItem" :key="key">
                <td class="font-weight-bold text-medium-emphasis" style="width: 250px;">{{ key }}</td>
                <td>{{ value ?? '-' }}</td>
              </tr>
            </tbody>
          </v-table>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn variant="text" @click="viewDialog = false">Schließen</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Delete Confirmation Dialog -->
    <v-dialog v-model="deleteDialog" max-width="400">
      <v-card>
        <v-card-title class="text-h6">
          <v-icon icon="mdi-alert" color="error" class="mr-2"></v-icon>
          Löschen bestätigen
        </v-card-title>
        <v-card-text>
          Möchten Sie die Analyse mit ID <strong>{{ itemToDelete?.id }}</strong> wirklich löschen?
          Diese Aktion kann nicht rückgängig gemacht werden.
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn variant="text" @click="deleteDialog = false">Abbrechen</v-btn>
          <v-btn color="error" variant="flat" @click="deleteItem" :loading="deleting">
            Löschen
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup>
import { ref, computed, inject } from 'vue'
import { AnalysisService } from '@/api'

const showNotification = inject('showNotification')

// Table state
const search = ref('')
const loading = ref(false)
const items = ref([])
const totalItems = ref(0)
const itemsPerPage = ref(10)

// Dialog state
const dialog = ref(false)
const viewDialog = ref(false)
const deleteDialog = ref(false)
const isEditing = ref(false)
const formValid = ref(false)
const saving = ref(false)
const deleting = ref(false)

const editedItem = ref({})
const viewedItem = ref({})
const itemToDelete = ref(null)

const defaultItem = {
  id: null,
  sampleId: '',
  sampleStamp: '',
  pol: null,
  nat: null,
  kal: null,
  an: null,
  glu: null,
  dry: null,
  dateIn: '',
  dateOut: '',
  weightMeasured: null,
  weightNormal: null,
  weightCurrent: null,
  weightDifference: null,
  density: null,
  flags: '',
  lane: null,
  comment: ''
}

const rules = {
  required: v => !!v || v === 0 || 'Pflichtfeld'
}

// Headers mit Sortierung für indizierte Spalten
const headers = [
  { title: 'ID', key: 'id', sortable: true, width: '70px' },
  { title: 'Sample ID', key: 'sampleId', sortable: true },
  { title: 'Sample Zeit', key: 'sampleStamp', sortable: true },
  { title: 'Name', key: 'associatedSample.name', sortable: false },
  { title: 'Brutto (g)', key: 'associatedSample.weightBrutto', sortable: false },
  { title: 'Netto (g)', key: 'associatedSample.weightNet', sortable: false },
  { title: 'Box', key: 'associatedSample.boxPosView', sortable: false },
  { title: 'POL', key: 'pol', sortable: false, width: '70px' },
  { title: 'Datum Ein', key: 'dateIn', sortable: true },  // indiziert
  { title: 'Flags', key: 'flags', sortable: true },  // indiziert
  { title: 'Aktionen', key: 'actions', sortable: false, width: '120px' }
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

const formatNumber = (num) => {
  if (num === null || num === undefined) return '-'
  return Number(num).toFixed(2)
}

const getFlagColor = (flags) => {
  if (!flags) return 'grey'
  if (flags.includes('E') || flags.includes('ERR')) return 'error'
  if (flags.includes('W') || flags.includes('WARN')) return 'warning'
  return 'success'
}

// Flatten nested objects for view dialog
const flattenedViewItem = computed(() => {
  const flat = {}
  const flatten = (obj, prefix = '') => {
    for (const [key, value] of Object.entries(obj || {})) {
      const newKey = prefix ? `${prefix}.${key}` : key
      if (value && typeof value === 'object' && !Array.isArray(value) && !(value instanceof Date)) {
        flatten(value, newKey)
      } else {
        flat[newKey] = value
      }
    }
  }
  flatten(viewedItem.value)
  return flat
})

// Data loading
const loadData = async (options) => {
  loading.value = true
  try {
    const page = options?.page ? options.page - 1 : 0
    const size = options?.itemsPerPage || itemsPerPage.value
    const sortBy = options?.sortBy?.[0]?.key || 'id'
    const sortOrder = options?.sortBy?.[0]?.order || 'asc'
    
    const response = await AnalysisService.getAll(page, size, `${sortBy},${sortOrder}`)
    
    items.value = response.content || []
    totalItems.value = response.totalElements || 0
  } catch (error) {
    console.error('Fehler beim Laden:', error)
    showNotification('Fehler beim Laden der Daten', 'error')
    items.value = []
    totalItems.value = 0
  } finally {
    loading.value = false
  }
}

// CRUD Operations
const openCreateDialog = () => {
  isEditing.value = false
  editedItem.value = { ...defaultItem }
  dialog.value = true
}

const editItem = (item) => {
  isEditing.value = true
  editedItem.value = { ...item }
  // Convert datetime for input field
  if (editedItem.value.sampleStamp) {
    editedItem.value.sampleStamp = editedItem.value.sampleStamp.slice(0, 16)
  }
  if (editedItem.value.dateIn) {
    editedItem.value.dateIn = editedItem.value.dateIn.slice(0, 16)
  }
  if (editedItem.value.dateOut) {
    editedItem.value.dateOut = editedItem.value.dateOut.slice(0, 16)
  }
  dialog.value = true
}

const viewItem = (item) => {
  viewedItem.value = { ...item }
  viewDialog.value = true
}

const closeDialog = () => {
  dialog.value = false
  editedItem.value = { ...defaultItem }
}

const saveItem = async () => {
  saving.value = true
  try {
    const data = { ...editedItem.value }
    
    // Remove associatedSample from data (read-only)
    delete data.associatedSample
    
    // Convert datetime strings to ISO format
    if (data.sampleStamp && typeof data.sampleStamp === 'string') {
      data.sampleStamp = new Date(data.sampleStamp).toISOString()
    }
    if (data.dateIn && typeof data.dateIn === 'string') {
      data.dateIn = new Date(data.dateIn).toISOString()
    }
    if (data.dateOut && typeof data.dateOut === 'string') {
      data.dateOut = new Date(data.dateOut).toISOString()
    }

    if (isEditing.value) {
      await AnalysisService.update(data.id, data)
      showNotification('Analyse erfolgreich aktualisiert', 'success')
    } else {
      await AnalysisService.create(data)
      showNotification('Analyse erfolgreich erstellt', 'success')
    }
    
    closeDialog()
    loadData({ page: 1, itemsPerPage: itemsPerPage.value })
  } catch (error) {
    console.error('Fehler beim Speichern:', error)
    const message = error.response?.data?.message || 'Fehler beim Speichern'
    showNotification(message, 'error')
  } finally {
    saving.value = false
  }
}

const confirmDelete = (item) => {
  itemToDelete.value = item
  deleteDialog.value = true
}

const deleteItem = async () => {
  deleting.value = true
  try {
    await AnalysisService.delete(itemToDelete.value.id)
    showNotification('Analyse erfolgreich gelöscht', 'success')
    deleteDialog.value = false
    itemToDelete.value = null
    loadData({ page: 1, itemsPerPage: itemsPerPage.value })
  } catch (error) {
    console.error('Fehler beim Löschen:', error)
    showNotification('Fehler beim Löschen', 'error')
  } finally {
    deleting.value = false
  }
}
</script>

<style scoped>
.max-width-300 {
  max-width: 300px;
}
</style>
