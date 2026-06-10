<script setup>
import {ref, inject, onMounted, watch, computed} from 'vue'
import router from "@/router/index.js"
import {useAuthStore} from '@/stores/auth.js'
import TourListTile from '@/components/TourListTile.vue'
import AutocompleteInput from '@/components/AutocompleteInput.vue'
import {useOpenRoute} from '@/composables/useOpenRoute.js'
import {useAutocomplete} from '@/composables/useAutocomplete.js'
import {useMapping} from "@/composables/useMapping.js"

const sidebarOpen = inject('sidebarOpen')

const auth = useAuthStore()

const tours = ref([])
const selectedTour = ref(null)

const newTour = ref({
  name: '',
  description: '',
  from: '',
  to: '',
  transportType: 'CAR',
})

async function fetchTours() {
  try {
    const res = await fetch('/tours/users/' + auth.userId)
    if (res.ok) {
      tours.value = await res.json()
    }
  } catch (e) {
    console.error('Failed to fetch tours', e)
  }
}

const selectedImage = ref(null)

function onImageSelected(event) {
  selectedImage.value = event.target.files[0] || null
}

async function saveTour() {
  formError.value = ''

  const fromHasDigits = /\d/.test(newTour.value.from)
  const toHasDigits = /\d/.test(newTour.value.to)
  if ((fromHasDigits && !fromAc.isFromGeocode.value) || (toHasDigits && !toAc.isFromGeocode.value)) {
    formError.value = 'Numbers are not accepted unless the location is selected from suggestions.'
    return
  }

  if (newTour.value.from.trim() === newTour.value.to.trim()) {
    formError.value = 'From and To cannot be the same'
    return
  }

  const res = await fetch('/tours/create', {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify({
      name: newTour.value.name.trim(),
      description: newTour.value.description.trim(),
      fromLocation: newTour.value.from.trim(),
      toLocation: newTour.value.to.trim(),
      transportType: newTour.value.transportType,
      distanceKm: routeEstimate.value?.distanceKm ?? 0,
      estimatedTime: routeEstimate.value?.estimatedTimeMin ?? 0,
      userId: Number(auth.userId),
    }),
  })
  if (res.ok) {
    const createdTour = await res.json()
    if (selectedImage.value) {
      const formData = new FormData()
      formData.append('file', selectedImage.value)
      await fetch(`/tours/${createdTour.id}/image`, {
        method: 'POST',
        body: formData,
      })
    }
    await fetchTours()
    newTour.value = {name: '', description: '', from: '', to: '', transportType: 'CAR'}
    selectedImage.value = null
    routeEstimate.value = null
    estimateError.value = ''
    formError.value = ''
    fromAc.reset()
    toAc.reset()
  } else {
    formError.value = 'Failed to create tour'
  }
}

const {getDistanceAndTime} = useOpenRoute()
const {minutesToHrsAndMins} = useMapping()

const routeEstimate = ref()
const estimateHours = ref('')
const estimateMinutes = ref('')
const estimateError = ref('')
const formError = ref('')

const fromAc = useAutocomplete()
const toAc = useAutocomplete()

let estimateTimeout = null
watch(
    () => [newTour.value.from, newTour.value.to, newTour.value.transportType],
    ([from, to, transportType]) => {
      routeEstimate.value = null
      estimateError.value = ''
      clearTimeout(estimateTimeout)
      if (!from || !to) return
      estimateTimeout = setTimeout(async () => {
        const result = await getDistanceAndTime(from, to, transportType)
        if (result?.error) {
          estimateError.value = result.error
          routeEstimate.value = null
        } else {
          routeEstimate.value = result
          estimateError.value = ''
          if (result) {
            const {hours, minutes} = minutesToHrsAndMins(result.estimatedTimeMin)
            estimateHours.value = hours
            estimateMinutes.value = minutes
          }
        }
      }, 500)
    },
)

function closeSidebar() {
  sidebarOpen.value = false
}

const importFile = ref(null)
const importMessage = ref('')

async function exportTours() {
  try {
    const res = await fetch('/tours/export/' + auth.userId)
    if (!res.ok) { alert('Export failed'); return }
    const blob = await res.blob()
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = 'tours-export.csv'
    a.click()
    URL.revokeObjectURL(url)
  } catch (e) {
    alert('Export failed')
  }
}

function onImportFileSelected(event) {
  importFile.value = event.target.files[0] || null
}

async function importTours() {
  if (!importFile.value) return
  importMessage.value = ''
  const formData = new FormData()
  formData.append('file', importFile.value)
  try {
    const res = await fetch('/tours/import/' + auth.userId, {
      method: 'POST',
      body: formData,
    })
    if (res.ok) {
      const data = await res.json()
      importMessage.value = 'Imported ' + data.imported + ' entries'
      importFile.value = null
      await fetchTours()
    } else {
      importMessage.value = 'Import failed'
    }
  } catch (e) {
    importMessage.value = 'Import failed'
  }
}

onMounted(() => {
  fetchTours()
})
</script>

