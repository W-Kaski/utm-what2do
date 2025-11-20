import { createApp } from 'vue';
import { createPinia } from 'pinia';

import App from './App.vue';
import router from './router';
import { useUserStore } from './stores/user';

import '@/styles/main.css';

const app = createApp(App);
const pinia = createPinia();

app.use(pinia);
app.use(router);

// Initialize user state from localStorage
const userStore = useUserStore();
userStore.initFromStorage();

app.mount('#app');
