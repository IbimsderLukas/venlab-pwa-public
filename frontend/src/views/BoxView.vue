<template>
  <v-container fluid>
    <v-card>
      <v-card-title class="d-flex align-center">
        <v-icon class="mr-2">mdi-package-variant</v-icon>
        Boxen
        <v-spacer />
        <ColumnSelector table="box" class="mr-2" />
        <v-btn icon variant="text" @click="load" :loading="loading">
          <v-icon>mdi-refresh</v-icon>
        </v-btn>
      </v-card-title>
      <v-card-text>
        <v-text-field v-model="search" prepend-inner-icon="mdi-magnify" label="Suchen" hide-details class="mb-4" />
        <v-data-table :headers="headers" :items="items" :search="search" :loading="loading" :items-per-page="10" />
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useSettingsStore } from '@/stores/settings'
import ColumnSelector from '@/components/ColumnSelector.vue'
import axios from 'axios'

const settings = useSettingsStore()
const items = ref([])
const loading = ref(false)
const search = ref('')

const headers = computed(() => settings.getTableHeaders('box'))

async function load() {
  loading.value = true
  try {
    const res = await axios.get('/api/box')
    // API gibt {content: [...]} zurück
    items.value = res.data?.content || res.data || []
  } catch (e) {
    console.error(e)
    items.value = []
  }
  loading.value = false
}

onMounted(load)
</script>