import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '@/views/LoginView.vue'
import MainView from "@/views/MainView.vue";
import RegisterView from "@/views/RegisterView.vue";
import TourDetailView from "@/views/TourDetailView.vue";
import SearchView from "@/views/SearchView.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'main',
      component: MainView,
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
    },
    {
      path: '/register',
      name: 'register',
      component: RegisterView,
    },
    {
      path: '/tour/:id',
      name: 'tour',
      component: TourDetailView,
    },
    {
      path: '/search',
      name: 'search',
      component: SearchView,
    }
  ],
})

export default router
