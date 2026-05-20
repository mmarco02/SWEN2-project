<script setup>
import {ref, watch, onMounted, onUnmounted} from 'vue'

const props = defineProps({
  tour: {
    type: Object,
    default: null,
  },
})

const mapContainer = ref(null)
let map = null
let routeLayer = null

onMounted(() => {
  map = L.map(mapContainer.value).setView([47.5, 13.05], 7)

  L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>',
  }).addTo(map)

  if (props.tour) {
    renderRoute(props.tour)
  }
})

onUnmounted(() => {
  if (map) {
    map.remove()
    map = null
  }
})

watch(() => props.tour, (newTour) => {
  if (routeLayer) {
    map.removeLayer(routeLayer)
    routeLayer = null
  }
  if (newTour) {
    renderRoute(newTour)
  }
})

function renderRoute(tour) {
  if (!tour.route || !map) return

  try {
    const geojson = JSON.parse(tour.route)
    routeLayer = L.geoJSON(geojson, {
      style: {color: '#3388ff', weight: 4},
    }).addTo(map)
    map.fitBounds(routeLayer.getBounds(), {padding: [30, 30]})
  } catch {
    // route data isn't valid GeoJSON
  }
}
</script>

<template>
  <div ref="mapContainer" class="map-container"></div>
</template>

<style scoped>
.map-container {
  width: 100%;
  height: 100%;
}
</style>
