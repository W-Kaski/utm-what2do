<template>
  <div class="layout">
    <header class="app-header">
      <div class="nav-shell">
        <RouterLink to="/" class="brand" aria-label="UTM What2Do">
          <span class="brand__logo">UTM</span>
          <span class="brand__accent">What2Do</span>
        </RouterLink>

        <nav class="primary-nav" aria-label="Primary Navigation">
          <RouterLink to="/">Home</RouterLink>
          <RouterLink to="/events">Events</RouterLink>
          <RouterLink to="/map">Map</RouterLink>
          <RouterLink :to="{ name: 'feed' }" :class="{ 'router-link-active': communityActive }">
            Community
          </RouterLink>
          <RouterLink to="/profile">Profile</RouterLink>
        </nav>

        <div class="header-actions">
          <RouterLink to="/events/new" class="pill-link">Create Event</RouterLink>

          <!-- Not logged in: Show Sign In button -->
          <RouterLink v-if="!userStore.isAuthenticated" to="/login" class="sign-in-btn">
            Sign In
          </RouterLink>

          <!-- Logged in: Show user menu -->
          <div v-else class="profile" ref="menuRef">
            <button
              class="avatar-button"
              type="button"
              aria-haspopup="menu"
              :aria-expanded="menuOpen"
              @click="toggleMenu"
            >
              <img v-if="userStore.avatar" :src="userStore.avatar" :alt="userStore.name" class="avatar-img" />
              <span v-else class="avatar-initials">{{ userInitials }}</span>
              <span class="status-dot" aria-hidden="true"></span>
            </button>

            <div
              v-if="menuOpen"
              class="dropdown"
              role="menu"
              aria-label="User menu"
            >
              <div class="dropdown__header">
                <p>Welcome back</p>
                <strong>{{ userStore.name }}</strong>
              </div>
              <div class="dropdown__body">
                <button type="button" role="menuitem" @click="goHome">
                  Home
                </button>
                <button type="button" role="menuitem" class="logout-btn" @click="handleLogout">Log out</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </header>

    <main>
      <slot />
    </main>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue';
import { RouterLink, useRoute, useRouter } from 'vue-router';
import { useUserStore } from '@/stores/user';

const menuOpen = ref(false);
const menuRef = ref(null);
const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const communityActive = computed(() =>
  ['feed', 'search', 'post-create', 'post-detail'].includes(route.name)
);

const userInitials = computed(() => {
  const name = userStore.name || 'User';
  const parts = name.split(' ');
  if (parts.length >= 2) {
    return (parts[0][0] + parts[1][0]).toUpperCase();
  }
  return name.substring(0, 2).toUpperCase();
});

const closeMenu = () => {
  menuOpen.value = false;
};

const toggleMenu = () => {
  menuOpen.value = !menuOpen.value;
};

const handleOutsideClick = (event) => {
  if (!menuRef.value) return;
  if (!menuRef.value.contains(event.target)) {
    closeMenu();
  }
};

const goHome = () => {
  closeMenu();
  router.push({ name: 'home' });
};

const handleLogout = async () => {
  closeMenu();
  await userStore.logout();
  router.push({ name: 'home' });
};

onMounted(async () => {
  window.addEventListener('click', handleOutsideClick);

  // If the store only has a token (e.g. on refresh) fetch the latest profile
  if (userStore.isAuthenticated && !userStore.id) {
    try {
      await userStore.fetchCurrentUser();
    } catch (err) {
      console.error('Failed to refresh current user', err);
    }
  }
});

onBeforeUnmount(() => {
  window.removeEventListener('click', handleOutsideClick);
});
</script>

<style scoped>
.layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: radial-gradient(circle at top, #f6f7fb, #edf0f7);
}

.app-header {
  position: sticky;
  top: 0;
  z-index: 100;
  backdrop-filter: blur(12px);
  background: rgba(255, 255, 255, 0.9);
  border-bottom: 1px solid rgba(15, 23, 42, 0.08);
}

