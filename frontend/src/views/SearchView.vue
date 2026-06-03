<script setup>
import {ref, watch} from "vue";
import TourListTile from '@/components/TourListTile.vue'
import TourLogListTile from '@/components/TourLogListTile.vue'

var searchInput = ref('');

var tours = ref();
var tourLogs = ref();

var searchError = ref('');

async function getSearchResults() {
  try {
    const res = await fetch("/search?query=" + encodeURIComponent(searchInput.value))
    if (res.ok) {
      const data = await res.json()
      tours.value = data.foundTours
      tourLogs.value = data.foundLogs
    } else {
      searchError.value = 'Failed to find Search results'
    }
  } catch (e) {
    searchError.value = 'Failed to find Search results'
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
  <div class="search-wrapper">
    <input type="search" v-model="searchInput">
    <span>Found Tours: </span>
    <ul>
      <TourListTile v-for="t in tours" :key="t.id" :tour="t"/>
    </ul>

    <span>Found TourLogs: </span>
    <ul>
      <TourLogListTile v-for="tl in tourLogs" :key="tl.id" :tourLog="tl"/>
    </ul>
  </div>
</template>

<style scoped>

.search-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
}
</style>