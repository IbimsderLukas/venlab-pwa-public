<template>
  <v-app :theme="currentTheme">
    <!-- Navigation Drawer -->
    <v-navigation-drawer v-if="auth.isAuthenticated" v-model="drawer" app>
      <v-list-item
        :title="auth.user?.name || auth.username"
        :subtitle="auth.role"
        prepend-icon="mdi-account-circle"
        class="mb-2"
      />
      
      <v-divider />
      
      <v-list nav density="compact">
        <v-list-item
          v-for="item in menuItems"
          :key="item.to"
          :to="item.to"
          :prepend-icon="item.icon"
          :title="item.title"
          rounded="lg"
        />
      </v-list>

      <template v-slot:append>
        <div class="pa-4">
          <v-btn
            block
            color="error"
            variant="tonal"
            prepend-icon="mdi-logout"
            @click="handleLogout"
          >
            Abmelden
          </v-btn>
        </div>
      </template>
    </v-navigation-drawer>

    <!-- App Bar -->
    <v-app-bar v-if="auth.isAuthenticated" app elevation="1">
      <v-app-bar-nav-icon @click="drawer = !drawer" />
      
      <v-app-bar-title>
        <v-icon class="mr-2">mdi-flask</v-icon>
        VenLab
      </v-app-bar-title>

      <v-spacer />

      <v-chip
        :color="isOnline ? 'success' : 'error'"
        size="small"
        variant="flat"
        class="mr-2"
      >
        <v-icon start size="small">
          {{ isOnline ? 'mdi-wifi' : 'mdi-wifi-off' }}
        </v-icon>
        {{ isOnline ? 'Online' : 'Offline' }}
      </v-chip>

      <ThemeSwitch />
    </v-app-bar>

    <!-- Main Content -->
    <v-main>
      <router-view />
    </v-main>

    <!-- PWA Update Snackbar -->
    <v-snackbar v-model="showUpdateSnackbar" timeout="-1" color="primary">
      Neue Version verfügbar!
      <template v-slot:actions>
        <v-btn variant="text" @click="updateApp">Aktualisieren</v-btn>
      </template>
    </v-snackbar>
  </v-app>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useTheme } from 'vuetify'
import { useAuthStore } from '@/stores/auth'
import { useSettingsStore } from '@/stores/settings'
import ThemeSwitch from '@/components/ThemeSwitch.vue'

const router = useRouter()
const theme = useTheme()
const auth = useAuthStore()
const settings = useSettingsStore()

const drawer = ref(true)
const isOnline = ref(navigator.onLine)
const showUpdateSnackbar = ref(false)

const currentTheme = computed(() => settings.isDarkMode ? 'dark' : 'light')

const menuItems = [
  { title: 'Dashboard', to: '/', icon: 'mdi-view-dashboard' },
  { title: 'Analysen', to: '/analysis', icon: 'mdi-chart-bar' },
  { title: 'Samples', to: '/sample', icon: 'mdi-test-tube' },
  { title: 'Boxen', to: '/box', icon: 'mdi-package-variant' },
  { title: 'Log', to: '/log', icon: 'mdi-text-box' }
]

function handleLogout() {
  auth.logout()
  router.push('/login')
}

function updateApp() {
  showUpdateSnackbar.value = false
  window.location.reload()
}

function handleOnline() {
  isOnline.value = true
}

function handleOffline() {
  isOnline.value = false
}

onMounted(() => {
  // Theme setzen
  theme.global.name.value = currentTheme.value
  
  // Online/Offline Events
  window.addEventListener('online', handleOnline)
  window.addEventListener('offline', handleOffline)
})

onUnmounted(() => {
  window.removeEventListener('online', handleOnline)
  window.removeEventListener('offline', handleOffline)
})
</script>
