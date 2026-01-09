<template>
  <v-container fluid>
    <h1 class="text-h4 mb-4">Dashboard</h1>
    <v-row>
      <v-col cols="12" sm="6" md="3" v-for="stat in stats" :key="stat.title">
        <v-card class="pa-4">
          <div class="d-flex align-center">
            <v-avatar :color="stat.color" size="48" class="mr-4">
              <v-icon>{{ stat.icon }}</v-icon>
            </v-avatar>
            <div>
              <div class="text-h5">{{ stat.count }}</div>
              <div class="text-caption">{{ stat.title }}</div>
            </div>
          </div>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const stats = ref([
  { title: 'Analysen', count: '-', icon: 'mdi-chart-bar', color: 'primary' },
  { title: 'Samples', count: '-', icon: 'mdi-test-tube', color: 'success' },
  { title: 'Boxen', count: '-', icon: 'mdi-package-variant', color: 'info' }
])

onMounted(async () => {
  try {
    const [a, s, b] = await Promise.all([
      axios.get('/api/analysis').catch(() => ({ data: { content: [] } })),
      axios.get('/api/sample').catch(() => ({ data: { content: [] } })),
      axios.get('/api/box').catch(() => ({ data: { content: [] } }))
    ])
    // API gibt {content: [...]} zurück
    const aData = a.data?.content || a.data || []
    const sData = s.data?.content || s.data || []
    const bData = b.data?.content || b.data || []

    stats.value[0].count = Array.isArray(aData) ? aData.length : 0
    stats.value[1].count = Array.isArray(sData) ? sData.length : 0
    stats.value[2].count = Array.isArray(bData) ? bData.length : 0
  } catch (e) { console.error(e) }
})
</script>