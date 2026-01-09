// stores/auth.js - Einfacher Frontend-Login (kein Backend erforderlich)
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  // State
  const user = ref(null)
  const token = ref(null)
  const isLoading = ref(false)
  const error = ref(null)

  // Demo-Benutzer (Passwörter nur im Frontend - für Produktion Backend verwenden!)
  const USERS = {
    'admin': { password: 'admin123', role: 'Admin' },
    'user': { password: 'user123', role: 'Reader' },
    'researcher': { password: 'research123', role: 'Researcher' }
  }

  // Computed
  const isAuthenticated = computed(() => !!token.value)
  const username = computed(() => user.value?.username || '')
  const role = computed(() => user.value?.role || '')

  // Token aus LocalStorage laden
  function loadToken() {
    try {
      const savedToken = localStorage.getItem('venlab-token')
      const savedUser = localStorage.getItem('venlab-user')
      
      if (savedToken && savedUser) {
        token.value = savedToken
        user.value = JSON.parse(savedUser)
        return true
      }
    } catch (e) {
      console.error('Token laden fehlgeschlagen:', e)
      logout()
    }
    return false
  }

  // Login (Frontend-only)
  async function login(inputUsername, inputPassword) {
    isLoading.value = true
    error.value = null

    // Simuliere kurze Verzögerung
    await new Promise(resolve => setTimeout(resolve, 300))

    try {
      const demoUser = USERS[inputUsername]
      
      if (!demoUser || demoUser.password !== inputPassword) {
        error.value = 'Ungültiger Benutzername oder Passwort'
        isLoading.value = false
        return false
      }

      // Einfacher Token generieren
      const newToken = btoa(`${inputUsername}:${Date.now()}`)
      const userData = { 
        username: inputUsername, 
        role: demoUser.role 
      }

      // State setzen
      token.value = newToken
      user.value = userData

      // LocalStorage speichern
      localStorage.setItem('venlab-token', newToken)
      localStorage.setItem('venlab-user', JSON.stringify(userData))

      isLoading.value = false
      return true
    } catch (e) {
      console.error('Login Fehler:', e)
      error.value = 'Ein Fehler ist aufgetreten'
      isLoading.value = false
      return false
    }
  }

  // Logout
  function logout() {
    token.value = null
    user.value = null
    error.value = null
    localStorage.removeItem('venlab-token')
    localStorage.removeItem('venlab-user')
  }

  // Beim Laden initialisieren
  loadToken()

  return {
    // State
    user,
    token,
    isLoading,
    error,
    // Computed
    isAuthenticated,
    username,
    role,
    // Actions
    login,
    logout,
    loadToken
  }
})
