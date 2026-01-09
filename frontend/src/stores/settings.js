// stores/settings.js
import { defineStore } from 'pinia'
import { ref, watch } from 'vue'

export const useSettingsStore = defineStore('settings', () => {
  // Theme
  const isDarkMode = ref(false)
  
  // Spalten pro Tabelle
  const columnSettings = ref({
    analysis: {
      available: [
        { key: 'aId', label: 'ID', default: true },
        { key: 'sId', label: 'Sample ID', default: true },
        { key: 'sStamp', label: 'Timestamp', default: true },
        { key: 'pol', label: 'POL', default: true },
        { key: 'dateIn', label: 'Datum Ein', default: true },
        { key: 'aFlags', label: 'Flags', default: true }
      ],
      selected: []
    },
    sample: {
      available: [
        { key: 'sId', label: 'Sample ID', default: true },
        { key: 'sStamp', label: 'Timestamp', default: true },
        { key: 'name', label: 'Name', default: true },
        { key: 'weightNet', label: 'Gewicht Netto', default: true },
        { key: 'sFlags', label: 'Flags', default: true }
      ],
      selected: []
    },
    box: {
      available: [
        { key: 'bId', label: 'Box ID', default: true },
        { key: 'name', label: 'Name', default: true },
        { key: 'numMax', label: 'Max Positionen', default: true },
        { key: 'type', label: 'Typ', default: true }
      ],
      selected: []
    }
  })

  function initializeColumns() {
    Object.keys(columnSettings.value).forEach(table => {
      const config = columnSettings.value[table]
      if (config.selected.length === 0) {
        config.selected = config.available.filter(c => c.default).map(c => c.key)
      }
    })
  }

  function loadSettings() {
    try {
      const theme = localStorage.getItem('venlab-theme')
      isDarkMode.value = theme === 'dark'

      const cols = localStorage.getItem('venlab-columns')
      if (cols) {
        const parsed = JSON.parse(cols)
        Object.keys(parsed).forEach(table => {
          if (columnSettings.value[table]) {
            columnSettings.value[table].selected = parsed[table]
          }
        })
      } else {
        initializeColumns()
      }
    } catch (e) {
      initializeColumns()
    }
  }

  function saveSettings() {
    localStorage.setItem('venlab-theme', isDarkMode.value ? 'dark' : 'light')
    const cols = {}
    Object.keys(columnSettings.value).forEach(t => {
      cols[t] = columnSettings.value[t].selected
    })
    localStorage.setItem('venlab-columns', JSON.stringify(cols))
  }

  function toggleTheme() {
    isDarkMode.value = !isDarkMode.value
    saveSettings()
  }

  function setColumns(table, columns) {
    if (columnSettings.value[table]) {
      columnSettings.value[table].selected = columns
      saveSettings()
    }
  }

  function getColumns(table) {
    return columnSettings.value[table]?.selected || []
  }

  function getAvailableColumns(table) {
    return columnSettings.value[table]?.available || []
  }

  function getTableHeaders(table) {
    const config = columnSettings.value[table]
    if (!config) return []
    return config.available
      .filter(col => config.selected.includes(col.key))
      .map(col => ({ title: col.label, key: col.key, sortable: true }))
  }

  watch(isDarkMode, saveSettings)
  loadSettings()

  return {
    isDarkMode,
    columnSettings,
    toggleTheme,
    setColumns,
    getColumns,
    getAvailableColumns,
    getTableHeaders,
    loadSettings,
    saveSettings
  }
})
