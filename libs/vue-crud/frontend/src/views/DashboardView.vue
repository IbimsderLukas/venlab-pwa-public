<template>
  <div>
    <v-row>
      <v-col cols="12">
        <h1 class="text-h4 font-weight-bold mb-2">Willkommen bei Venlab</h1>
        <p class="text-subtitle-1 text-medium-emphasis mb-6">
          Labordaten-Verwaltungssystem
        </p>
      </v-col>
    </v-row>

    <!-- Statistik-Karten -->
    <v-row>
      <v-col v-for="stat in stats" :key="stat.title" cols="12" sm="6" md="4" lg="2">
        <v-card :color="stat.color" variant="tonal" class="pa-4">
          <div class="d-flex align-center">
            <v-icon :icon="stat.icon" size="40" class="mr-4"></v-icon>
            <div>
              <div class="text-h5 font-weight-bold">{{ stat.value }}</div>
              <div class="text-caption">{{ stat.title }}</div>
            </div>
          </div>
        </v-card>
      </v-col>
    </v-row>

    <!-- Quick Links -->
    <v-row class="mt-6">
      <v-col cols="12">
        <h2 class="text-h6 font-weight-bold mb-4">Schnellzugriff</h2>
      </v-col>
      <v-col v-for="link in quickLinks" :key="link.title" cols="12" sm="6" md="4">
        <v-card
          :to="link.to"
          class="pa-4 h-100 cursor-pointer"
          hover
        >
          <div class="d-flex align-center">
            <v-avatar :color="link.color" size="48" class="mr-4">
              <v-icon :icon="link.icon" color="white"></v-icon>
            </v-avatar>
            <div>
              <div class="text-subtitle-1 font-weight-bold">{{ link.title }}</div>
              <div class="text-caption text-medium-emphasis">{{ link.description }}</div>
            </div>
          </div>
        </v-card>
      </v-col>
    </v-row>

    <!-- API Status -->
    <v-row class="mt-6">
      <v-col cols="12" md="6">
        <v-card>
          <v-card-title>
            <v-icon icon="mdi-api" class="mr-2"></v-icon>
            API Status
          </v-card-title>
          <v-card-text>
            <v-list density="compact">
              <v-list-item v-for="endpoint in apiEndpoints" :key="endpoint.name">
                <template v-slot:prepend>
                  <v-icon
                    :icon="endpoint.status === 'ok' ? 'mdi-check-circle' : 'mdi-alert-circle'"
                    :color="endpoint.status === 'ok' ? 'success' : 'error'"
                    size="small"
                  ></v-icon>
                </template>
                <v-list-item-title>{{ endpoint.name }}</v-list-item-title>
                <v-list-item-subtitle>{{ endpoint.url }}</v-list-item-subtitle>
              </v-list-item>
            </v-list>
          </v-card-text>
          <v-card-actions>
            <v-btn color="primary" variant="text" @click="checkApiStatus" :loading="checking">
              Status prüfen
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-col>

      <v-col cols="12" md="6">
        <v-card>
          <v-card-title>
            <v-icon icon="mdi-information" class="mr-2"></v-icon>
            Systeminfo
          </v-card-title>
          <v-card-text>
            <v-list density="compact">
              <v-list-item>
                <v-list-item-title>Frontend</v-list-item-title>
                <v-list-item-subtitle>Vue 3 + Vuetify 3 + Axios</v-list-item-subtitle>
              </v-list-item>
              <v-list-item>
                <v-list-item-title>Backend</v-list-item-title>
                <v-list-item-subtitle>Spring Boot REST API</v-list-item-subtitle>
              </v-list-item>
              <v-list-item>
                <v-list-item-title>Datenbank</v-list-item-title>
                <v-list-item-subtitle>PostgreSQL 13</v-list-item-subtitle>
              </v-list-item>
              <v-list-item>
                <v-list-item-title>API Dokumentation</v-list-item-title>
                <v-list-item-subtitle>
                  <a href="http://localhost:8081/swagger-ui/index.html" target="_blank">
                    Swagger UI öffnen
                  </a>
                </v-list-item-subtitle>
              </v-list-item>
            </v-list>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </div>
