<script setup>
import {onMounted, ref, computed, inject} from 'vue'
import {useRoute} from 'vue-router'
import router from '@/router/index.js'
import {useAuthStore} from '@/stores/auth.js'
import Map from '@/components/Map.vue'
import TourLogTile from "@/components/TourLogTile.vue";
import {useOpenRoute} from '@/composables/useOpenRoute.js'
import {useMapping} from "@/composables/useMapping.js";

const { getCoordsFromLocationName } = useOpenRoute()
const sidebarOpen = inject('sidebarOpen')

function closeSidebar() {
  sidebarOpen.value = false
}

const route = useRoute()
const auth = useAuthStore()
const tour = ref(null)
const logs = ref([])
const loading = ref(true)
const showLogForm = ref(false)
const startCoords = ref(null)
const endCoords = ref(null)

const newLog = ref({
  comment: '',
  difficulty: 'MEDIUM',
  totalDistance: 0,
  rating: 3,
})
const logTimeHours = ref(0)
const logTimeMinutes = ref(0)
const logDate = ref(new Date().toISOString().slice(0, 10))

onMounted(async () => {
  await fetchTour()
  await fetchLogs()
  if (tour.value) {
    const [start, end] = await Promise.all([
      getCoordsFromLocationName(tour.value.fromLocation),
      getCoordsFromLocationName(tour.value.toLocation),
    ])
    startCoords.value = start
    endCoords.value = end
  }
  loading.value = false
})

async function fetchTour() {
  const res = await fetch('/tours/' + route.params.id)
  if (res.ok) {
    tour.value = await res.json()
  }
}

async function fetchLogs() {
  const res = await fetch('/tours/' + route.params.id + '/logs')
  if (res.ok) {
    logs.value = await res.json()
  }
}

async function addLog() {
  const res = await fetch('/tours/' + route.params.id + '/logs', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      ...newLog.value,
      totalTime: hrsAndMinsToMinutes(logTimeHours.value, logTimeMinutes.value),
      dateTime: new Date(logDate.value).toISOString(),
      tourId: Number(route.params.id),
      userId: Number(auth.userId),
    }),
  })
  if (res.ok) {
    await fetchLogs()
    showLogForm.value = false
    newLog.value = { comment: '', difficulty: 'MEDIUM', totalDistance: 0, rating: 3 }
    logTimeHours.value = 0
    logTimeMinutes.value = 0
    logDate.value = new Date().toISOString().slice(0, 10)
  }
}

async function deleteTour() {
  const res = await fetch('/tours/' + route.params.id, { method: 'DELETE' })
  if (res.ok) {
    router.push('/')
  }
}

async function deleteLog(logId) {
  const res = await fetch('/tours/' + route.params.id + '/logs/' + logId, { method: 'DELETE' })
  if (res.ok) {
    await fetchLogs()
  }
}

function formatDate(dateTime) {
  return new Date(dateTime).toLocaleDateString('de-AT', {
    day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit',
  })
}

const { minutesToHrsAndMins, hrsAndMinsToMinutes } = useMapping()
const estimatedTime = computed(() => {
  if (!tour.value?.estimatedTime) return { hours: 0, minutes: 0 }
  return minutesToHrsAndMins(tour.value.estimatedTime)
})

</script>

<template>
  <div v-if="loading" class="loading">Loading...</div>

  <div v-else-if="!tour" class="loading">Tour not found.</div>

  <div v-else class="detail-layout">
    <div v-if="sidebarOpen" class="sidebar-overlay" @click="closeSidebar"></div>
    <div class="detail-sidebar" :class="{ open: sidebarOpen }">
      <div class="top-bar">
        <button class="close-sidebar-btn" @click="closeSidebar">&times;</button>
        <button class="back-btn" @click="router.push('/')">&#8592; Back</button>
        <button class="delete-tour-btn" @click="deleteTour">Delete Tour</button>
      </div>

      <div class="tour-info">
        <div class="tour-header">
          <h2>{{ tour.name }}</h2>
          <span class="transport-badge">{{ tour.transportType }}</span>
        </div>
        <p class="tour-route">{{ tour.fromLocation }} &rarr; {{ tour.toLocation }}</p>
        <p v-if="tour.userUsername" class="tour-author">by {{ tour.userUsername }}</p>
        <p v-if="tour.description" class="tour-description">{{ tour.description }}</p>
        <div class="tour-meta">
          <span v-if="tour.distanceKm"><strong>Distance:</strong> {{ tour.distanceKm }} km</span>
          <span v-if="tour.estimatedTime"><strong>Est. time:</strong> {{ estimatedTime.hours }} h {{ estimatedTime.minutes }} min</span>
        </div>
      </div>

      <div class="logs-section">
        <div class="logs-header">
          <h3>Tour Logs</h3>
          <button class="add-log-btn" @click="showLogForm = !showLogForm">
            {{ showLogForm ? 'Cancel' : '+ Add Log' }}
          </button>
        </div>

        <form v-if="showLogForm" class="log-form" @submit.prevent="addLog">
          <label>
            Date
            <input v-model="logDate" type="datetime-local" required>
          </label>
          <textarea v-model="newLog.comment" placeholder="Comment" rows="2"></textarea>
          <div class="form-row">
            <label>
              Difficulty
              <select v-model="newLog.difficulty">
                <option value="EASY">Easy</option>
                <option value="MEDIUM">Medium</option>
                <option value="HARD">Hard</option>
                <option value="EXPERT">Expert</option>
              </select>
            </label>
            <label>
              Rating
              <select v-model.number="newLog.rating">
                <option v-for="n in 5" :key="n" :value="n">{{ n }}</option>
              </select>
            </label>
          </div>
          <div class="form-row">
            <label>
              Distance (km)
              <input v-model.number="newLog.totalDistance" type="number" step="0.1" min="0">
            </label>
            <label>
              Time (h)
              <input v-model.number="logTimeHours" type="number" min="0">
            </label>
            <label>
              Time (min)
              <input v-model.number="logTimeMinutes" type="number" min="0" max="59">
            </label>
          </div>
          <button type="submit">Save Log</button>
        </form>

        <div v-if="logs.length === 0 && !showLogForm" class="empty-state">No logs yet.</div>

        <div v-for="log in logs" :key="log.id" class="log-entry">
          <TourLogTile :delete-log="() => deleteLog(log.id)" :format-date="formatDate(log.dateTime)"
                        :log="log"/>
        </div>
      </div>
    </div>

    <div class="map-panel">
      <Map :start="startCoords" :end="endCoords" :startName="tour.fromLocation" :endName="tour.toLocation" :transport-type="tour?.transportType" />
    </div>
  </div>
