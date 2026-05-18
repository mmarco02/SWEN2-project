<script setup>
import router from "@/router/index.js";
import { useAuthStore } from '@/stores/auth.js'

const auth = useAuthStore()

if (!auth.isLoggedIn) {
  router.push("/login")
}
</script>

<template>
  <h1>Welcome {{ auth.username }}</h1>
</template>

<style scoped>

</style>