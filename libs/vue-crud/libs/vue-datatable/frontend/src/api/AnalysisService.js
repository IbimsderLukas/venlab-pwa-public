import apiClient from './axios'

/**
 * Analysis API Service
 * Vollständige CRUD-Operationen für Analysis-Entitäten
 */
class AnalysisService {
  /**
   * Alle Analysen abrufen (paginiert)
   * @param {number} page - Seitennummer (0-basiert)
   * @param {number} size - Einträge pro Seite
   * @param {string} sort - Sortierfeld und Richtung (z.B. 'id,asc')
   */
  async getAll(page = 0, size = 20, sort = 'id,asc') {
    const response = await apiClient.get('/api/analysis', {
      params: { page, size, sort }
    })
    return response.data
  }

  /**
   * Einzelne Analyse nach ID abrufen
   * @param {number} id - Analysis ID
   */
  async getById(id) {
    const response = await apiClient.get(`/api/analysis/${id}`)
    return response.data
  }

  /**
   * Neue Analyse erstellen
   * @param {Object} analysis - Analysis-Objekt
   */
  async create(analysis) {
    const response = await apiClient.post('/api/analysis', analysis)
    return response.data
  }

  /**
   * Analyse aktualisieren
   * @param {number} id - Analysis ID
   * @param {Object} analysis - Aktualisierte Daten
   */
  async update(id, analysis) {
    const response = await apiClient.put(`/api/analysis/${id}`, analysis)
    return response.data
  }

  /**
   * Analyse löschen
   * @param {number} id - Analysis ID
   */
  async delete(id) {
    await apiClient.delete(`/api/analysis/${id}`)
  }
}

export default new AnalysisService()
