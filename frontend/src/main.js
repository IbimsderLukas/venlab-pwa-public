import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import vuetify from './plugins/vuetify'
import { registerSW } from 'virtual:pwa-register'

const app = createApp(App)
app.use(createPinia())
app.use(router)
app.use(vuetify)
app.mount('#app')

// PWA Service Worker
registerSW({
  onNeedRefresh() {
    if (confirm('Neue Version verfügbar. Aktualisieren?')) {
      window.location.reload()
    }
  },
  onOfflineReady() {
    console.log('App offline verfügbar')
  }
})
