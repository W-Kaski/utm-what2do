import { createRouter, createWebHistory } from 'vue-router';

import HomePage from '@/pages/HomePage.vue';
import MapPage from '@/pages/MapPage.vue';
import EventsPage from '@/pages/EventsPage.vue';
import EventDetailPage from '@/pages/EventDetailPage.vue';
import ClubDetailPage from '@/pages/ClubDetailPage.vue';
import ClubsExplorePage from '@/pages/ClubsExplorePage.vue';
import CreateEventPage from '@/pages/CreateEventPage.vue';
import ProfilePage from '@/pages/ProfilePage.vue';
import FeedPage from '@/pages/FeedPage.vue';
import PostDetailPage from '@/pages/PostDetailPage.vue';
import PostCreatePage from '@/pages/PostCreatePage.vue';
import SearchPage from '@/pages/SearchPage.vue';
import UserDetailPage from '@/pages/UserDetailPage.vue';
import LoginPage from '@/pages/LoginPage.vue';
import SettingsPage from '@/pages/SettingsPage.vue';

// Admin pages
import AdminDashboard from '@/pages/admin/AdminDashboard.vue';
import AdminUsers from '@/pages/admin/AdminUsers.vue';
import AdminEvents from '@/pages/admin/AdminEvents.vue';
import AdminClubs from '@/pages/admin/AdminClubs.vue';

const routes = [
  { path: '/', name: 'home', component: HomePage },
  { path: '/login', name: 'login', component: LoginPage },
  { path: '/map', name: 'map', component: MapPage },
  { path: '/events', name: 'events', component: EventsPage },
  { path: '/explore', name: 'explore', component: EventsPage },
  { path: '/events/:id', name: 'event-detail', component: EventDetailPage, props: true },
  { path: '/clubs', name: 'clubs', component: ClubsExplorePage },
  { path: '/clubs/:id', name: 'club-detail', component: ClubDetailPage, props: true },
  { path: '/events/new', name: 'create-event', component: CreateEventPage },
  { path: '/profile', name: 'profile', component: ProfilePage },
  { path: '/settings', name: 'settings', component: SettingsPage },
  { path: '/feed', name: 'feed', component: FeedPage },
  { path: '/post/:id', name: 'post-detail', component: PostDetailPage, props: true },
  { path: '/post/create', name: 'post-create', component: PostCreatePage },
  { path: '/search', name: 'search', component: SearchPage },
  { path: '/users/:id', name: 'user-detail', component: UserDetailPage, props: true },

  // Admin routes
  { path: '/admin', name: 'admin-dashboard', component: AdminDashboard },
  { path: '/admin/users', name: 'admin-users', component: AdminUsers },
  { path: '/admin/events', name: 'admin-events', component: AdminEvents },
  { path: '/admin/clubs', name: 'admin-clubs', component: AdminClubs }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;