.nav-shell {
  max-width: 1200px;
  margin: 0 auto;
  padding: 1rem 2rem;
  display: flex;
  align-items: center;
  gap: 2rem;
}

.brand {
  display: inline-flex;
  align-items: baseline;
  gap: 0.35rem;
  font-weight: 700;
  text-decoration: none;
}

.brand__logo {
  font-size: 1.25rem;
  color: #0f172a;
  letter-spacing: 0.05em;
}

.brand__accent {
  color: #2563eb;
  font-size: 0.95rem;
}

.primary-nav {
  display: flex;
  align-items: center;
  gap: 1.25rem;
  font-weight: 500;
  color: #475569;
}

.primary-nav a {
  text-decoration: none;
  color: inherit;
  padding-bottom: 0.2rem;
  border-bottom: 2px solid transparent;
  transition: color 0.2s ease, border-color 0.2s ease;
}

.primary-nav a.router-link-active {
  color: #111827;
  border-color: #2563eb;
}

.header-actions {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 1rem;
}

.pill-link {
  padding: 0.5rem 1rem;
  border-radius: 999px;
  border: 1px solid rgba(37, 99, 235, 0.3);
  color: #1d4ed8;
  font-weight: 600;
  text-decoration: none;
  transition: background 0.2s ease, color 0.2s ease;
}

.pill-link:hover {
  background: rgba(37, 99, 235, 0.08);
}

.sign-in-btn {
  padding: 0.5rem 1.25rem;
  border-radius: 999px;
  background: #2563eb;
  color: #fff;
  font-weight: 600;
  text-decoration: none;
  transition: background 0.2s ease, transform 0.15s ease;
}

.sign-in-btn:hover {
  background: #1d4ed8;
  transform: translateY(-1px);
}

.profile {
  position: relative;
}

.avatar-button {
  width: 42px;
  height: 42px;
  border-radius: 999px;
  border: none;
  background: linear-gradient(135deg, #2563eb, #4f46e5);
  color: #fff;
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  position: relative;
  box-shadow: 0 8px 24px rgba(37, 99, 235, 0.35);
  cursor: pointer;
}

.avatar-img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
}

.avatar-initials {
  font-size: 0.85rem;
  letter-spacing: 0.04em;
}

.status-dot {
  position: absolute;
  bottom: 4px;
  right: 4px;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #22c55e;
  border: 2px solid #fff;
}

.dropdown {
  position: absolute;
  top: 52px;
  right: 0;
  width: 220px;
  background: #fff;
  border-radius: 1rem;
  box-shadow: 0 20px 40px rgba(15, 23, 42, 0.15);
  border: 1px solid rgba(148, 163, 184, 0.3);
  padding: 1rem;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.dropdown__header p {
  margin: 0;
  font-size: 0.85rem;
  color: #94a3b8;
}

.dropdown__header strong {
  color: #0f172a;
  font-size: 1rem;
}

.dropdown__body {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.dropdown__body a,
.dropdown__body button {
  text-align: left;
  padding: 0.5rem 0.25rem;
  border-radius: 0.5rem;
  border: none;
  background: transparent;
  color: #1e293b;
  font-weight: 500;
  text-decoration: none;
  cursor: pointer;
  transition: background 0.2s ease, color 0.2s ease;
}

.dropdown__body a:hover,
.dropdown__body button:hover {
  background: rgba(37, 99, 235, 0.08);
  color: #2563eb;
}

.dropdown__body .logout-btn:hover {
  background: rgba(239, 68, 68, 0.08);
  color: #dc2626;
}

main {
  flex: 1;
  padding: 2.5rem 2rem 3rem;
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
}

@media (max-width: 900px) {
  .nav-shell {
    flex-wrap: wrap;
    gap: 1rem;
  }

  .primary-nav {
    order: 3;
    width: 100%;
    justify-content: space-between;
  }
}
</style>
