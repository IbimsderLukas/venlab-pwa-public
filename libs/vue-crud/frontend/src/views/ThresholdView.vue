<template>
  <div>
    <!-- Header -->
    <v-row class="mb-4">
      <v-col>
        <h1 class="text-h5 font-weight-bold">Schwellenwerte</h1>
        <p class="text-subtitle-2 text-medium-emphasis">
          Konfigurierte Grenzwerte für Messwerte (nur Lesezugriff)
        </p>
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

      <v-data-table
        :headers="headers"
        :items="items"
        :loading="loading"
        :search="search"
        class="elevation-0"
      >
        <!-- ID Column -->
        <template v-slot:item.id="{ item }">
          <v-chip size="small" color="success" variant="tonal">
            {{ item.id }}
          </v-chip>
        </template>

        <!-- Min/Max Columns with visual indicator -->
        <template v-slot:item.min="{ item }">
          <div class="d-flex align-center">
            <v-icon icon="mdi-arrow-down" size="small" color="info" class="mr-1"></v-icon>
            {{ formatNumber(item.min) }}
          </div>
        </template>
        
        <template v-slot:item.max="{ item }">
          <div class="d-flex align-center">
            <v-icon icon="mdi-arrow-up" size="small" color="warning" class="mr-1"></v-icon>
            {{ formatNumber(item.max) }}
          </div>
        </template>

        <!-- Range visualization -->
        <template v-slot:item.range="{ item }">
          <div class="d-flex align-center" style="min-width: 150px;">
            <v-progress-linear
              :model-value="50"
              color="success"
              height="8"
              rounded
              class="mr-2"
            >
              <template v-slot:default>
                <span class="text-caption">{{ formatNumber(item.min) }} - {{ formatNumber(item.max) }}</span>
              </template>
            </v-progress-linear>
          </div>
        </template>

        <!-- Date Changed Column -->
        <template v-slot:item.dateChanged="{ item }">
          {{ formatDateTime(item.dateChanged) }}
        </template>

        <!-- Actions -->
        <template v-slot:item.actions="{ item }">
          <v-btn icon="mdi-eye" variant="text" size="small" @click="viewItem(item)"></v-btn>
        </template>
      </v-data-table>
    </v-card>

    <!-- Cards View for visual summary -->
    <v-row class="mt-6">
      <v-col cols="12">
        <h2 class="text-h6 font-weight-bold mb-4">Schwellenwert-Übersicht</h2>
      </v-col>
      <v-col v-for="item in items" :key="item.id" cols="12" sm="6" md="4" lg="3">
        <v-card variant="outlined" class="h-100">
          <v-card-title class="d-flex align-center pb-0">
            <v-icon icon="mdi-tune" size="small" class="mr-2" color="success"></v-icon>
            {{ item.id }}
          </v-card-title>
          <v-card-text>
            <div class="d-flex justify-space-between align-center mb-2">
              <span class="text-caption text-medium-emphasis">Bereich</span>
            </div>
            <div class="d-flex justify-space-between align-center">
              <v-chip size="small" color="info" variant="tonal">
                Min: {{ formatNumber(item.min) }}
              </v-chip>
              <v-icon icon="mdi-arrow-right" size="small" color="grey"></v-icon>
              <v-chip size="small" color="warning" variant="tonal">
                Max: {{ formatNumber(item.max) }}
              </v-chip>
            </div>
            <v-divider class="my-2"></v-divider>
            <div class="text-caption text-medium-emphasis">
              Geändert: {{ formatDateTime(item.dateChanged) }}
            </div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- View Dialog -->
    <v-dialog v-model="viewDialog" max-width="400">
      <v-card>
        <v-card-title class="d-flex align-center">
          <v-icon icon="mdi-tune" class="mr-2"></v-icon>
          Schwellenwert Details
        </v-card-title>
        <v-divider></v-divider>
        <v-card-text>
          <v-list density="compact">
            <v-list-item>
              <v-list-item-title class="font-weight-bold">ID</v-list-item-title>
              <v-list-item-subtitle>{{ viewedItem.id }}</v-list-item-subtitle>
            </v-list-item>
            <v-list-item>
              <v-list-item-title class="font-weight-bold">Minimum</v-list-item-title>
              <v-list-item-subtitle>{{ formatNumber(viewedItem.min) }}</v-list-item-subtitle>
            </v-list-item>
            <v-list-item>
              <v-list-item-title class="font-weight-bold">Maximum</v-list-item-title>
              <v-list-item-subtitle>{{ formatNumber(viewedItem.max) }}</v-list-item-subtitle>
            </v-list-item>
            <v-list-item>
              <v-list-item-title class="font-weight-bold">Geändert am</v-list-item-title>
              <v-list-item-subtitle>{{ formatDateTime(viewedItem.dateChanged) }}</v-list-item-subtitle>
            </v-list-item>
          </v-list>
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
import { ref, onMounted, inject } from 'vue'
import { ThresholdService } from '@/api'

const showNotification = inject('showNotification')

// Table state
const search = ref('')
const loading = ref(false)
const items = ref([])

// Dialog state
const viewDialog = ref(false)
const viewedItem = ref({})

const headers = [
  { title: 'ID', key: 'id', sortable: true },
  { title: 'Minimum', key: 'min', sortable: true },
  { title: 'Maximum', key: 'max', sortable: true },
  { title: 'Bereich', key: 'range', sortable: false },
  { title: 'Geändert', key: 'dateChanged', sortable: true },
  { title: 'Aktionen', key: 'actions', sortable: false, width: '80px' }
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
const loadData = async () => {
  loading.value = true
  try {
    items.value = await ThresholdService.getAll()
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

<style scoped>
.max-width-300 {
  max-width: 300px;
}
</style>
