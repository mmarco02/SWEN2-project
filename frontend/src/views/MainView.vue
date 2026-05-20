<script setup>
import {ref, onMounted} from 'vue'
import router from "@/router/index.js"
import {useAuthStore} from '@/stores/auth.js'
import TourListTile from '@/components/TourListTile.vue'

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
  transportType: 'BICYCLE',
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

onMounted(() => {
  fetchTours()
})
</script>

<template>
  <div class="main-layout">
    <aside class="sidebar">
      <div class="sidebar-header">
        <h2>Tours</h2>
      </div>

      <div class="tour-form">
        <h3>New Tour</h3>
        <input v-model="newTour.name" placeholder="Tour name">
        <input v-model="newTour.from" placeholder="From">
        <input v-model="newTour.to" placeholder="To">
        <textarea v-model="newTour.description" placeholder="Description" rows="2"></textarea>
        <select v-model="newTour.transportType">
          <option value="CAR">Car</option>
          <option value="BICYCLE">Bicycle</option>
          <option value="WALKING">Walking</option>
          <option value="BUS">Bus</option>
        </select>
        <button>Add Tour</button>
      </div>
    </aside>

    <section class="detail-panel">
      <div class="tour-list">
        <TourListTile
            v-for="tour in tours"
            :key="tour.id"
            :tour="tour"
            @select="selectedTour = $event"
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

.empty-state {
  padding: 2rem;
  text-align: center;
  color: #999;
}
</style>
