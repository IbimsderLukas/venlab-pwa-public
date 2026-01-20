import apiClient from './axios'

/**
 * BoxPos API Service
 * Vollständige CRUD-Operationen für BoxPos-Entitäten (Box-Positionen)
 * Hinweis: BoxPos hat zusammengesetzten Schlüssel (b_id, bpos_id)
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
   * @param {string} boxId - Box ID
   * @param {number} posId - Position ID
   */
  async getById(boxId, posId) {
    const response = await apiClient.get(`/api/boxpos/${boxId}/${posId}`)
    return response.data
  }

  /**
   * Alle Positionen einer bestimmten Box abrufen
   * @param {string} boxId - Box ID
   */
  async getByBoxId(boxId) {
    const response = await apiClient.get(`/api/boxpos/box/${boxId}`)
    return response.data
  }

  /**
   * Neue BoxPos erstellen
   * @param {Object} boxPos - BoxPos-Objekt
   */
  async create(boxPos) {
    const response = await apiClient.post('/api/boxpos', boxPos)
    return response.data
  }

  /**
   * BoxPos aktualisieren
   * @param {string} boxId - Box ID
   * @param {number} posId - Position ID
   * @param {Object} boxPos - Aktualisierte Daten
   */
  async update(boxId, posId, boxPos) {
    const response = await apiClient.put(`/api/boxpos/${boxId}/${posId}`, boxPos)
    return response.data
  }

  /**
   * BoxPos löschen
   * @param {string} boxId - Box ID
   * @param {number} posId - Position ID
   */
  async delete(boxId, posId) {
    await apiClient.delete(`/api/boxpos/${boxId}/${posId}`)
  }
}

export default new BoxPosService()
