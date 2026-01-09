<template>
  <v-btn icon variant="text" @click="toggleTheme">
    <v-icon>{{ isDarkMode ? 'mdi-weather-sunny' : 'mdi-weather-night' }}</v-icon>
  </v-btn>
</template>

<script setup>
import { computed } from 'vue'
import { useTheme } from 'vuetify'
import { useSettingsStore } from '@/stores/settings'

const theme = useTheme()
const settings = useSettingsStore()

const isDarkMode = computed(() => settings.isDarkMode)

function toggleTheme() {
  settings.toggleTheme()
  theme.global.name.value = settings.isDarkMode ? 'dark' : 'light'
}
</script>
