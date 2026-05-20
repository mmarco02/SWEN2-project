<script setup>
import {ref} from 'vue'
import router from "@/router/index.js";
import {useAuthStore} from '@/stores/auth.js'

const auth = useAuthStore()
const username = ref('')
const password = ref('')

async function login() {
  const response = await fetch('/users/login', {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify({username: username.value, password: password.value}),
  })

  if (response.ok) {
    const userId = await response.text()
    auth.login(username.value, userId)
    await router.push("/")
  } else {
    alert('Login failed')
  }
}
</script>

<template>
  <div class="login-wrapper">
    <h1>Login</h1>
    <form @submit.prevent="login">
      <div class="form-wrapper">
        <input class="username-input" v-model="username" placeholder="Username">
        <input class="password-input" v-model="password" type="password" placeholder="Password">
        <button type="submit">Log In</button>
        <span>Don't have an Account yet?` <RouterLink to="/register"><b>Register here</b></RouterLink> </span>
      </div>
    </form>
  </div>

</template>

<style scoped>
.login-wrapper {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  width: 100%;
  flex: 1;
  gap: 1.5rem;
}

.form-wrapper {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 1.5rem;
}
</style>