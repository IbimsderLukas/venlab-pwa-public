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

        <!-- Date Columns -->
        <template v-slot:item.dateIn="{ item }">
          {{ formatDateTime(item.dateIn) }}
        </template>
        <template v-slot:item.dateOut="{ item }">
          {{ formatDateTime(item.dateOut) }}
        </template>

        <!-- Numeric Columns -->
        <template v-slot:item.pol="{ item }">
          {{ formatNumber(item.pol) }}
        </template>
        <template v-slot:item.nat="{ item }">
          {{ formatNumber(item.nat) }}
        </template>
        <template v-slot:item.kal="{ item }">
          {{ formatNumber(item.kal) }}
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
    <v-dialog v-model="dialog" max-width="800" persistent>
      <v-card>
        <v-card-title class="d-flex align-center">
          <v-icon :icon="isEditing ? 'mdi-pencil' : 'mdi-plus'" class="mr-2"></v-icon>
          {{ isEditing ? 'Analyse bearbeiten' : 'Neue Analyse erstellen' }}
        </v-card-title>
        <v-divider></v-divider>
        <v-card-text>
          <v-form ref="form" v-model="formValid">
            <v-row>
              <v-col cols="12" md="6">
                <v-text-field
                  v-model.number="editedItem.id"
                  label="ID *"
                  type="number"
                  :rules="[rules.required]"
                  :disabled="isEditing"
                ></v-text-field>
              </v-col>
              <v-col cols="12" md="6">
                <v-text-field
                  v-model="editedItem.sampleId"
                  label="Sample ID"
                ></v-text-field>
              </v-col>
              <v-col cols="12" md="6">
                <v-text-field
                  v-model="editedItem.sampleStamp"
                  label="Sample Timestamp"
                  type="datetime-local"
                ></v-text-field>
              </v-col>
              <v-col cols="12" md="6">
                <v-text-field
                  v-model.number="editedItem.lane"
                  label="Lane"
                  type="number"
                ></v-text-field>
              </v-col>
            </v-row>

            <v-divider class="my-4"></v-divider>
            <p class="text-subtitle-2 text-medium-emphasis mb-2">Messwerte</p>

            <v-row>
              <v-col cols="6" md="4">
                <v-text-field
                  v-model.number="editedItem.pol"
                  label="POL"
                  type="number"
                  step="0.001"
                ></v-text-field>
              </v-col>
              <v-col cols="6" md="4">
                <v-text-field
                  v-model.number="editedItem.nat"
                  label="NAT"
                  type="number"
                  step="0.001"
                ></v-text-field>
              </v-col>
              <v-col cols="6" md="4">
                <v-text-field
                  v-model.number="editedItem.kal"
                  label="KAL"
                  type="number"
                  step="0.001"
                ></v-text-field>
              </v-col>
              <v-col cols="6" md="4">
                <v-text-field
                  v-model.number="editedItem.an"
                  label="AN"
                  type="number"
                  step="0.001"
                ></v-text-field>
              </v-col>
              <v-col cols="6" md="4">
                <v-text-field
                  v-model.number="editedItem.glu"
                  label="GLU"
                  type="number"
                  step="0.001"
                ></v-text-field>
              </v-col>
              <v-col cols="6" md="4">
                <v-text-field
                  v-model.number="editedItem.dry"
                  label="DRY"
                  type="number"
                  step="0.001"
                ></v-text-field>
              </v-col>
            </v-row>

            <v-divider class="my-4"></v-divider>
            <p class="text-subtitle-2 text-medium-emphasis mb-2">Gewichte</p>

            <v-row>
              <v-col cols="6" md="3">
                <v-text-field
                  v-model.number="editedItem.weightMeasured"
                  label="Gemessen"
                  type="number"
                  step="0.001"
                ></v-text-field>
              </v-col>
              <v-col cols="6" md="3">
                <v-text-field
                  v-model.number="editedItem.weightNormal"
                  label="Normal"
                  type="number"
                  step="0.001"
                ></v-text-field>
              </v-col>
              <v-col cols="6" md="3">
                <v-text-field
                  v-model.number="editedItem.weightCurrent"
                  label="Aktuell"
                  type="number"
                  step="0.001"
                ></v-text-field>
              </v-col>
              <v-col cols="6" md="3">
                <v-text-field
                  v-model.number="editedItem.weightDifference"
                  label="Differenz"
                  type="number"
                  step="0.001"
                ></v-text-field>
              </v-col>
            </v-row>

            <v-row>
              <v-col cols="12" md="6">
                <v-text-field
                  v-model.number="editedItem.density"
                  label="Dichte"
                  type="number"
                  step="0.001"
                ></v-text-field>
              </v-col>
              <v-col cols="12" md="6">
                <v-text-field
                  v-model="editedItem.flags"
                  label="Flags"
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
    <v-dialog v-model="viewDialog" max-width="700">
      <v-card>
        <v-card-title class="d-flex align-center">
          <v-icon icon="mdi-eye" class="mr-2"></v-icon>
          Analyse Details
        </v-card-title>
        <v-divider></v-divider>
        <v-card-text>
          <v-table density="compact">
            <tbody>
              <tr v-for="(value, key) in viewedItem" :key="key">
                <td class="font-weight-bold text-medium-emphasis" style="width: 200px;">{{ key }}</td>
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
import { ref, inject } from 'vue'
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

const headers = [
  { title: 'ID', key: 'id', sortable: true, width: '80px' },
  { title: 'Sample ID', key: 'sampleId', sortable: true },
  { title: 'POL', key: 'pol', sortable: true },
  { title: 'NAT', key: 'nat', sortable: true },
  { title: 'KAL', key: 'kal', sortable: true },
  { title: 'Datum Ein', key: 'dateIn', sortable: true },
  { title: 'Datum Aus', key: 'dateOut', sortable: true },
  { title: 'Lane', key: 'lane', sortable: true },
  { title: 'Aktionen', key: 'actions', sortable: false, width: '150px' }
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
  return Number(num).toFixed(3)
}

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
    
    // Convert datetime string to ISO format if present
    if (data.sampleStamp && typeof data.sampleStamp === 'string') {
      data.sampleStamp = new Date(data.sampleStamp).toISOString()
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