</template>

<script setup>
import { ref, onMounted, inject } from 'vue'
import {
  AnalysisService,
  SampleService,
  BoxService,
  BoxPosService,
  LogService,
  ThresholdService
} from '@/api'

const showNotification = inject('showNotification')

const stats = ref([
  { title: 'Analysen', value: '-', icon: 'mdi-chart-line', color: 'primary' },
  { title: 'Samples', value: '-', icon: 'mdi-test-tube', color: 'secondary' },
  { title: 'Boxen', value: '-', icon: 'mdi-package-variant-closed', color: 'accent' },
  { title: 'Positionen', value: '-', icon: 'mdi-grid', color: 'info' },
  { title: 'Logs', value: '-', icon: 'mdi-file-document-outline', color: 'warning' },
  { title: 'Thresholds', value: '-', icon: 'mdi-tune', color: 'success' }
])

const quickLinks = [
  {
    title: 'Analysen verwalten',
    description: 'CRUD-Operationen für Analysedaten',
    icon: 'mdi-chart-line',
    color: 'primary',
    to: '/analysis'
  },
  {
    title: 'Samples ansehen',
    description: 'Proben-Übersicht und Details',
    icon: 'mdi-test-tube',
    color: 'secondary',
    to: '/sample'
  },
  {
    title: 'Box-Verwaltung',
    description: 'Lagerboxen und Positionen',
    icon: 'mdi-package-variant-closed',
    color: 'accent',
    to: '/box'
  }
]

const apiEndpoints = ref([
  { name: 'Analysis', url: '/api/analysis', status: 'pending' },
  { name: 'Sample', url: '/api/sample', status: 'pending' },
  { name: 'Box', url: '/api/box', status: 'pending' },
  { name: 'BoxPos', url: '/api/boxpos', status: 'pending' },
  { name: 'Log', url: '/api/log', status: 'pending' },
  { name: 'Threshold', url: '/api/threshold', status: 'pending' }
])

const checking = ref(false)

const loadStats = async () => {
  try {
    const [analysis, sample, box, boxpos, log, threshold] = await Promise.allSettled([
      AnalysisService.getAll(0, 1),
      SampleService.getAll(0, 1),
      BoxService.getAll(),
      BoxPosService.getAll(),
      LogService.getAll(),
      ThresholdService.getAll()
    ])

    if (analysis.status === 'fulfilled') {
      stats.value[0].value = analysis.value.totalElements || analysis.value.length || 0
    }
    if (sample.status === 'fulfilled') {
      stats.value[1].value = sample.value.totalElements || sample.value.length || 0
    }
    if (box.status === 'fulfilled') {
      stats.value[2].value = box.value.length || 0
    }
    if (boxpos.status === 'fulfilled') {
      stats.value[3].value = boxpos.value.length || 0
    }
    if (log.status === 'fulfilled') {
      stats.value[4].value = log.value.length || 0
    }
    if (threshold.status === 'fulfilled') {
      stats.value[5].value = threshold.value.length || 0
    }
  } catch (error) {
    console.error('Fehler beim Laden der Statistiken:', error)
  }
}

const checkApiStatus = async () => {
  checking.value = true
  
  const services = [
    { index: 0, service: () => AnalysisService.getAll(0, 1) },
    { index: 1, service: () => SampleService.getAll(0, 1) },
    { index: 2, service: () => BoxService.getAll() },
    { index: 3, service: () => BoxPosService.getAll() },
    { index: 4, service: () => LogService.getAll() },
    { index: 5, service: () => ThresholdService.getAll() }
  ]

  for (const { index, service } of services) {
    try {
      await service()
      apiEndpoints.value[index].status = 'ok'
    } catch {
      apiEndpoints.value[index].status = 'error'
    }
  }

  checking.value = false
  showNotification('API-Status wurde aktualisiert', 'info')
}

onMounted(() => {
  loadStats()
  checkApiStatus()
})
</script>
