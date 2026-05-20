<script setup>
import {ref, watch, onMounted, onUnmounted} from 'vue'
import {useOpenRoute} from '@/composables/useOpenRoute.js'

const { getRoute } = useOpenRoute()

const props = defineProps({
  start: { type: Object, default: null },
  end: { type: Object, default: null },
  startName: {type: String, default: 'Start'},
  endName: {type: String, default: 'End'},
  transportType: { type: String, default: 'WALKING' },
})

const mapContainer = ref(null)
let map = null
let startMarker = null
let endMarker = null
let routeLayer = null

onMounted(() => {
  map = L.map(mapContainer.value).setView([47.5, 13.05], 7)

  L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>',
  }).addTo(map)

  updateMarkers()
})

onUnmounted(() => {
  if (map) {
    map.remove()
    map = null
  }
})

watch(() => [props.start, props.end], () => {
  updateMarkers()
})

function updateMarkers() {
  if (!map) return

  if (startMarker) { map.removeLayer(startMarker); startMarker = null }
  if (endMarker) { map.removeLayer(endMarker); endMarker = null }
  if (routeLayer) { map.removeLayer(routeLayer); routeLayer = null }

  if (props.start) {
    startMarker = L.marker([props.start.lat, props.start.lng])
      .addTo(map)
      .bindPopup(props.startName)
  }

  if (props.end) {
    endMarker = L.marker([props.end.lat, props.end.lng])
      .addTo(map)
      .bindPopup(props.endName)
  }

  if (props.start && props.end) {
    const bounds = L.latLngBounds(
      [props.start.lat, props.start.lng],
      [props.end.lat, props.end.lng],
    )
    map.fitBounds(bounds, { padding: [50, 50] })
    fetchRoute(props.start, props.end)
  } else if (props.start) {
    map.setView([props.start.lat, props.start.lng], 13)
  } else if (props.end) {
    map.setView([props.end.lat, props.end.lng], 13)
  }
}

async function fetchRoute(start, end) {
  const result = await getRoute(start, end, props.transportType)
  if (!result) return

  routeLayer = L.geoJSON(result.geojson, {
    style: { color: '#3388ff', weight: 4 },
  }).addTo(map)
  map.fitBounds(routeLayer.getBounds(), { padding: [50, 50] })
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
