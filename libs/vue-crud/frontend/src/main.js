import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

// Vuetify
import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import '@mdi/font/css/materialdesignicons.css'

const vuetify = createVuetify({
  components,
  directives,
  theme: {
    defaultTheme: 'venlabTheme',
    themes: {
      venlabTheme: {
        dark: false,
        colors: {
          primary: '#1565C0',
          secondary: '#42A5F5',
          accent: '#7C4DFF',
          error: '#FF5252',
          info: '#2196F3',
          success: '#4CAF50',
          warning: '#FFC107',
          background: '#F5F7FA',
          surface: '#FFFFFF'
        }
      }
    }
  },
  defaults: {
    VDataTable: {
      fixedHeader: true,
      hover: true
    },
    VCard: {
      elevation: 2
    },
    VBtn: {
      variant: 'flat'
    }
  }
})

const app = createApp(App)
app.use(router)
app.use(vuetify)
app.mount('#app')
