<template>
  <v-menu :close-on-content-click="false">
    <template v-slot:activator="{ props }">
      <v-btn v-bind="props" variant="outlined" size="small" prepend-icon="mdi-table-column">
        Spalten ({{ selectedCount }})
      </v-btn>
    </template>
    <v-card min-width="250">
      <v-card-title class="text-subtitle-1">Spalten auswählen</v-card-title>
      <v-card-text>
        <v-checkbox
          v-for="col in availableColumns"
          :key="col.key"
          v-model="selectedColumns"
          :label="col.label"
          :value="col.key"
          density="compact"
          hide-details
        />
      </v-card-text>
    </v-card>
  </v-menu>
</template>

<script setup>
import { computed } from 'vue'
import { useSettingsStore } from '@/stores/settings'

const props = defineProps({ table: { type: String, required: true } })
const emit = defineEmits(['update:columns'])
const settings = useSettingsStore()

const availableColumns = computed(() => settings.getAvailableColumns(props.table))
const selectedColumns = computed({
  get: () => settings.getColumns(props.table),
  set: (val) => {
    settings.setColumns(props.table, val)
    emit('update:columns', val)
  }
})
const selectedCount = computed(() => selectedColumns.value.length)
</script>
