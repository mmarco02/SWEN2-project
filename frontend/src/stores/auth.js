import { ref, computed } from 'vue'
import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', () => {
  const username = ref(sessionStorage.getItem("loggedInUser"))
  const userId = ref(sessionStorage.getItem("loggedInUserId"))
  const isLoggedIn = computed(() => !!username.value)

  function login(user, id) {
    sessionStorage.setItem("loggedInUser", user)
    sessionStorage.setItem("loggedInUserId", id)
    username.value = user
    userId.value = id
  }

  function logout() {
    sessionStorage.removeItem("loggedInUser")
    sessionStorage.removeItem("loggedInUserId")
    username.value = null
    userId.value = null
  }

  return { username, userId, isLoggedIn, login, logout }
})
