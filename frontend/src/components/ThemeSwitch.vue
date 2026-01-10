<template>
  <v-btn icon variant="text" @click="toggle" :title="isDark ? 'Light Mode' : 'Dark Mode'">
    <v-icon>{{ isDark ? 'mdi-weather-sunny' : 'mdi-weather-night' }}</v-icon>
  </v-btn>
</template>

<script setup>
import { computed } from 'vue'
import { useTheme } from 'vuetify'
import { useSettingsStore } from '@/stores/settings'

const theme = useTheme()
const settings = useSettingsStore()

const isDark = computed(() => settings.isDarkMode)

function toggle() {
  settings.toggleTheme()
  theme.global.name.value = settings.isDarkMode ? 'dark' : 'light'
}
</script>