<template>
  <div class="main-layout">
    <div v-if="sidebarOpen" class="sidebar-overlay" @click="sidebarOpen = false"></div>
    <aside class="sidebar" :class="{ open: sidebarOpen }">
      <button v-if="sidebarOpen" class="sidebar-close-btn" @click="closeSidebar">&times;</button>
      <div class="sidebar-header">
        <h2>Your Tours</h2>
        <div class="header-actions">
          <button @click="router.push('/search')">Search Tours</button>
          <button @click="exportTours">Export CSV</button>
        </div>
        <div class="import-section">
          <label class="import-label">
            Import
            <input type="file" accept=".csv" class="import-input" @change="onImportFileSelected">
          </label>
          <button v-if="importFile" @click="importTours" class="import-btn">Upload</button>
          <span v-if="importMessage" class="import-message">{{ importMessage }}</span>
        </div>
      </div>

      <form class="tour-form" @submit.prevent="saveTour()">
        <h3>New Tour</h3>
        <input v-model="newTour.name" placeholder="Tour name" required="required">
        <AutocompleteInput
            v-model="newTour.from"
            placeholder="From"
            :required="true"
            :suggestions="fromAc.suggestions.value"
            :show-suggestions="fromAc.showSuggestions.value"
            @input="fromAc.onInput"
            @select="fromAc.onSelect"
            @focusout="fromAc.onFocusOut"
        />
        <AutocompleteInput
            v-model="newTour.to"
            placeholder="To"
            :required="true"
            :suggestions="toAc.suggestions.value"
            :show-suggestions="toAc.showSuggestions.value"
            @input="toAc.onInput"
            @select="toAc.onSelect"
            @focusout="toAc.onFocusOut"
        />
        <textarea v-model="newTour.description" placeholder="Description" rows="2"></textarea>
        <select v-model="newTour.transportType" required="required">
          <option value="CAR">Car</option>
          <option value="BICYCLE">Bicycle</option>
          <option value="WALKING">Walking</option>
          <option value="BUS">Bus</option>
        </select>
        <label class="image-upload-label">
          Upload image
          <input type="file" accept="image/*" class="image-upload" @change="onImageSelected">
        </label>
        <div class="route-estimation">
          <span class="estimate-info">Route estimation only works for distances under 6000 km</span>
          <div v-if="estimateError" class="estimate-error">{{ estimateError }}</div>
          <div class="estimate-row">
            <span>Estimated Time:</span>
            <span v-if="routeEstimate">{{ estimateHours }} h {{ estimateMinutes }} min</span>
            <span class="no-est" v-else>No Estimation Yet...</span>
          </div>
          <div class="estimate-row">
            <span>Estimated Distance:</span>
            <span v-if="routeEstimate">{{ routeEstimate.distanceKm }} km</span>
            <span class="no-est" v-else>No Estimation Yet...</span>
          </div>
        </div>
        <div v-if="formError" class="form-error">{{ formError }}</div>
        <button type="submit">Add Tour</button>
      </form>
    </aside>

    <section class="detail-panel">
      <div class="tour-list">
        <TourListTile
            v-for="tour in tours"
            :key="tour.id"
            :tour="tour"
            @select="selectedTour = $event"
            @deleted="fetchTours"
        />
        <p v-if="tours.length === 0" class="empty-state">No tours yet.</p>
      </div>
    </section>
  </div>
</template>

<style scoped>
.main-layout {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.sidebar {
  width: 320px;
  min-width: 320px;
  background: var(--color-bg-secondary);
  border-right: 1px solid var(--color-border);
  display: flex;
  flex-direction: column;
  overflow-y: auto;
}

.sidebar-header {
  padding: 1rem;
  border-bottom: 1px solid var(--color-border);
}

.sidebar-header h2 {
  margin-bottom: 0.5rem;
}

.header-actions {
  display: flex;
  gap: 0.5rem;
}

.import-section {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-top: 0.5rem;
  flex-wrap: wrap;
}

.import-label {
  font-size: 0.8rem;
  color: var(--color-text-secondary);
  cursor: pointer;
}

.import-input {
  width: 100%;
  font-size: 0.75rem;
}

.import-btn {
  font-size: 0.8rem;
  padding: 0.2rem 0.5rem;
}

.import-message {
  font-size: 0.75rem;
  color: var(--color-text-secondary);
}

.tour-form {
  padding: 1rem;
  border-bottom: 1px solid var(--color-border);
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.tour-form h3 {
  margin-bottom: 0.25rem;
}

.detail-panel {
  flex: 1;
  overflow-y: auto;
}

.tour-list {
  display: flex;
  flex-direction: column;
}

.route-estimation {
  display: flex;
  flex-direction: column;
  gap: 0.3rem;
  padding: 0.5rem;
  background: var(--color-bg-secondary);
  border-radius: 4px;
  font-size: 0.85rem;
}

.estimate-info {
  font-size: 0.75rem;
  color: var(--color-text-muted);
  font-style: italic;
}

.estimate-error {
  font-size: 0.8rem;
  color: var(--color-danger);
}

.estimate-row {
  display: flex;
  gap: 0.4rem;
}

.no-est {
  color: var(--color-text-secondary);
  opacity: 0.6;
}

.form-error {
  font-size: 0.8rem;
  color: var(--color-danger);
  padding: 0.3rem 0;
}

.image-upload {
  width: 100%;
}

@media (max-width: 768px) {
  .sidebar {
    position: fixed;
    top: 0;
    left: 0;
    bottom: 0;
    z-index: 100;
    transform: translateX(-100%);
    transition: transform 0.25s ease;
    width: 300px;
    min-width: 300px;
  }

  .sidebar.open {
    transform: translateX(0);
  }

  .detail-panel {
    width: 100%;
  }

  .sidebar-close-btn {
    align-self: flex-start;
  }
}
</style>
