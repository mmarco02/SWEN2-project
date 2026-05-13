<script setup>
import { ref, onMounted } from 'vue'

const tours = ref([])

onMounted(async () => {
  const response = await fetch('/tours')
  tours.value = await response.json()
})
</script>

<template>
  <h1>You did it!</h1>
  <p>
    Visit <a href="https://vuejs.org/" target="_blank" rel="noopener">vuejs.org</a> to read the
    documentation
  </p>

  <p v-if="tours.length === 0">No tours found.</p>
  <div v-for="tour in tours" :key="tour.id">
    <h3>{{ tour.name }}</h3>
    <p>from: {{ tour.fromLocation }} to: {{ tour.toLocation }}</p>
    <p>{{ tour.description }}</p>
  </div>

</template>

<style scoped></style>
