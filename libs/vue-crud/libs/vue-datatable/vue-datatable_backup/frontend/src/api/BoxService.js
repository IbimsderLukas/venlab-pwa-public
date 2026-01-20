import apiClient from './axios'

/**
 * Box API Service
 * Lesezugriff auf Box-Entitäten
 */
class BoxService {
  /**
   * Alle Boxen abrufen
   */
  async getAll() {
    const response = await apiClient.get('/api/box')
    return response.data
  }

  /**
   * Einzelne Box nach ID abrufen
   * @param {string} id - Box ID
   */
  async getById(id) {
    const response = await apiClient.get(`/api/box/${id}`)
    return response.data
  }
}

export default new BoxService()
