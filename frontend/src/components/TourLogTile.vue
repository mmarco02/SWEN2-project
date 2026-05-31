<script setup>
defineProps({
  deleteLog: { type: Function, required: true },
  formatDate: { type: String, required: true },
  log: { type: Object, required: true },
})
</script>

<template>
  <div class="log-header">
    <span class="log-date">{{ formatDate }}</span>
    <span class="difficulty-badge" :class="log.difficulty.toLowerCase()">{{ log.difficulty }}</span>
    <button class="delete-btn" @click.stop="deleteLog">&times;</button>
  </div>
  <p v-if="log.comment" class="log-comment">{{ log.comment }}</p>
  <div class="log-meta">
    <span>{{ log.totalDistance }} km</span>
    <span>{{ log.totalTime }} min</span>
    <span>Rating: {{ log.rating }}/5</span>
  </div>
</template>

<style scoped>

.tour-header h2 {
  margin: 0;
  font-size: 1.2rem;
}

.logs-header h3 {
  margin: 0;
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

.log-header {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.log-date {
  font-size: 0.8rem;
  color: #777;
}

.difficulty-badge {
  font-size: 0.65rem;
  padding: 0.1rem 0.35rem;
  border-radius: 4px;
  text-transform: lowercase;
}

.delete-btn {
  margin-left: auto;
  background: none;
  border: none;
  font-size: 1.1rem;
  color: #999;
  cursor: pointer;
  padding: 0 0.3rem;
}

.delete-btn:hover {
  color: #e53e3e;
}

.log-comment {
  margin: 0.3rem 0;
  font-size: 0.85rem;
  color: #444;
}

.log-meta {
  display: flex;
  gap: 1rem;
  font-size: 0.75rem;
  color: #888;
  margin-top: 0.2rem;
}

</style>