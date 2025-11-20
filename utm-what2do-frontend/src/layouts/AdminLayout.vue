<template>
  <div class="admin-layout">
    <aside class="admin-sidebar">
      <div class="sidebar-header">
        <h2>Admin Panel</h2>
      </div>
      <nav class="sidebar-nav">
        <RouterLink to="/admin" exact-active-class="active">
          <span class="icon">📊</span>
          Dashboard
        </RouterLink>
        <RouterLink to="/admin/users" active-class="active">
          <span class="icon">👥</span>
          Users
        </RouterLink>
        <RouterLink to="/admin/events" active-class="active">
          <span class="icon">📅</span>
          Events
        </RouterLink>
        <RouterLink to="/admin/clubs" active-class="active">
          <span class="icon">🏛️</span>
          Clubs
        </RouterLink>
      </nav>
      <div class="sidebar-footer">
        <RouterLink to="/" class="back-link">
          ← Back to Site
        </RouterLink>
      </div>
    </aside>
    <main class="admin-main">
      <slot />
    </main>
  </div>
</template>

<script setup>
import { onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/user';

const router = useRouter();
const userStore = useUserStore();

onMounted(() => {
  // Check if user is admin (you can adjust this logic)
  if (!userStore.isAuthenticated) {
    router.push('/login');
  }
});
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
  background: #f1f5f9;
}

.admin-sidebar {
  width: 250px;
  background: #1e293b;
  color: #fff;
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  padding: 1.5rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.sidebar-header h2 {
  margin: 0;
  font-size: 1.25rem;
}

.sidebar-nav {
  flex: 1;
  padding: 1rem 0;
}

.sidebar-nav a {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.75rem 1.5rem;
  color: #94a3b8;
  text-decoration: none;
  transition: all 0.2s ease;
}

.sidebar-nav a:hover {
  background: rgba(255, 255, 255, 0.05);
  color: #fff;
}

.sidebar-nav a.active {
  background: rgba(37, 99, 235, 0.2);
  color: #60a5fa;
  border-left: 3px solid #3b82f6;
}

.icon {
  font-size: 1.1rem;
}

.sidebar-footer {
  padding: 1rem 1.5rem;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.back-link {
  color: #94a3b8;
  text-decoration: none;
  font-size: 0.875rem;
}

.back-link:hover {
  color: #fff;
}

.admin-main {
  flex: 1;
  padding: 2rem;
  overflow-y: auto;
}
</style>
