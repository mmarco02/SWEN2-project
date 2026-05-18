<script setup>
import { RouterView, RouterLink } from 'vue-router'
import { useAuthStore } from '@/stores/auth.js'

const auth = useAuthStore()
</script>

<template>
  <nav class="navbar">
    <RouterLink to="/" class="nav-main">Tour Planner</RouterLink>
    <div class="nav-links">
      <RouterLink to="/">Home</RouterLink>
      <RouterLink v-if="auth.isLoggedIn" @click="auth.logout()" to="/login">Log Out</RouterLink>
      <RouterLink v-else to="/login">Log In</RouterLink>
    </div>
  </nav>
  <main class="content">
    <RouterView />
  </main>
</template>

<style scoped>
.navbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0.75rem 1.5rem;
  background-color: #2c3e50;
  color: #fff;
}

.nav-main {
  font-size: 1.25rem;
  font-weight: bold;
  color: #fff;
  text-decoration: none;
}

.nav-links {
  display: flex;
  gap: 1rem;
}

.nav-links a {
  color: #ecf0f1;
  text-decoration: none;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.nav-links a:hover,
.nav-links a.router-link-active {
  background-color: rgba(255, 255, 255, 0.15);
}

.content {
  padding: 1.5rem;
}
</style>
