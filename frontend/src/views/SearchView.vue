<script setup>
import {ref, watch} from "vue";
import router from "@/router/index.js";
import TourListTile from '@/components/TourListTile.vue'
import TourLogListTile from '@/components/TourLogListTile.vue'

const searchInput = ref('');
const tours = ref([]);
const tourLogs = ref([]);
const searchError = ref('');

async function getSearchResults() {
  if (!searchInput.value.trim()) {
    tours.value = []
    tourLogs.value = []
    searchError.value = ''
    return
  }
  try {
    const res = await fetch("/search?query=" + encodeURIComponent(searchInput.value))
    if (res.ok) {
      const data = await res.json()
      tours.value = data.foundTours
      tourLogs.value = data.foundLogs
      searchError.value = ''
    } else {
      searchError.value = 'Failed to find search results'
    }
  } catch (e) {
    searchError.value = 'Failed to find search results'
  }
}

let timeout = null

watch(searchInput, () => {
  clearTimeout(timeout)
  timeout = setTimeout(() => {
    getSearchResults()
  }, 500)
})
</script>

<template>
  <div class="search-page">
    <div class="search-header">
      <button class="back-btn" @click="router.push('/')">&larr; Back</button>
      <h2>Search Tours & Logs</h2>
    </div>

    <div class="search-bar">
      <input type="search" v-model="searchInput" placeholder="Search tours and logs...">
    </div>

    <div v-if="searchError" class="search-error">{{ searchError }}</div>

    <div v-if="!searchInput.trim()" class="empty-state">Type something to search across all tours and logs.</div>

    <template v-else>
      <div class="results-section">
        <h3>Tours ({{ tours.length }})</h3>
        <div class="results-list" v-if="tours.length">
          <TourListTile v-for="t in tours" :key="t.id" :tour="t"/>
        </div>
        <p v-else class="no-results">No tours found.</p>
      </div>

      <div class="results-section">
        <h3>Tour Logs ({{ tourLogs.length }})</h3>
        <div class="results-list" v-if="tourLogs.length">
          <TourLogListTile v-for="tl in tourLogs" :key="tl.id" :tourLog="tl"/>
        </div>
        <p v-else class="no-results">No tour logs found.</p>
      </div>
    </template>
  </div>
</template>

<style scoped>
.search-page {
  display: flex;
  flex-direction: column;
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
  padding: 1.5rem;
  overflow-y: auto;
}

.search-header {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1rem;
}

.search-header h2 {
  font-size: 1.25rem;
}

.back-btn {
  padding: 0.35rem 0.75rem;
  font-size: 0.85rem;
  background: none;
  border: 1px solid var(--color-border);
  color: var(--color-text);
  border-radius: 6px;
  cursor: pointer;
}

.back-btn:hover {
  background: var(--color-bg-hover);
}

.search-bar {
  margin-bottom: 1.5rem;
}

.search-bar input {
  width: 100%;
  padding: 0.6rem 1rem;
  font-size: 1rem;
}

.search-error {
  color: var(--color-danger);
  font-size: 0.85rem;
  margin-bottom: 1rem;
}

.results-section {
  margin-bottom: 1.5rem;
}

.results-section h3 {
  font-size: 1rem;
  padding-bottom: 0.5rem;
  border-bottom: 1px solid var(--color-border);
  margin-bottom: 0.25rem;
  color: var(--color-text-secondary);
}

.results-list {
  display: flex;
  flex-direction: column;
}

.no-results {
  padding: 1rem;
  text-align: center;
  color: var(--color-text-muted);
  font-size: 0.85rem;
}
</style>
