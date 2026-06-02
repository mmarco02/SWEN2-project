<script setup>
defineProps({
  modelValue: { type: String, required: true },
  placeholder: { type: String, default: '' },
  required: { type: Boolean, default: false },
  suggestions: { type: Array, default: () => [] },
  showSuggestions: { type: Boolean, default: false },
})

const emit = defineEmits(['update:modelValue', 'input', 'select', 'focusout'])

function onInputChange(e) {
  const value = e.target.value
  emit('update:modelValue', value)
  emit('input', value)
}

function onSuggestionPick(label) {
  emit('update:modelValue', label)
  emit('select', label)
}

function onWrapperFocusOut(e) {
  if (!e.currentTarget.contains(e.relatedTarget)) {
    emit('focusout')
  }
}
</script>

<template>
  <div class="autocomplete-wrapper" @focusout="onWrapperFocusOut">
    <input
      :value="modelValue"
      :placeholder="placeholder"
      :required="required"
      @input="onInputChange"
    >
    <ul v-if="showSuggestions" class="autocomplete-list">
      <li
        v-for="s in suggestions"
        :key="s"
        tabindex="0"
        @click="onSuggestionPick(s)"
        @keydown.enter.prevent="onSuggestionPick(s)"
      >{{ s }}</li>
    </ul>
  </div>
</template>

<style scoped>
.autocomplete-wrapper {
  position: relative;
}

.autocomplete-wrapper input {
  width: 100%;
  box-sizing: border-box;
}

.autocomplete-list {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  margin: 0;
  padding: 0;
  list-style: none;
  background: white;
  border: 1px solid #ccc;
  border-top: none;
  border-radius: 0 0 4px 4px;
  max-height: 180px;
  overflow-y: auto;
  z-index: 50;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.autocomplete-list li {
  padding: 0.4rem 0.6rem;
  font-size: 0.85rem;
  cursor: pointer;
  color: #333;
}

.autocomplete-list li:hover,
.autocomplete-list li:focus {
  background: #eef1f7;
  outline: none;
}
</style>
