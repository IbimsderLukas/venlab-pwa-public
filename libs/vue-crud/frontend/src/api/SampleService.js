import apiClient from './axios'

/**
 * Sample API Service
 * Vollständige CRUD-Operationen für Sample-Entitäten
 * Hinweis: Sample hat zusammengesetzten Schlüssel (s_id, s_stamp)
 */
class SampleService {
  /**
   * Alle Samples abrufen (paginiert)
   * @param {number} page - Seitennummer (0-basiert)
   * @param {number} size - Einträge pro Seite
   */
  async getAll(page = 0, size = 20) {
    const response = await apiClient.get('/api/sample', {
      params: { page, size }
    })
    return response.data
  }

  /**
   * Sample nach zusammengesetztem Schlüssel abrufen
   * @param {string} sId - Sample ID
   * @param {string} sStamp - Sample Timestamp (ISO-Format)
   */
  async getById(sId, sStamp) {
    const response = await apiClient.get(`/api/sample/${encodeURIComponent(sId)}/${encodeURIComponent(sStamp)}`)
    return response.data
  }

  /**
   * Neues Sample erstellen
   * @param {Object} sample - Sample-Objekt
   */
  async create(sample) {
    const response = await apiClient.post('/api/sample', sample)
    return response.data
  }

  /**
   * Sample aktualisieren
   * @param {string} sId - Sample ID
   * @param {string} sStamp - Sample Timestamp (ISO-Format)
   * @param {Object} sample - Aktualisierte Daten
   */
  async update(sId, sStamp, sample) {
    const response = await apiClient.put(`/api/sample/${encodeURIComponent(sId)}/${encodeURIComponent(sStamp)}`, sample)
    return response.data
  }

  /**
   * Sample löschen
   * @param {string} sId - Sample ID
   * @param {string} sStamp - Sample Timestamp (ISO-Format)
   */
  async delete(sId, sStamp) {
    await apiClient.delete(`/api/sample/${encodeURIComponent(sId)}/${encodeURIComponent(sStamp)}`)
  }
}

export default new SampleService()
