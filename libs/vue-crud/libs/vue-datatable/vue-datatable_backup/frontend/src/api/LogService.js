import apiClient from './axios'

/**
 * Log API Service
 * Lesezugriff auf Log-Einträge
 */
class LogService {
  /**
   * Alle Log-Einträge abrufen
   */
  async getAll() {
    const response = await apiClient.get('/api/log')
    return response.data
  }

  /**
   * Einzelnen Log-Eintrag nach ID abrufen
   * @param {number} id - Log ID
   */
  async getById(id) {
    const response = await apiClient.get(`/api/log/${id}`)
    return response.data
  }
}

export default new LogService()
