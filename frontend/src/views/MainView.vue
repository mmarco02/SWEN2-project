<script setup>
import {ref} from 'vue'
import router from "@/router/index.js";
import {useAuthStore} from '@/stores/auth.js'

const auth = useAuthStore()

if (!auth.isLoggedIn) {
  router.push("/login")
}

const tours = ref([])

const newTour = ref({
  name: '',
  description: '',
  from: '',
  to: '',
  transportType: 'bike',
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
          <option value="1" selected="selected">1</option>
          <option value="2">2</option>
          <option value="3">3</option>
        </select>
        <button>Add Tour</button>
      </div>
    </aside>

    <section class="detail-panel">
      <span> MAP Placeholder </span>
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
  padding: 1.5rem;
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>