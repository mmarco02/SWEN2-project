import { ref } from 'vue'
import { useOpenRoute } from './useOpenRoute.js'

export function useAutocomplete() {
  const { getAutocomplete } = useOpenRoute()

  const suggestions = ref([])
  const showSuggestions = ref(false)
  const isFromGeocode = ref(false)
  let timeout = null

  function onInput(text) {
    isFromGeocode.value = false
    clearTimeout(timeout)
    if (!text || text.length < 2) {
      suggestions.value = []
      showSuggestions.value = false
      return
    }
    timeout = setTimeout(async () => {
      suggestions.value = await getAutocomplete(text)
      showSuggestions.value = suggestions.value.length > 0
    }, 300)
  }

  function onSelect() {
    isFromGeocode.value = true
    showSuggestions.value = false
  }

  function onFocusOut() {
    showSuggestions.value = false
  }

  function reset() {
    suggestions.value = []
    showSuggestions.value = false
    isFromGeocode.value = false
    clearTimeout(timeout)
  }

  return { suggestions, showSuggestions, isFromGeocode, onInput, onSelect, onFocusOut, reset }
}
