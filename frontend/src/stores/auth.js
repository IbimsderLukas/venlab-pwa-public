import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const user = ref(null)
  const token = ref(null)
  const isLoading = ref(false)
  const error = ref(null)

  // Demo Users - In Produktion über Backend/JWT
  const DEMO_USERS = {
    'admin': { password: 'admin123', role: 'Admin', name: 'Administrator' },
    'user': { password: 'user123', role: 'Reader', name: 'Benutzer' },
    'researcher': { password: 'research123', role: 'Researcher', name: 'Forscher' }
  }

  const isAuthenticated = computed(() => !!token.value)
  const username = computed(() => user.value?.username || '')
  const role = computed(() => user.value?.role || '')

  function loadFromStorage() {
    try {
      const savedToken = localStorage.getItem('venlab_token')
      const savedUser = localStorage.getItem('venlab_user')
      if (savedToken && savedUser) {
        token.value = savedToken
        user.value = JSON.parse(savedUser)
        return true
      }
    } catch (e) {
      console.error('Fehler beim Laden der Session:', e)
      logout()
    }
    return false
  }

  async function login(inputUsername, inputPassword) {
    isLoading.value = true
    error.value = null

    // Kurze Verzögerung simulieren
    await new Promise(r => setTimeout(r, 300))

    try {
      const demoUser = DEMO_USERS[inputUsername]
      
      if (!demoUser || demoUser.password !== inputPassword) {
        error.value = 'Ungültiger Benutzername oder Passwort'
        isLoading.value = false
        return false
      }

      const newToken = btoa(`${inputUsername}:${Date.now()}`)
      const userData = {
        username: inputUsername,
        role: demoUser.role,
        name: demoUser.name
      }

      token.value = newToken
      user.value = userData

      localStorage.setItem('venlab_token', newToken)
      localStorage.setItem('venlab_user', JSON.stringify(userData))

      isLoading.value = false
      return true
    } catch (e) {
      error.value = 'Ein Fehler ist aufgetreten'
      isLoading.value = false
      return false
    }
  }

  function logout() {
    token.value = null
    user.value = null
    error.value = null
    localStorage.removeItem('venlab_token')
    localStorage.removeItem('venlab_user')
  }

  // Beim Start laden
  loadFromStorage()

  return {
    user,
    token,
    isLoading,
    error,
    isAuthenticated,
    username,
    role,
    login,
    logout,
    loadFromStorage
  }
})
