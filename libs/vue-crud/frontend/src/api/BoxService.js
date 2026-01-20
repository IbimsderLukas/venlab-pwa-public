import apiClient from './axios'

/**
 * Box API Service
 * Vollständige CRUD-Operationen für Box-Entitäten
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

  /**
   * Neue Box erstellen
   * @param {Object} box - Box-Objekt
   */
  async create(box) {
    const response = await apiClient.post('/api/box', box)
    return response.data
  }

  /**
   * Box aktualisieren
   * @param {string} id - Box ID
   * @param {Object} box - Aktualisierte Daten
   */
  async update(id, box) {
    const response = await apiClient.put(`/api/box/${id}`, box)
    return response.data
  }

  /**
   * Box löschen
   * @param {string} id - Box ID
   */
  async delete(id) {
    await apiClient.delete(`/api/box/${id}`)
  }
}

export default new BoxService()
