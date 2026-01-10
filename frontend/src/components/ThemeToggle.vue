<template>
  <v-btn
    icon
    @click="themeStore.toggleTheme()"
    :title="themeStore.isDark ? 'Zu Light Mode wechseln' : 'Zu Dark Mode wechseln'"
  >
    <v-icon>{{ themeStore.isDark ? 'mdi-weather-sunny' : 'mdi-weather-night' }}</v-icon>
  </v-btn>
</template>

<script setup>
import { useThemeStore } from '@/stores/theme'

const themeStore = useThemeStore()
</script>
