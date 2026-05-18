<script setup>
import { ref } from 'vue'
import router from "@/router/index.js";
import { useAuthStore } from '@/stores/auth.js'

const auth = useAuthStore()
const username = ref('')
const password = ref('')

async function register() {
  const response = await fetch('/users/register', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username: username.value, password: password.value }),
  })

  if (response.ok) {
    auth.login(username.value)
    await router.push("/")
  } else if (response.status === 409) {
    alert('Username is already taken')
  } else {
    alert('Registration failed')
  }
}
</script>

<template>
  <h1>Register</h1>
  <form @submit.prevent="register">
    <input class="username-input" v-model="username" placeholder="Username">
    <input class="password-input" v-model="password" type="password" placeholder="Password">
    <button type="submit"> Register </button>
  </form>
  <span>Already have an Account?` <RouterLink to="/login"><b>Log in here</b></RouterLink> </span>
</template>

<style scoped>

</style>