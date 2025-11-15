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

const routes = [
  { path: '/', name: 'home', component: HomePage },
  { path: '/map', name: 'map', component: MapPage },
  { path: '/events', name: 'events', component: EventsPage },
  { path: '/explore', name: 'explore', component: EventsPage },
  { path: '/events/:id', name: 'event-detail', component: EventDetailPage, props: true },
  { path: '/clubs', name: 'clubs', component: ClubsExplorePage },
  { path: '/clubs/:id', name: 'club-detail', component: ClubDetailPage, props: true },
  { path: '/events/new', name: 'create-event', component: CreateEventPage },
  { path: '/profile', name: 'profile', component: ProfilePage },
  { path: '/feed', name: 'feed', component: FeedPage },
  { path: '/post/:id', name: 'post-detail', component: PostDetailPage, props: true },
  { path: '/post/create', name: 'post-create', component: PostCreatePage },
  { path: '/search', name: 'search', component: SearchPage },
  { path: '/users/:id', name: 'user-detail', component: UserDetailPage, props: true }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;
