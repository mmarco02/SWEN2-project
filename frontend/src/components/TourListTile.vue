<script setup>
import router from "@/router/index.js";

const props = defineProps({
  tour: {
    type: Object,
    required: true,
  },
})

const emit = defineEmits(['deleted'])

function formatPopularity(val) {
  const labels = { POPULAR: 'Popular', AVERAGE: 'Average', UNPOPULAR: 'Unpopular' }
  return labels[val] || val
}

function formatChildFriendliness(val) {
  const labels = { CHILD_FRIENDLY: 'Child-Friendly', MODERATE: 'Moderate', NOT_CHILD_FRIENDLY: 'Not Child-Friendly' }
  return labels[val] || val
}

async function deleteTour() {
  if (!confirm('Are you sure you want to delete "' + props.tour.name + '"?')) return
  try {
    const res = await fetch('/tours/' + props.tour.id, { method: 'DELETE' })
    if (res.ok) {
      emit('deleted')
    } else {
      alert('Failed to delete tour.')
    }
  } catch (e) {
    alert('Failed to delete tour.')
  }
}

</script>

<template>
  <div class="tour-tile-wrapper">
    <img
        v-if="tour.imagePath"
        class="tour-thumbnail"
        :src="'/tours/' + tour.id + '/image'"
        :alt="tour.name + ' image'"
    >
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
      <div class="tour-badges" v-if="tour.popularity || tour.childFriendliness">
        <span v-if="tour.popularity" class="badge" :class="'popularity-' + tour.popularity.toLowerCase()">{{ formatPopularity(tour.popularity) }}</span>
        <span v-if="tour.childFriendliness" class="badge" :class="'cf-' + tour.childFriendliness.toLowerCase().replaceAll('_', '-')">{{ formatChildFriendliness(tour.childFriendliness) }}</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.tour-tile-wrapper {
  display: flex;
  flex-direction: row;
  align-items: stretch;
  border-bottom: 1px solid var(--color-border);
}

.tour-tile {
  display: flex;
  flex-direction: column;
  padding: 0.75rem 1rem;
  cursor: pointer;
  transition: background 0.15s;
  width: 100%;
}

.tour-thumbnail {
  width: 96px;
  min-width: 96px;
  height: 96px;
  object-fit: cover;
  align-self: center;
  margin-left: 0.75rem;
  border-radius: 6px;
  background: var(--color-border);
}

.tour-tile-wrapper:hover {
  background: var(--color-bg-hover);
}

.tour-tile:hover {
  background: transparent;
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

.tour-route {
  margin: 0.25rem 0;
  font-size: 0.85rem;
  color: var(--color-text-secondary);
}

.tour-author {
  margin: 0.15rem 0;
  font-size: 0.75rem;
  color: var(--color-text-muted);
  font-style: italic;
}

.tour-description {
  margin: 0.25rem 0;
  font-size: 0.8rem;
  color: var(--color-text-muted);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.tour-meta {
  display: flex;
  gap: 1rem;
  font-size: 0.75rem;
  color: var(--color-text-muted);
  margin-top: 0.25rem;
}

.tour-badges {
  display: flex;
  gap: 0.4rem;
  margin-top: 0.25rem;
  flex-wrap: wrap;
}

.badge {
  font-size: 0.65rem;
  padding: 0.1rem 0.4rem;
  border-radius: 10px;
  font-weight: 500;
}

.popularity-popular { background: #d4edda; color: #155724; }
.popularity-average { background: #fff3cd; color: #856404; }
.popularity-unpopular { background: #f8d7da; color: #721c24; }
.cf-child-friendly { background: #cce5ff; color: #004085; }
.cf-moderate { background: #fff3cd; color: #856404; }
.cf-not-child-friendly { background: #f8d7da; color: #721c24; }
</style>
