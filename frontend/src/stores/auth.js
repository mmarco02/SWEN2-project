import { ref, computed } from 'vue'
import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', () => {
  const username = ref(sessionStorage.getItem("loggedInUser"))
  const isLoggedIn = computed(() => !!username.value)

  function login(user) {
    sessionStorage.setItem("loggedInUser", user)
    username.value = user
  }

  function logout() {
    sessionStorage.removeItem("loggedInUser")
    username.value = null
  }

  return { username, isLoggedIn, login, logout }
})
