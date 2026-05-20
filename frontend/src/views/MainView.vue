<script setup>
import {ref, inject, onMounted, watch, computed} from 'vue'
import router from "@/router/index.js"
import {useAuthStore} from '@/stores/auth.js'
import TourListTile from '@/components/TourListTile.vue'
import {useOpenRoute} from '@/composables/useOpenRoute.js'
import {useMapping} from "@/composables/useMapping.js"

const sidebarOpen = inject('sidebarOpen')

const auth = useAuthStore()

if (!auth.isLoggedIn) {
  router.push("/login")
}

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

async function saveTour() {
  const res = await fetch('/tours/create', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      name: newTour.value.name,
      description: newTour.value.description,
      fromLocation: newTour.value.from,
      toLocation: newTour.value.to,
      transportType: newTour.value.transportType,
      distanceKm: routeEstimate.value?.distanceKm ?? 0,
      estimatedTime: routeEstimate.value?.estimatedTimeMin ?? 0,
      userId: Number(auth.userId),
    }),
  })
  if (res.ok) {
    await fetchTours()
    newTour.value = { name: '', description: '', from: '', to: '', transportType: 'CAR' }
    routeEstimate.value = null
  }
}

const { getDistanceAndTime } = useOpenRoute()
const { minutesToHrsAndMins } = useMapping()

const routeEstimate = ref()
const estimateHours = ref('')
const estimateMinutes = ref('')

let estimateTimeout = null
watch(
  () => [newTour.value.from, newTour.value.to, newTour.value.transportType],
  ([from, to, transportType]) => {
    routeEstimate.value = null
    clearTimeout(estimateTimeout)
    if (!from || !to) return
    estimateTimeout = setTimeout(async () => {
      routeEstimate.value = await getDistanceAndTime(from, to, transportType)
      if (routeEstimate.value) {
        const { hours, minutes } = minutesToHrsAndMins(routeEstimate.value.estimatedTimeMin)
        estimateHours.value = hours
        estimateMinutes.value = minutes
      }
    }, 500)
  },
)

function closeSidebar() {
  sidebarOpen.value = false
}

onMounted(() => {
  fetchTours()
})
</script>

<template>
  <div class="main-layout">
    <div v-if="sidebarOpen" class="sidebar-overlay" @click="sidebarOpen = false"></div>
    <aside class="sidebar" :class="{ open: sidebarOpen }">
      <button v-if="sidebarOpen" class="close-sidebar" @click="closeSidebar">&times;</button>
      <div class="sidebar-header">
        <h2>Your Tours</h2>
        <button @click="router.push('/search')">Search Tours</button>
      </div>

      <form class="tour-form" @submit.prevent="saveTour">
        <h3>New Tour</h3>
        <input v-model="newTour.name" placeholder="Tour name" required="required">
        <input v-model="newTour.from" placeholder="From" required="required">
        <input v-model="newTour.to" placeholder="To" required="required">
        <textarea v-model="newTour.description" placeholder="Description" rows="2"></textarea>
        <select v-model="newTour.transportType" required="required">
          <option value="CAR">Car</option>
          <option value="BICYCLE">Bicycle</option>
          <option value="WALKING">Walking</option>
          <option value="BUS">Bus</option>
        </select>
        <div class="route-estimation" v-if="routeEstimate">
          <span>Estimated Time: {{ estimateHours }} h {{ estimateMinutes }} min</span>
          <span>Estimated Distance: {{ routeEstimate.distanceKm }} km</span>
        </div>
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
  background: #f5f6fa;
  border-right: 1px solid #ddd;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
}

.sidebar-header {
  padding: 1rem;
  border-bottom: 1px solid #ddd;
}

.sidebar-header h2 {
  margin-bottom: 0.5rem;
}

.tour-form {
  padding: 1rem;
  border-bottom: 1px solid #ddd;
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
  justify-content: center;
}

.empty-state {
  padding: 2rem;
  text-align: center;
  color: #999;
}

.sidebar-overlay {
  display: none;
}

.close-sidebar {
  display: none;
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

  .sidebar-overlay {
    display: block;
    position: fixed;
    inset: 0;
    z-index: 99;
    background: rgba(0, 0, 0, 0.4);
  }

  .detail-panel {
    width: 100%;
  }

  .close-sidebar {
    display: block;
    background: none;
    border: none;
    font-size: 1.5rem;
    color: #555;
    cursor: pointer;
    padding: 0.3rem 0.6rem;
    align-self: flex-start;
  }

  .close-sidebar:hover {
    color: #e53e3e;
  }
}
</style>