</template>

<style scoped>
.loading {
  display: flex;
  align-items: center;
  justify-content: center;
  flex: 1;
  color: #999;
  font-size: 1.1rem;
}

.detail-layout {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.detail-sidebar {
  width: 380px;
  min-width: 380px;
  background: #f5f6fa;
  border-right: 1px solid #ddd;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
  z-index: 10;
}

.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #ddd;
}

.back-btn {
  padding: 0.6rem 1rem;
  background: none;
  border: none;
  text-align: left;
  font-size: 0.9rem;
  cursor: pointer;
  color: #555;
}

.back-btn:hover {
  background: #eef1f7;
}

.delete-tour-btn {
  padding: 0.4rem 0.8rem;
  margin-right: 0.5rem;
  background: none;
  border: 1px solid #e53e3e;
  border-radius: 4px;
  color: #e53e3e;
  font-size: 0.8rem;
  cursor: pointer;
}

.delete-tour-btn:hover {
  background: #e53e3e;
  color: white;
}

.tour-info {
  padding: 1rem;
  border-bottom: 1px solid #ddd;
}

.tour-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.tour-header h2 {
  margin: 0;
  font-size: 1.2rem;
}

.transport-badge {
  font-size: 0.7rem;
  background: #d1d5db;
  padding: 0.15rem 0.4rem;
  border-radius: 4px;
  text-transform: lowercase;
}

.tour-route {
  margin: 0.4rem 0;
  font-size: 0.9rem;
  color: #555;
}

.tour-author {
  margin: 0.15rem 0;
  font-size: 0.8rem;
  color: #999;
  font-style: italic;
}

.tour-description {
  margin: 0.4rem 0;
  font-size: 0.85rem;
  color: #666;
}

.tour-meta {
  display: flex;
  gap: 1.5rem;
  font-size: 0.85rem;
  color: #555;
  margin-top: 0.5rem;
}

.logs-section {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.logs-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem 1rem;
  border-bottom: 1px solid #ddd;
}

.logs-header h3 {
  margin: 0;
}

.add-log-btn {
  font-size: 0.8rem;
  padding: 0.3rem 0.6rem;
  border: 1px solid #bbb;
  border-radius: 4px;
  background: white;
  color: black;
  cursor: pointer;
}

.add-log-btn:hover {
  background: #eef1f7;
}

.log-form {
  padding: 0.75rem 1rem;
  border-bottom: 1px solid #ddd;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.log-form textarea,
.log-form input,
.log-form select {
  width: 100%;
  padding: 0.4rem;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 0.85rem;
  box-sizing: border-box;
}

.log-form label {
  display: flex;
  flex-direction: column;
  gap: 0.2rem;
  font-size: 0.8rem;
  color: #555;
  flex: 1;
}

.form-row {
  display: flex;
  gap: 0.75rem;
}

.log-form button[type="submit"] {
  padding: 0.5rem;
  background: #4a90d9;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.85rem;
}

.log-form button[type="submit"]:hover {
  background: #3a7bc8;
}

.empty-state {
  padding: 2rem;
  text-align: center;
  color: #999;
  font-size: 0.9rem;
}

.log-entry {
  padding: 0.75rem 1rem;
  border-bottom: 1px solid #e0e0e0;
}

.map-panel {
  flex: 1;
  z-index: 0;
  position: relative;
}

.sidebar-overlay {
  display: none;
}

.close-sidebar-btn {
  display: none;
  background: none;
  border: none;
  font-size: 1.5rem;
  color: #555;
  cursor: pointer;
  padding: 0.4rem 0.6rem;
}

.close-sidebar-btn:hover {
  color: #e53e3e;
}

@media (max-width: 768px) {
  .detail-sidebar {
    position: fixed;
    top: 0;
    left: 0;
    bottom: 0;
    z-index: 100;
    width: 320px;
    min-width: 320px;
    transform: translateX(-100%);
    transition: transform 0.25s ease;
  }

  .detail-sidebar.open {
    transform: translateX(0);
  }

  .sidebar-overlay {
    display: block;
    position: fixed;
    inset: 0;
    z-index: 99;
    background: rgba(0, 0, 0, 0.4);
  }

  .close-sidebar-btn {
    display: block;
  }

  .map-panel {
    width: 100%;
    z-index: -1;
  }
}
</style>
