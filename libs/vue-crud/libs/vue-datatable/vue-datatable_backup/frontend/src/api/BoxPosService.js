import apiClient from './axios'

/**
 * BoxPos API Service
 * Lesezugriff auf BoxPos-Entitäten (Box-Positionen)
 */
class BoxPosService {
  /**
   * Alle Box-Positionen abrufen
   */
  async getAll() {
    const response = await apiClient.get('/api/boxpos')
    return response.data
  }

  /**
   * BoxPos nach zusammengesetztem Schlüssel abrufen
   * Hinweis: Der Controller unterstützt derzeit nur getAll
   * @param {string} boxId - Box ID
   * @param {number} posId - Position ID
   */
  async getById(boxId, posId) {
    // Falls der Endpoint erweitert wird:
    // const response = await apiClient.get(`/api/boxpos/${boxId}/${posId}`)
    // return response.data
    console.warn('BoxPosService.getById ist noch nicht im Backend implementiert')
    return null
  }
}

export default new BoxPosService()
