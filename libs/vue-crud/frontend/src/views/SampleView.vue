<template>
  <div>
    <!-- Header mit Actions -->
    <v-row class="mb-4">
      <v-col>
        <h1 class="text-h5 font-weight-bold">Samples</h1>
        <p class="text-subtitle-2 text-medium-emphasis">
          Verwaltung aller Proben
        </p>
      </v-col>
      <v-col cols="auto">
        <v-btn color="primary" prepend-icon="mdi-plus" @click="openCreateDialog">
          Neues Sample
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
          <v-chip size="small" color="secondary" variant="tonal">
            {{ item.id }}
          </v-chip>
        </template>

        <!-- Timestamp Column - SORTIERBAR (indiziert) -->
        <template v-slot:item.stamp="{ item }">
          {{ formatDateTime(item.stamp) }}
        </template>

        <!-- Name Column - SORTIERBAR (indiziert: idx_sample_name) -->
        <template v-slot:item.name="{ item }">
          {{ item.name || '-' }}
        </template>

        <!-- Date Crumbled Column - SORTIERBAR (indiziert: idx_sample_date_crumbled) -->
        <template v-slot:item.dateCrumbled="{ item }">
          {{ formatDateTime(item.dateCrumbled) }}
        </template>

        <!-- Flags Column - SORTIERBAR (indiziert: idx_sample_s_flags) -->
        <template v-slot:item.flags="{ item }">
          <v-chip v-if="item.flags" size="small" :color="getFlagColor(item.flags)" variant="outlined">
            {{ item.flags }}
          </v-chip>
          <span v-else class="text-medium-emphasis">-</span>
        </template>

        <!-- BoxPos Column (aus sample_boxpos View) -->
        <template v-slot:item.boxPosView="{ item }">
          <v-chip v-if="item.boxPosView?.boxPos" size="small" color="info" variant="outlined">
            {{ item.boxPosView.boxPos }}
          </v-chip>
          <span v-else class="text-medium-emphasis">-</span>
        </template>

        <!-- Weight Columns -->
        <template v-slot:item.weightNet="{ item }">
          {{ formatNumber(item.weightNet) }}
        </template>
        <template v-slot:item.weightBrutto="{ item }">
          {{ formatNumber(item.weightBrutto) }}
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
    <v-dialog v-model="dialog" max-width="700" persistent>
      <v-card>
        <v-card-title class="d-flex align-center">
          <v-icon :icon="isEditing ? 'mdi-pencil' : 'mdi-plus'" class="mr-2"></v-icon>
          {{ isEditing ? 'Sample bearbeiten' : 'Neues Sample erstellen' }}
        </v-card-title>
        <v-divider></v-divider>
        <v-card-text>
          <v-form ref="form" v-model="formValid">
            <v-row>
              <!-- Nicht editierbare Felder bei Bearbeitung -->
              <v-col cols="12" md="6">
                <v-text-field
                  v-model="editedItem.id"
                  label="Sample ID *"
                  :rules="[rules.required]"
                  :disabled="isEditing"
                  :hint="isEditing ? 'Kann nicht geändert werden' : ''"
                  persistent-hint
                ></v-text-field>
              </v-col>
              <v-col cols="12" md="6">
                <v-text-field
                  v-model="editedItem.stamp"
                  label="Timestamp *"
                  type="datetime-local"
                  :rules="[rules.required]"
                  :disabled="isEditing"
                  :hint="isEditing ? 'Kann nicht geändert werden' : ''"
                  persistent-hint
                ></v-text-field>
              </v-col>
            </v-row>

            <v-divider class="my-4"></v-divider>
            <p class="text-subtitle-2 text-medium-emphasis mb-2">Editierbare Felder</p>

            <v-row>
              <v-col cols="12" md="6">
                <v-text-field
                  v-model="editedItem.name"
                  label="Name"
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

            <v-row>
              <v-col cols="6" md="4">
                <v-text-field
                  v-model.number="editedItem.weightNet"
                  label="Gewicht Netto"
                  type="number"
                  step="0.01"
                ></v-text-field>
              </v-col>
              <v-col cols="6" md="4">
                <v-text-field
                  v-model.number="editedItem.weightBrutto"
                  label="Gewicht Brutto"
                  type="number"
                  step="0.01"
                ></v-text-field>
              </v-col>
              <v-col cols="6" md="4">
                <v-text-field
                  v-model.number="editedItem.weightTara"
                  label="Gewicht Tara"
                  type="number"
                  step="0.01"
                ></v-text-field>
              </v-col>
            </v-row>

            <v-row>
              <v-col cols="6" md="4">
                <v-text-field
                  v-model.number="editedItem.quantity"
                  label="Menge"
                  type="number"
                ></v-text-field>
              </v-col>
              <v-col cols="6" md="4">
                <v-text-field
                  v-model.number="editedItem.distance"
                  label="Distanz"
                  type="number"
                  step="0.01"
                ></v-text-field>
              </v-col>
              <v-col cols="6" md="4">
                <v-text-field
                  v-model="editedItem.flags"
                  label="Flags"
                ></v-text-field>
              </v-col>
            </v-row>

            <v-row>
              <v-col cols="12" md="6">
                <v-text-field
                  v-model="editedItem.dateCrumbled"
                  label="Zerkleinert am"
                  type="datetime-local"
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
          <v-icon icon="mdi-test-tube" class="mr-2"></v-icon>
          Sample Details
        </v-card-title>
        <v-divider></v-divider>
        <v-card-text>
          <v-table density="compact">
            <tbody>
              <tr v-for="(value, key) in flattenedViewItem" :key="key">
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
          Möchten Sie das Sample <strong>{{ itemToDelete?.id }}</strong> wirklich löschen?
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
import { SampleService } from '@/api'

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
  id: '',
  stamp: '',
  name: '',
  weightNet: null,
  weightBrutto: null,
  weightTara: null,
  quantity: null,
  distance: null,
  dateCrumbled: '',
  flags: '',
  lane: null,
  comment: ''
}

