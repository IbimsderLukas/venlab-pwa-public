<template>
  <div>
    <!-- Header -->
    <v-row class="mb-4">
      <v-col>
        <h1 class="text-h5 font-weight-bold">Log-Einträge</h1>
        <p class="text-subtitle-2 text-medium-emphasis">
          Systemprotokoll und Ereignisse (nur Lesezugriff)
        </p>
      </v-col>
    </v-row>

    <!-- Filters -->
    <v-card class="mb-4">
      <v-card-text>
        <v-row align="center">
          <v-col cols="12" md="4">
            <v-text-field
              v-model="search"
              prepend-inner-icon="mdi-magnify"
              label="Suchen"
              single-line
              hide-details
              density="compact"
            ></v-text-field>
          </v-col>
          <v-col cols="12" md="3">
            <v-select
              v-model="levelFilter"
              :items="logLevels"
              label="Log Level"
              clearable
              density="compact"
              hide-details
            ></v-select>
          </v-col>
          <v-col cols="auto">
            <v-btn icon="mdi-refresh" variant="text" @click="loadData" :loading="loading"></v-btn>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>

    <!-- Data Table -->
    <v-card>
      <v-data-table
        :headers="headers"
        :items="filteredItems"
        :loading="loading"
        :search="search"
        class="elevation-0"
      >
        <!-- ID Column -->
        <template v-slot:item.id="{ item }">
          <v-chip size="small" color="primary" variant="tonal">
            {{ item.id }}
          </v-chip>
        </template>

        <!-- Level Column -->
        <template v-slot:item.level="{ item }">
          <v-chip :color="getLevelColor(item.level)" size="small" variant="flat">
            {{ item.level }}
          </v-chip>
        </template>

        <!-- Created Column -->
        <template v-slot:item.created="{ item }">
          {{ formatDateTime(item.created) }}
        </template>

        <!-- Info Column (truncated) -->
        <template v-slot:item.info="{ item }">
          <span class="text-truncate d-inline-block" style="max-width: 300px;">
            {{ item.info || '-' }}
          </span>
        </template>

        <!-- Sample ID Column -->
        <template v-slot:item.sampleId="{ item }">
          <v-chip v-if="item.sampleId" size="small" color="secondary" variant="tonal">
            {{ item.sampleId }}
          </v-chip>
          <span v-else class="text-medium-emphasis">-</span>
        </template>

        <!-- Analysis ID Column -->
        <template v-slot:item.analysisId="{ item }">
          <v-chip v-if="item.analysisId" size="small" color="info" variant="outlined">
            {{ item.analysisId }}
          </v-chip>
          <span v-else class="text-medium-emphasis">-</span>
        </template>

        <!-- Actions -->
        <template v-slot:item.actions="{ item }">
          <v-btn icon="mdi-eye" variant="text" size="small" @click="viewItem(item)"></v-btn>
        </template>
      </v-data-table>
    </v-card>

    <!-- View Dialog -->
    <v-dialog v-model="viewDialog" max-width="600">
      <v-card>
        <v-card-title class="d-flex align-center">
          <v-icon icon="mdi-file-document-outline" class="mr-2"></v-icon>
          Log Details
          <v-spacer></v-spacer>
          <v-chip :color="getLevelColor(viewedItem.level)" size="small">
            {{ viewedItem.level }}
          </v-chip>
        </v-card-title>
        <v-divider></v-divider>
        <v-card-text>
          <v-list density="compact">
            <v-list-item>
              <v-list-item-title class="font-weight-bold">ID</v-list-item-title>
              <v-list-item-subtitle>{{ viewedItem.id }}</v-list-item-subtitle>
            </v-list-item>
            <v-list-item>
              <v-list-item-title class="font-weight-bold">Erstellt</v-list-item-title>
              <v-list-item-subtitle>{{ formatDateTime(viewedItem.created) }}</v-list-item-subtitle>
            </v-list-item>
            <v-list-item>
              <v-list-item-title class="font-weight-bold">Level</v-list-item-title>
              <v-list-item-subtitle>{{ viewedItem.level || '-' }}</v-list-item-subtitle>
            </v-list-item>
            <v-list-item>
              <v-list-item-title class="font-weight-bold">Sample ID</v-list-item-title>
              <v-list-item-subtitle>{{ viewedItem.sampleId || '-' }}</v-list-item-subtitle>
            </v-list-item>
            <v-list-item>
              <v-list-item-title class="font-weight-bold">Sample Timestamp</v-list-item-title>
              <v-list-item-subtitle>{{ formatDateTime(viewedItem.sampleStamp) }}</v-list-item-subtitle>
            </v-list-item>
            <v-list-item>
              <v-list-item-title class="font-weight-bold">Analysis ID</v-list-item-title>
              <v-list-item-subtitle>{{ viewedItem.analysisId || '-' }}</v-list-item-subtitle>
            </v-list-item>
            <v-list-item>
              <v-list-item-title class="font-weight-bold">Exportiert am</v-list-item-title>
              <v-list-item-subtitle>{{ formatDateTime(viewedItem.dateExported) }}</v-list-item-subtitle>
            </v-list-item>
          </v-list>
          
          <v-divider class="my-3"></v-divider>
          
          <div class="text-subtitle-2 font-weight-bold mb-2">Info</div>
          <v-sheet color="grey-lighten-4" rounded class="pa-3">
            <pre class="text-body-2" style="white-space: pre-wrap; word-break: break-word;">{{ viewedItem.info || '-' }}</pre>
          </v-sheet>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn variant="text" @click="viewDialog = false">Schließen</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, inject } from 'vue'
import { LogService } from '@/api'

const showNotification = inject('showNotification')

// Table state
const search = ref('')
const levelFilter = ref(null)
const loading = ref(false)
const items = ref([])

// Dialog state
const viewDialog = ref(false)
const viewedItem = ref({})

const logLevels = ['DEBUG', 'INFO', 'WARN', 'ERROR', 'FATAL']

const headers = [
  { title: 'ID', key: 'id', sortable: true, width: '80px' },
  { title: 'Level', key: 'level', sortable: true, width: '100px' },
  { title: 'Erstellt', key: 'created', sortable: true },
  { title: 'Info', key: 'info', sortable: false },
  { title: 'Sample ID', key: 'sampleId', sortable: true },
  { title: 'Analysis ID', key: 'analysisId', sortable: true },
  { title: 'Aktionen', key: 'actions', sortable: false, width: '80px' }
]

// Computed filtered items
const filteredItems = computed(() => {
  if (!levelFilter.value) return items.value
  return items.value.filter(item => item.level === levelFilter.value)
})

// Helper functions
const formatDateTime = (dateStr) => {
  if (!dateStr) return '-'
  try {
    return new Date(dateStr).toLocaleString('de-AT')
  } catch {
    return dateStr
  }
}

const getLevelColor = (level) => {
  const colors = {
    'DEBUG': 'grey',
    'INFO': 'info',
    'WARN': 'warning',
    'ERROR': 'error',
    'FATAL': 'error'
  }
  return colors[level] || 'grey'
}

// Data loading
const loadData = async () => {
  loading.value = true
  try {
    items.value = await LogService.getAll()
  } catch (error) {
    console.error('Fehler beim Laden:', error)
    showNotification('Fehler beim Laden der Daten', 'error')
    items.value = []
  } finally {
    loading.value = false
  }
}

const viewItem = (item) => {
  viewedItem.value = { ...item }
  viewDialog.value = true
}

onMounted(loadData)
</script>
