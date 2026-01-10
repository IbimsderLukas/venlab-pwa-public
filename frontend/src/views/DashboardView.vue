<template>
  <v-container fluid>
    <h1 class="text-h4 mb-6">Dashboard</h1>
    
    <v-row>
      <v-col v-for="stat in stats" :key="stat.title" cols="12" sm="6" md="3">
        <v-card :loading="loading">
          <v-card-text class="d-flex align-center pa-4">
            <v-avatar :color="stat.color" size="56" class="mr-4">
              <v-icon size="28" color="white">{{ stat.icon }}</v-icon>
            </v-avatar>
            <div>
              <div class="text-h4 font-weight-bold">{{ stat.count }}</div>
              <div class="text-body-2 text-medium-emphasis">{{ stat.title }}</div>
            </div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <v-row class="mt-4">
      <v-col cols="12" md="6">
        <v-card>
          <v-card-title>Schnellzugriff</v-card-title>
          <v-card-text>
            <v-list>
              <v-list-item
                v-for="link in quickLinks"
                :key="link.to"
                :to="link.to"
                :prepend-icon="link.icon"
                :title="link.title"
              />
            </v-list>
          </v-card-text>
        </v-card>
      </v-col>
      
      <v-col cols="12" md="6">
        <v-card>
          <v-card-title>System Info</v-card-title>
          <v-card-text>
            <v-list density="compact">
              <v-list-item>
                <template v-slot:prepend>
                  <v-icon color="success">mdi-circle</v-icon>
                </template>
                <v-list-item-title>API Status: {{ apiOnline ? 'Online' : 'Offline' }}</v-list-item-title>
              </v-list-item>
              <v-list-item>
                <template v-slot:prepend>
                  <v-icon>mdi-account</v-icon>
                </template>
                <v-list-item-title>Angemeldet als: {{ auth.username }} ({{ auth.role }})</v-list-item-title>
              </v-list-item>
            </v-list>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const loading = ref(true)
const apiOnline = ref(false)

const stats = ref([
  { title: 'Analysen', count: '-', icon: 'mdi-chart-bar', color: 'primary' },
  { title: 'Samples', count: '-', icon: 'mdi-test-tube', color: 'success' },
  { title: 'Boxen', count: '-', icon: 'mdi-package-variant', color: 'info' },
  { title: 'Log Einträge', count: '-', icon: 'mdi-text-box', color: 'warning' }
])

const quickLinks = [
  { title: 'Analysen anzeigen', to: '/analysis', icon: 'mdi-chart-bar' },
  { title: 'Samples anzeigen', to: '/sample', icon: 'mdi-test-tube' },
  { title: 'Boxen verwalten', to: '/box', icon: 'mdi-package-variant' },
  { title: 'Log anzeigen', to: '/log', icon: 'mdi-text-box' }
]

async function loadStats() {
  loading.value = true
  
  try {
    const endpoints = ['/api/analysis', '/api/sample', '/api/box', '/api/log']
    const results = await Promise.all(
      endpoints.map(url => 
        axios.get(url).catch(() => ({ data: null }))
      )
    )

    results.forEach((res, i) => {
      if (res.data) {
        // Handle both array and {content: [...]} response
        const data = res.data.content || res.data
        stats.value[i].count = Array.isArray(data) ? data.length : 0
        apiOnline.value = true
      }
    })
  } catch (e) {
    console.error('Fehler beim Laden der Statistiken:', e)
  }
  
  loading.value = false
}

onMounted(loadStats)
</script>
