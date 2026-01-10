<template>
  <v-container fluid>
    <v-card>
      <v-card-title class="d-flex align-center flex-wrap ga-2">
        <v-icon class="mr-2">mdi-test-tube</v-icon>
        <span>Samples</span>
        <v-spacer />
        <ColumnSelector table="sample" @update="loadData" class="mr-2" />
        <v-btn icon variant="text" @click="loadData" :loading="loading">
          <v-icon>mdi-refresh</v-icon>
        </v-btn>
      </v-card-title>

      <v-card-text>
        <v-text-field
          v-model="search"
          prepend-inner-icon="mdi-magnify"
          label="Suchen..."
          single-line
          hide-details
          clearable
          class="mb-4"
        />

        <v-data-table
          :headers="headers"
          :items="items"
          :search="search"
          :loading="loading"
          :items-per-page="10"
          class="elevation-1"
        >
          <template v-slot:loading>
            <v-skeleton-loader type="table-row@5" />
          </template>
        </v-data-table>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import { useSettingsStore } from '@/stores/settings'
import ColumnSelector from '@/components/ColumnSelector.vue'

const settings = useSettingsStore()
const items = ref([])
const loading = ref(false)
const search = ref('')

const headers = computed(() => settings.getTableHeaders('sample'))

async function loadData() {
  loading.value = true
  try {
    const res = await axios.get('/api/sample')
    items.value = res.data?.content || res.data || []
  } catch (e) {
    console.error('Fehler beim Laden:', e)
    items.value = []
  }
  loading.value = false
}

onMounted(loadData)
</script>
