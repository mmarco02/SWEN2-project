<script setup>
import { ref } from 'vue'
import router from "@/router/index.js";
import { useAuthStore } from '@/stores/auth.js'

const auth = useAuthStore()
const username = ref('')
const password = ref('')

async function login() {
  const response = await fetch('/users/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username: username.value, password: password.value }),
  })

  if (response.ok) {
    auth.login(username.value)
    await router.push("/")
  } else {
    alert('Login failed')
  }
}
</script>

<template>
  <h1>Login</h1>
  <form @submit.prevent="login">
    <input class="username-input" v-model="username" placeholder="Username">
    <input class="password-input" v-model="password" type="password" placeholder="Password">
    <button type="submit">Log In</button>
  </form>
  <span>Don't have an Account yet?` <RouterLink to="/register"><b>Register here</b></RouterLink> </span>
</template>

<style scoped>

</style>