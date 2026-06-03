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
:root {
  --color-primary: #2c3e50;
  --color-primary-hover: #1a252f;
  --color-accent: #4a90d9;
  --color-accent-hover: #3a7bc8;
  --color-danger: #e53e3e;

  --color-bg-secondary: #f5f6fa;
  --color-bg-hover: #eef1f7;
  --color-bg-badge: #d1d5db;

  --color-border: #ddd;
  --color-border-input: #ccc;

  --color-text: #333;
  --color-text-secondary: #555;
  --color-text-muted: #888;
}

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
  height: 100vh;
  font-family: Roboto, sans-serif;
  color: var(--color-text);
  overflow: hidden;
}

input, select, textarea {
  padding: 0.5rem 0.75rem;
  border: 1px solid var(--color-border-input);
  border-radius: 6px;
  font-size: 0.95rem;
  outline: none;
  transition: border-color 0.2s;
}

input:focus, select:focus, textarea:focus {
  border-color: var(--color-primary);
}

button {
  padding: 0.5rem 1.25rem;
  border: none;
  border-radius: 6px;
  background-color: var(--color-primary);
  color: #fff;
  font-size: 0.95rem;
  cursor: pointer;
  transition: background-color 0.2s;
}

button:hover {
  background-color: var(--color-primary-hover);
}

/* Shared classes */

.transport-badge {
  font-size: 0.7rem;
  background: var(--color-bg-badge);
  padding: 0.15rem 0.4rem;
  border-radius: 4px;
  text-transform: lowercase;
}

.delete-btn {
  margin-left: auto;
  padding: 0.1rem 0.4rem;
  font-size: 0.65rem;
  background: none;
  border: 1px solid var(--color-danger);
  border-radius: 3px;
  color: var(--color-danger);
  cursor: pointer;
}

.delete-btn:hover {
  background: var(--color-danger);
  color: white;
}

.empty-state {
  padding: 2rem;
  text-align: center;
  color: var(--color-text-muted);
  font-size: 0.9rem;
}

.sidebar-overlay {
  display: none;
}

.sidebar-close-btn {
  display: none;
  background: none;
  border: none;
  font-size: 1.5rem;
  color: var(--color-text-secondary);
  cursor: pointer;
  padding: 0.3rem 0.6rem;
}

.sidebar-close-btn:hover {
  color: var(--color-danger);
}

@media (max-width: 768px) {
  .sidebar-overlay {
    display: block;
    position: fixed;
    inset: 0;
    z-index: 99;
    background: rgba(0, 0, 0, 0.4);
  }

  .sidebar-close-btn {
    display: block;
  }
}
</style>

<style scoped>
.navbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0.75rem 1.5rem;
  background-color: var(--color-primary);
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
  overflow: hidden;
}

@media (max-width: 768px) {
  .menu-toggle {
    display: block;
  }
}
</style>
