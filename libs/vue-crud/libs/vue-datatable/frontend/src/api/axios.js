import axios from 'axios'

// API Base URL Konfiguration:
// - In Docker (via nginx proxy): Relativer Pfad '' (nginx leitet /api/* an Backend weiter)
// - Lokale Entwicklung: 'http://localhost:8081'
// 
// Setze VITE_API_URL in .env oder .env.local für lokale Entwicklung:
// VITE_API_URL=http://localhost:8081
const API_BASE_URL = import.meta.env.VITE_API_URL || ''

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  },
  timeout: 10000
})

// Request Interceptor
apiClient.interceptors.request.use(
  (config) => {
    console.log(`[API] ${config.method?.toUpperCase()} ${config.url}`)
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response Interceptor
apiClient.interceptors.response.use(
  (response) => {
    return response
  },
  (error) => {
    console.error('[API Error]', error.response?.status, error.message)
    return Promise.reject(error)
  }
)

export default apiClient
