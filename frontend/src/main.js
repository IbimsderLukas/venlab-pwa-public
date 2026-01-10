import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import vuetify from './plugins/vuetify'

// PWA
import { registerSW } from 'virtual:pwa-register'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(vuetify)

app.mount('#app')

// Service Worker Registration
const updateSW = registerSW({
  onNeedRefresh() {
    if (confirm('Neue Version verfügbar. Jetzt aktualisieren?')) {
      updateSW()
    }
  },
  onOfflineReady() {
    console.log('VenLab ist jetzt offline verfügbar')
  }
})
