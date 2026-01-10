import { defineStore } from 'pinia'
import { ref, watch } from 'vue'

export const useSettingsStore = defineStore('settings', () => {
  const isDarkMode = ref(false)

  const tableColumns = ref({
    analysis: {
      available: [
        { key: 'id', label: 'ID', default: true },
        { key: 'sampleId', label: 'Sample ID', default: true },
        { key: 'stamp', label: 'Zeitstempel', default: true },
        { key: 'pol', label: 'POL', default: true },
        { key: 'nat', label: 'NAT', default: false },
        { key: 'kal', label: 'KAL', default: false },
        { key: 'an', label: 'AN', default: false },
        { key: 'glu', label: 'GLU', default: false },
        { key: 'dry', label: 'DRY', default: false },
        { key: 'dateIn', label: 'Datum Ein', default: true },
        { key: 'dateOut', label: 'Datum Aus', default: false },
        { key: 'flags', label: 'Flags', default: true }
      ],
      selected: []
    },
    sample: {
      available: [
        { key: 'id', label: 'ID', default: true },
        { key: 'stamp', label: 'Zeitstempel', default: true },
        { key: 'name', label: 'Name', default: true },
        { key: 'weightNet', label: 'Gewicht Netto', default: true },
        { key: 'weightBrutto', label: 'Gewicht Brutto', default: false },
        { key: 'quantity', label: 'Menge', default: false },
        { key: 'dateCrumbled', label: 'Zerkleinert', default: false },
        { key: 'flags', label: 'Flags', default: true }
      ],
      selected: []
    },
    box: {
      available: [
        { key: 'id', label: 'ID', default: true },
        { key: 'name', label: 'Name', default: true },
        { key: 'numMax', label: 'Max. Positionen', default: true },
        { key: 'type', label: 'Typ', default: true },
        { key: 'comment', label: 'Kommentar', default: false }
      ],
      selected: []
    },
    boxpos: {
      available: [
        { key: 'boxId', label: 'Box ID', default: true },
        { key: 'posId', label: 'Position', default: true },
        { key: 'sampleId', label: 'Sample ID', default: true },
        { key: 'stamp', label: 'Zeitstempel', default: true }
      ],
      selected: []
    },
    log: {
      available: [
        { key: 'id', label: 'ID', default: true },
        { key: 'dateCreated', label: 'Erstellt', default: true },
        { key: 'level', label: 'Level', default: true },
        { key: 'info', label: 'Info', default: true },
        { key: 'sampleId', label: 'Sample ID', default: false },
        { key: 'analysisId', label: 'Analysis ID', default: false }
      ],
      selected: []
    }
  })

  function initDefaults() {
    Object.keys(tableColumns.value).forEach(table => {
      const config = tableColumns.value[table]
      if (config.selected.length === 0) {
        config.selected = config.available
          .filter(col => col.default)
          .map(col => col.key)
      }
    })
  }

  function loadSettings() {
    try {
      const savedTheme = localStorage.getItem('venlab_theme')
      isDarkMode.value = savedTheme === 'dark'

      const savedColumns = localStorage.getItem('venlab_columns')
      if (savedColumns) {
        const parsed = JSON.parse(savedColumns)
        Object.keys(parsed).forEach(table => {
          if (tableColumns.value[table]) {
            tableColumns.value[table].selected = parsed[table]
          }
        })
      } else {
        initDefaults()
      }
    } catch (e) {
      initDefaults()
    }
  }

  function saveSettings() {
    localStorage.setItem('venlab_theme', isDarkMode.value ? 'dark' : 'light')
    
    const cols = {}
    Object.keys(tableColumns.value).forEach(t => {
      cols[t] = tableColumns.value[t].selected
    })
    localStorage.setItem('venlab_columns', JSON.stringify(cols))
  }

  function toggleTheme() {
    isDarkMode.value = !isDarkMode.value
    saveSettings()
  }

  function setColumns(table, columns) {
    if (tableColumns.value[table]) {
      tableColumns.value[table].selected = columns
      saveSettings()
    }
  }

  function getSelectedColumns(table) {
    return tableColumns.value[table]?.selected || []
  }

  function getAvailableColumns(table) {
    return tableColumns.value[table]?.available || []
  }

  function getTableHeaders(table) {
    const config = tableColumns.value[table]
    if (!config) return []
    
    return config.available
      .filter(col => config.selected.includes(col.key))
      .map(col => ({
        title: col.label,
        key: col.key,
        sortable: true
      }))
  }

  watch(isDarkMode, saveSettings)
  loadSettings()

  return {
    isDarkMode,
    tableColumns,
    toggleTheme,
    setColumns,
    getSelectedColumns,
    getAvailableColumns,
    getTableHeaders,
    saveSettings
  }
})
