<script setup>

var tours;

tours = fetch("/tours")
</script>

<template>
  <h1>You did it!</h1>
  <p>
    Visit <a href="https://vuejs.org/" target="_blank" rel="noopener">vuejs.org</a> to read the
    documentation
  </p>

  <div v-for="(value, key) in tours" :key="key">
    {{ key }}: {{ value }}
  </div>

</template>

<style scoped></style>
