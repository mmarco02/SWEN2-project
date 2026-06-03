<script setup>
import router from "@/router/index.js";

defineProps({
  tourLog: {
    type: Object,
    required: true,
  },
})
</script>

<template>
  <div class="tourlog-tile" @click="router.push('/tour/' + tourLog.tourId)">
    <div class="tourlog-header">
      <span class="tourlog-date">{{ new Date(tourLog.dateTime).toLocaleDateString() }}</span>
      <span class="difficulty-badge" :class="tourLog.difficulty.toLowerCase()">{{ tourLog.difficulty }}</span>
      <span class="rating">Rating: {{ tourLog.rating }}/5</span>
    </div>
    <p v-if="tourLog.comment" class="tourlog-comment">{{ tourLog.comment }}</p>
    <div class="tourlog-meta">
      <span>{{ tourLog.totalDistance }} km</span>
      <span>{{ tourLog.totalTime }} min</span>
    </div>
  </div>
</template>

<style scoped>
.tourlog-tile {
  display: flex;
  flex-direction: column;
  padding: 0.75rem 1rem;
  cursor: pointer;
  transition: background 0.15s;
  border-bottom: 1px solid var(--color-border);
}

.tourlog-tile:hover {
  background: var(--color-bg-hover);
}

.tourlog-header {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.tourlog-date {
  font-size: 0.8rem;
  color: var(--color-text-muted);
}

.difficulty-badge {
  font-size: 0.65rem;
  padding: 0.1rem 0.35rem;
  border-radius: 4px;
  text-transform: lowercase;
}

.rating {
  font-size: 0.75rem;
  color: var(--color-text-muted);
  margin-left: auto;
}

.tourlog-comment {
  margin: 0.3rem 0;
  font-size: 0.85rem;
  color: var(--color-text-secondary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.tourlog-meta {
  display: flex;
  gap: 1rem;
  font-size: 0.75rem;
  color: var(--color-text-muted);
  margin-top: 0.2rem;
}
</style>
