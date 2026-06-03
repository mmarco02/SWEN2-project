<script setup>
defineProps({
  deleteLog: {type: Function, required: true},
  formatDate: {type: String, required: true},
  log: {type: Object, required: true},
})
</script>

<template>
  <div class="log-header">
    <span class="log-date">{{ formatDate }}</span>
    <span class="difficulty-badge" :class="log.difficulty.toLowerCase()">{{ log.difficulty }}</span>
    <button class="delete-btn" @click.stop="deleteLog"> delete</button>
  </div>
  <p v-if="log.comment" class="log-comment">{{ log.comment }}</p>
  <div class="log-meta">
    <span>{{ log.totalDistance }} km</span>
    <span>{{ log.totalTime }} min</span>
    <span>Rating: {{ log.rating }}/5</span>
  </div>
</template>

<style scoped>
.log-header {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.log-date {
  font-size: 0.8rem;
  color: var(--color-text-muted);
}

.difficulty-badge {
  font-size: 0.65rem;
  padding: 0.1rem 0.35rem;
  border-radius: 4px;
  text-transform: lowercase;
}

.log-comment {
  margin: 0.3rem 0;
  font-size: 0.85rem;
  color: var(--color-text-secondary);
}

.log-meta {
  display: flex;
  gap: 1rem;
  font-size: 0.75rem;
  color: var(--color-text-muted);
  margin-top: 0.2rem;
}
</style>