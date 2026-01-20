import apiClient from './axios'

/**
 * Threshold API Service
 * Lesezugriff auf Schwellenwert-Konfigurationen
 */
class ThresholdService {
  /**
   * Alle Schwellenwerte abrufen
   */
  async getAll() {
    const response = await apiClient.get('/api/threshold')
    return response.data
  }

  /**
   * Einzelnen Schwellenwert nach ID abrufen
   * @param {string} id - Threshold ID
   */
  async getById(id) {
    const response = await apiClient.get(`/api/threshold/${id}`)
    return response.data
  }
}

export default new ThresholdService()
