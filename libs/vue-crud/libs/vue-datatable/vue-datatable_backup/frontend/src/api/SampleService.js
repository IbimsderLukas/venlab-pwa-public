import apiClient from './axios'

/**
 * Sample API Service
 * Nur Lesezugriff (GET) gemäß Controller-Implementierung
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
   * Hinweis: Der Controller unterstützt derzeit nur getAll
   * Diese Methode ist für zukünftige Erweiterungen vorgesehen
   * @param {string} id - Sample ID
   * @param {string} stamp - Sample Timestamp (ISO-Format)
   */
  async getById(id, stamp) {
    // Falls der Endpoint erweitert wird:
    // const response = await apiClient.get(`/api/sample/${id}/${stamp}`)
    // return response.data
    console.warn('SampleService.getById ist noch nicht im Backend implementiert')
    return null
  }
}

export default new SampleService()
