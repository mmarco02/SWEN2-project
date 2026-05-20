const API_KEY = import.meta.env.VITE_OPEN_ROUTE_API_KEY
const BASE_URL = 'https://api.openrouteservice.org'

const TRANSPORT_PROFILES = {
  CAR: 'driving-car',
  BICYCLE: 'cycling-regular',
  BUS: 'driving-car',
  WALKING: 'foot-walking',
}

function getProfile(transportType) {
  return TRANSPORT_PROFILES[transportType] || 'driving-car'
}

export function useOpenRoute() {
  async function getCoordsFromLocationName(locationName) {
    const res = await fetch(
      `${BASE_URL}/geocode/search?api_key=${API_KEY}&text=${encodeURIComponent(locationName)}`
    )
    if (!res.ok) return null
    const data = await res.json()
    const coords = data.features?.[0]?.geometry?.coordinates
    return coords ? { lng: coords[0], lat: coords[1] } : null
  }

  async function getLocationNameFromCoords(lat, lng) {
    const res = await fetch(
      `${BASE_URL}/geocode/reverse?api_key=${API_KEY}&point.lat=${lat}&point.lon=${lng}`
    )
    if (!res.ok) return null
    const data = await res.json()
    return data.features?.[0]?.properties?.label || null
  }

  async function getRoute(start, end, transportType = 'CAR') {
    const profile = getProfile(transportType)
    const res = await fetch(`${BASE_URL}/v2/directions/${profile}/geojson`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': API_KEY,
      },
      body: JSON.stringify({
        coordinates: [
          [start.lng, start.lat],
          [end.lng, end.lat],
        ],
      }),
    })
    if (!res.ok) return null
    const data = await res.json()
    const segment = data.features?.[0]?.properties?.segments?.[0]
    return {
      geojson: data,
      distanceKm: segment ? +(segment.distance / 1000).toFixed(2) : null,
      estimatedTimeMin: segment ? Math.round(segment.duration / 60) : null,
    }
  }

  async function getDistanceAndTime(fromLocation, toLocation, transportType = 'CAR') {
    const [start, end] = await Promise.all([
      getCoordsFromLocationName(fromLocation),
      getCoordsFromLocationName(toLocation),
    ])
    if (!start || !end) return null
    const route = await getRoute(start, end, transportType)
    if (!route) return null
    return {
      startCoords: start,
      endCoords: end,
      distanceKm: route.distanceKm,
      estimatedTimeMin: route.estimatedTimeMin,
      geojson: route.geojson,
    }
  }

  return {
    getCoordsFromLocationName,
    getLocationNameFromCoords,
    getRoute,
    getDistanceAndTime,
  }
}
