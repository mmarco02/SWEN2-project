<script setup>
import router from "@/router/index.js";

const props = defineProps({
  tour: {
    type: Object,
    required: true,
  },
})

const emit = defineEmits(['deleted'])

async function deleteTour() {
  const res = await fetch('/tours/' + props.tour.id, { method: 'DELETE' })
  if (res.ok) {
    emit('deleted')
  }
}

</script>

<template>
  <div class="tour-tile-wrapper">
    <div class="tour-tile" @click="router.push('/tour/' + tour.id)">
      <div class="tour-tile-header">
        <h4>{{ tour.name }}</h4>
        <span class="transport-badge">{{ tour.transportType }}</span>
        <button class="delete-btn" @click.stop="deleteTour">Delete</button>
      </div>
      <p class="tour-route">{{ tour.fromLocation }} &rarr; {{ tour.toLocation }}</p>
      <p v-if="tour.userUsername" class="tour-author">by {{ tour.userUsername }}</p>
      <p v-if="tour.description" class="tour-description">{{ tour.description }}</p>
      <div class="tour-meta">
        <span v-if="tour.distanceKm">{{ tour.distanceKm }} km</span>
        <span v-if="tour.estimatedTime">{{ tour.estimatedTime }} min</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.tour-tile-wrapper {
  display: flex;
  flex-direction: row;
}

.tour-tile {
  display: flex;
  flex-direction: column;
  padding: 0.75rem 1rem;
  border-bottom: 1px solid #e0e0e0;
  cursor: pointer;
  transition: background 0.15s;
  width: 100%;
}

.tour-tile:hover {
  background: #eef1f7;
}

.tour-tile-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.tour-tile-header h4 {
  margin: 0;
  font-size: 0.95rem;
}

.transport-badge {
  font-size: 0.7rem;
  background: #d1d5db;
  padding: 0.15rem 0.4rem;
  border-radius: 4px;
  text-transform: lowercase;
}

.tour-route {
  margin: 0.25rem 0;
  font-size: 0.85rem;
  color: #555;
}

.tour-author {
  margin: 0.15rem 0;
  font-size: 0.75rem;
  color: #999;
  font-style: italic;
}

.tour-description {
  margin: 0.25rem 0;
  font-size: 0.8rem;
  color: #777;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.tour-meta {
  display: flex;
  gap: 1rem;
  font-size: 0.75rem;
  color: #888;
  margin-top: 0.25rem;
}

.delete-btn {
  margin-left: auto;
  padding: 0.1rem 0.4rem;
  font-size: 0.65rem;
  background: none;
  border: 1px solid #e53e3e;
  border-radius: 3px;
  color: #e53e3e;
  cursor: pointer;
}

.delete-btn:hover {
  background: #e53e3e;
  color: white;
}
</style>
