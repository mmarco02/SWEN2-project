<script setup>
import { RouterView, RouterLink } from 'vue-router'
import { useAuthStore } from '@/stores/auth.js'
import { provide, ref } from 'vue'

const auth = useAuthStore()
const sidebarOpen = ref(false)
provide('sidebarOpen', sidebarOpen)
</script>

<template>
  <nav class="navbar">
    <button class="menu-toggle" @click="sidebarOpen = !sidebarOpen">&#9776;</button>
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


<style>
*, *::before, *::after {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  height: 100%;
}

#app {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  font-family: Roboto, sans-serif;
  color: #333;
}

input, select, textarea {
  padding: 0.5rem 0.75rem;
  border: 1px solid #ccc;
  border-radius: 6px;
  font-size: 0.95rem;
  outline: none;
  transition: border-color 0.2s;
}

input:focus, select:focus, textarea:focus {
  border-color: #2c3e50;
}

button {
  padding: 0.5rem 1.25rem;
  border: none;
  border-radius: 6px;
  background-color: #2c3e50;
  color: #fff;
  font-size: 0.95rem;
  cursor: pointer;
  transition: background-color 0.2s;
}

button:hover {
  background-color: #1a252f;
}
</style>

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

.nav-links a:hover {
  background-color: rgba(255, 255, 255, 0.15);
}
/*
.nav-links a.router-link-active {
  background-color: rgba(255, 255, 255, 0.15);
}
 */

.menu-toggle {
  display: none;
  background: none;
  border: none;
  color: #fff;
  font-size: 1.4rem;
  cursor: pointer;
  padding: 0.25rem 0.5rem;
}

.menu-toggle:hover {
  background: rgba(255, 255, 255, 0.15);
}

.content {
  display: flex;
  flex: 1;
}

@media (max-width: 768px) {
  .menu-toggle {
    display: block;
  }
}
</style>
