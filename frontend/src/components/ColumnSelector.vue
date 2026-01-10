<template>
  <v-menu :close-on-content-click="false" location="bottom end">
    <template v-slot:activator="{ props }">
      <v-btn v-bind="props" variant="outlined" size="small" prepend-icon="mdi-table-column">
        Spalten ({{ selectedCount }}/{{ totalCount }})
      </v-btn>
    </template>
    
    <v-card min-width="250" max-height="400">
      <v-card-title class="text-subtitle-1 pb-0">
        Spalten auswählen
      </v-card-title>
      
      <v-card-text class="pt-2">
        <v-checkbox
          v-for="col in availableColumns"
          :key="col.key"
          v-model="selectedColumns"
          :label="col.label"
          :value="col.key"
          density="compact"
          hide-details
          class="my-1"
        />
      </v-card-text>
      
      <v-divider />
      
      <v-card-actions>
        <v-btn size="small" variant="text" @click="selectAll">Alle</v-btn>
        <v-btn size="small" variant="text" @click="selectNone">Keine</v-btn>
        <v-btn size="small" variant="text" @click="selectDefault">Standard</v-btn>
      </v-card-actions>
    </v-card>
  </v-menu>
</template>

<script setup>
import { computed } from 'vue'
import { useSettingsStore } from '@/stores/settings'

const props = defineProps({
  table: { type: String, required: true }
})

const emit = defineEmits(['update'])

const settings = useSettingsStore()

const availableColumns = computed(() => settings.getAvailableColumns(props.table))

const selectedColumns = computed({
  get: () => settings.getSelectedColumns(props.table),
  set: (val) => {
    settings.setColumns(props.table, val)
    emit('update', val)
  }
})

const selectedCount = computed(() => selectedColumns.value.length)
const totalCount = computed(() => availableColumns.value.length)

function selectAll() {
  selectedColumns.value = availableColumns.value.map(c => c.key)
}

function selectNone() {
  selectedColumns.value = []
}

function selectDefault() {
  selectedColumns.value = availableColumns.value
    .filter(c => c.default)
    .map(c => c.key)
}
</script>
