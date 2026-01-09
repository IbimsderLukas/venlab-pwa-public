<template>
  <v-app :theme="currentTheme">
    <v-navigation-drawer v-if="auth.isAuthenticated" v-model="drawer" app>
      <v-list-item :title="auth.username" :subtitle="auth.role" prepend-icon="mdi-account-circle" />
      <v-divider />
      <v-list nav density="compact">
        <v-list-item v-for="item in menu" :key="item.to" :to="item.to" :prepend-icon="item.icon" :title="item.title" />
      </v-list>
      <template v-slot:append>
        <div class="pa-2">
          <v-btn block color="error" variant="tonal" @click="logout">Abmelden</v-btn>
        </div>
      </template>
    </v-navigation-drawer>

    <v-app-bar v-if="auth.isAuthenticated" app>
      <v-app-bar-nav-icon @click="drawer = !drawer" />
      <v-app-bar-title>VenLab</v-app-bar-title>
      <v-spacer />
      <v-chip :color="apiOnline ? 'success' : 'error'" size="small" class="mr-2">
        {{ apiOnline ? 'Online' : 'Offline' }}
      </v-chip>
      <ThemeSwitch />
    </v-app-bar>

    <v-main>
      <router-view />
    </v-main>
  </v-app>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useTheme } from 'vuetify'
import { useAuthStore } from '@/stores/auth'
import { useSettingsStore } from '@/stores/settings'
import ThemeSwitch from '@/components/ThemeSwitch.vue'
import axios from 'axios'

const router = useRouter()
const theme = useTheme()
const auth = useAuthStore()
const settings = useSettingsStore()

const drawer = ref(true)
const apiOnline = ref(true)

const menu = [
  { title: 'Dashboard', to: '/', icon: 'mdi-view-dashboard' },
  { title: 'Analysen', to: '/analysis', icon: 'mdi-chart-bar' },
  { title: 'Samples', to: '/sample', icon: 'mdi-test-tube' },
  { title: 'Boxen', to: '/box', icon: 'mdi-package-variant' }
]

const currentTheme = computed(() => settings.isDarkMode ? 'dark' : 'light')

function logout() {
  auth.logout()
  router.push('/login')
}

async function checkApi() {
  try {
    await axios.get('/api/box', { timeout: 5000 })
    apiOnline.value = true
  } catch { apiOnline.value = false }
}

onMounted(() => {
  theme.global.name.value = currentTheme.value
  checkApi()
  setInterval(checkApi, 30000)
})
</script>
