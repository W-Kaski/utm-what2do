<template>
  <AdminLayout>
    <div class="admin-dashboard">
      <header class="page-header">
        <h1>Dashboard</h1>
        <p>Overview of your platform statistics</p>
      </header>

      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon users">👥</div>
          <div class="stat-info">
            <p class="stat-value">{{ stats.users }}</p>
            <p class="stat-label">Total Users</p>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon events">📅</div>
          <div class="stat-info">
            <p class="stat-value">{{ stats.events }}</p>
            <p class="stat-label">Total Events</p>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon clubs">🏛️</div>
          <div class="stat-info">
            <p class="stat-value">{{ stats.clubs }}</p>
            <p class="stat-label">Total Clubs</p>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon posts">📝</div>
          <div class="stat-info">
            <p class="stat-value">{{ stats.posts }}</p>
            <p class="stat-label">Total Posts</p>
          </div>
        </div>
      </div>

      <div class="dashboard-grid">
        <section class="recent-section">
          <h2>Recent Users</h2>
          <div class="table-wrapper">
            <table>
              <thead>
                <tr>
                  <th>Name</th>
                  <th>Email</th>
                  <th>Joined</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="user in recentUsers" :key="user.id">
                  <td>{{ user.name }}</td>
                  <td>{{ user.email }}</td>
                  <td>{{ user.joinedAt }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </section>

        <section class="recent-section">
          <h2>Recent Events</h2>
          <div class="table-wrapper">
            <table>
              <thead>
                <tr>
                  <th>Title</th>
                  <th>Date</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="event in recentEvents" :key="event.id">
                  <td>{{ event.title }}</td>
                  <td>{{ event.date }}</td>
                  <td>
                    <span :class="['status-badge', event.status]">{{ event.status }}</span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </section>
      </div>
    </div>
  </AdminLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import AdminLayout from '@/layouts/AdminLayout.vue';
import apiClient from '@/api';

const stats = ref({
  users: 0,
  events: 0,
  clubs: 0,
  posts: 0
});

const recentUsers = ref([
  { id: 1, name: 'John Doe', email: 'john@utm.edu', joinedAt: '2024-11-15' },
  { id: 2, name: 'Jane Smith', email: 'jane@utm.edu', joinedAt: '2024-11-14' },
  { id: 3, name: 'Bob Wilson', email: 'bob@utm.edu', joinedAt: '2024-11-13' }
]);

const recentEvents = ref([
  { id: 1, title: 'Tech Workshop', date: '2024-11-20', status: 'approved' },
  { id: 2, title: 'Music Night', date: '2024-11-22', status: 'pending' },
  { id: 3, title: 'Career Fair', date: '2024-11-25', status: 'approved' }
]);

onMounted(async () => {
  try {
    const response = await apiClient.get('/admin/stats');
    if (response.data?.data) {
      stats.value = response.data.data;
    }
  } catch (err) {
    // Use mock data if API fails
    stats.value = { users: 156, events: 42, clubs: 28, posts: 384 };
  }
});
</script>

<style scoped>
.admin-dashboard {
  max-width: 1200px;
}

.page-header {
  margin-bottom: 2rem;
}

.page-header h1 {
  margin: 0;
  font-size: 1.75rem;
  color: #0f172a;
}

.page-header p {
  margin: 0.25rem 0 0;
  color: #64748b;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.stat-card {
  background: #fff;
  border-radius: 1rem;
  padding: 1.5rem;
  display: flex;
  align-items: center;
  gap: 1rem;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 0.75rem;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
}

.stat-icon.users { background: #dbeafe; }
.stat-icon.events { background: #dcfce7; }
.stat-icon.clubs { background: #fef3c7; }
.stat-icon.posts { background: #f3e8ff; }

.stat-value {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 700;
  color: #0f172a;
}

.stat-label {
  margin: 0;
  font-size: 0.875rem;
  color: #64748b;
}

.dashboard-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 1.5rem;
}

.recent-section {
  background: #fff;
  border-radius: 1rem;
  padding: 1.5rem;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
}

.recent-section h2 {
  margin: 0 0 1rem;
  font-size: 1.1rem;
  color: #0f172a;
}

.table-wrapper {
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th, td {
  padding: 0.75rem;
  text-align: left;
  border-bottom: 1px solid #e2e8f0;
}

th {
  font-weight: 600;
  color: #64748b;
  font-size: 0.75rem;
  text-transform: uppercase;
}

td {
  color: #334155;
  font-size: 0.875rem;
}

.status-badge {
  padding: 0.25rem 0.5rem;
  border-radius: 999px;
  font-size: 0.75rem;
  font-weight: 600;
}

.status-badge.approved {
  background: #dcfce7;
  color: #166534;
}

.status-badge.pending {
  background: #fef3c7;
  color: #92400e;
}

.status-badge.rejected {
  background: #fee2e2;
  color: #991b1b;
}
</style>
