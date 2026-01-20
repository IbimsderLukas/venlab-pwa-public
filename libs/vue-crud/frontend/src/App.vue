<template>
  <v-app>
    <!-- Navigation Drawer -->
    <v-navigation-drawer
      v-model="drawer"
      :rail="rail"
      permanent
      color="primary"
    >
      <v-list-item
        prepend-icon="mdi-flask"
        title="Venlab"
        subtitle="Datenverwaltung"
        nav
        class="my-2"
        @click="rail = !rail"
      >
        <template v-slot:append>
          <v-btn
            :icon="rail ? 'mdi-chevron-right' : 'mdi-chevron-left'"
            variant="text"
            size="small"
          ></v-btn>
        </template>
      </v-list-item>

      <v-divider></v-divider>

      <v-list density="compact" nav>
        <v-list-item
          v-for="item in navItems"
          :key="item.title"
          :prepend-icon="item.icon"
          :title="item.title"
          :to="item.to"
          color="white"
          rounded="xl"
        ></v-list-item>
      </v-list>
    </v-navigation-drawer>

    <!-- App Bar -->
    <v-app-bar flat color="surface" border="b">
      <v-app-bar-nav-icon @click="drawer = !drawer"></v-app-bar-nav-icon>
      <v-toolbar-title class="text-h6 font-weight-bold">
        {{ currentPageTitle }}
      </v-toolbar-title>
      <v-spacer></v-spacer>
      <v-chip color="success" variant="flat" size="small" class="mr-4">
        <v-icon start size="small">mdi-circle</v-icon>
        API Connected
      </v-chip>
    </v-app-bar>

    <!-- Main Content -->
    <v-main class="bg-background">
      <v-container fluid class="pa-6">
        <router-view></router-view>
      </v-container>
    </v-main>

    <!-- Snackbar for notifications -->
    <v-snackbar
      v-model="snackbar.show"
      :color="snackbar.color"
      :timeout="3000"
      location="bottom right"
    >
      {{ snackbar.text }}
      <template v-slot:actions>
        <v-btn variant="text" @click="snackbar.show = false">
          Schließen
        </v-btn>
      </template>
    </v-snackbar>
  </v-app>
</template>

<script setup>
import { ref, computed, provide, reactive } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const drawer = ref(true)
const rail = ref(false)

const navItems = [
  { title: 'Dashboard', icon: 'mdi-view-dashboard', to: '/' },
  { title: 'Analysis', icon: 'mdi-chart-line', to: '/analysis' },
  { title: 'Sample', icon: 'mdi-test-tube', to: '/sample' },
  { title: 'Boxen & Positionen', icon: 'mdi-package-variant-closed', to: '/box' },
  { title: 'Log', icon: 'mdi-file-document-outline', to: '/log' },
  { title: 'Threshold', icon: 'mdi-tune', to: '/threshold' }
]

const currentPageTitle = computed(() => {
  const item = navItems.find(item => item.to === route.path)
  return item ? item.title : 'Venlab'
})

// Global snackbar state
const snackbar = reactive({
  show: false,
  text: '',
  color: 'success'
})

const showNotification = (text, color = 'success') => {
  snackbar.text = text
  snackbar.color = color
  snackbar.show = true
}

provide('showNotification', showNotification)
</script>

<style>
.v-navigation-drawer .v-list-item--active {
  background-color: rgba(255, 255, 255, 0.15) !important;
}
</style>