const rules = {
  required: v => !!v || 'Pflichtfeld'
}

// Headers mit Sortierung für indizierte Spalten
const headers = [
  { title: 'ID', key: 'id', sortable: true },
  { title: 'Timestamp', key: 'stamp', sortable: true },
  { title: 'Name', key: 'name', sortable: true },  // indiziert
  { title: 'Netto (g)', key: 'weightNet', sortable: false },
  { title: 'Brutto (g)', key: 'weightBrutto', sortable: false },
  { title: 'Zerkleinert', key: 'dateCrumbled', sortable: true },  // indiziert
  { title: 'Flags', key: 'flags', sortable: true },  // indiziert
  { title: 'Box Position', key: 'boxPosView', sortable: false },
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
    
    const response = await SampleService.getAll(page, size)
    
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
  if (editedItem.value.stamp) {
    editedItem.value.stamp = editedItem.value.stamp.slice(0, 16)
  }
  if (editedItem.value.dateCrumbled) {
    editedItem.value.dateCrumbled = editedItem.value.dateCrumbled.slice(0, 16)
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
    
    // Convert datetime strings to ISO format
    if (data.stamp && typeof data.stamp === 'string') {
      data.stamp = new Date(data.stamp).toISOString()
    }
    if (data.dateCrumbled && typeof data.dateCrumbled === 'string') {
      data.dateCrumbled = new Date(data.dateCrumbled).toISOString()
    }

    if (isEditing.value) {
      // Für Update: Original-ID und Timestamp verwenden
      const originalId = editedItem.value.id
      const originalStamp = new Date(editedItem.value.stamp).toISOString()
      await SampleService.update(originalId, originalStamp, data)
      showNotification('Sample erfolgreich aktualisiert', 'success')
    } else {
      await SampleService.create(data)
      showNotification('Sample erfolgreich erstellt', 'success')
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
    const stamp = new Date(itemToDelete.value.stamp).toISOString()
    await SampleService.delete(itemToDelete.value.id, stamp)
    showNotification('Sample erfolgreich gelöscht', 'success')
    deleteDialog.value = false
    itemToDelete.value = null
    loadData({ page: 1, itemsPerPage: itemsPerPage.value })
  } catch (error) {
    console.error('Fehler beim Löschen:', error)
    const message = error.response?.data?.message || 'Fehler beim Löschen'
    showNotification(message, 'error')
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
