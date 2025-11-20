<template>
  <AdminLayout>
    <div class="admin-users">
      <header class="page-header">
        <div>
          <h1>User Management</h1>
          <p>Manage all registered users</p>
        </div>
        <div class="header-actions">
          <input
            v-model="searchQuery"
            type="search"
            placeholder="Search users..."
            class="search-input"
          />
        </div>
      </header>

      <div class="table-card">
        <table>
          <thead>
            <tr>
              <th>User</th>
              <th>Email</th>
              <th>Role</th>
              <th>Status</th>
              <th>Joined</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="user in filteredUsers" :key="user.id">
              <td>
                <div class="user-cell">
                  <div class="avatar">{{ getInitials(user.name) }}</div>
                  <span>{{ user.name }}</span>
                </div>
              </td>
              <td>{{ user.email }}</td>
              <td>
                <span :class="['role-badge', user.role]">{{ user.role }}</span>
              </td>
              <td>
                <span :class="['status-badge', user.status]">{{ user.status }}</span>
              </td>
              <td>{{ user.joinedAt }}</td>
              <td>
                <div class="action-buttons">
                  <button @click="viewUser(user)" title="View">👁️</button>
                  <button @click="toggleStatus(user)" title="Toggle Status">
                    {{ user.status === 'active' ? '🔒' : '🔓' }}
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </AdminLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import AdminLayout from '@/layouts/AdminLayout.vue';
import apiClient from '@/api';

const searchQuery = ref('');
const users = ref([
  { id: 1, name: 'John Doe', email: 'john@utm.edu', role: 'user', status: 'active', joinedAt: '2024-11-15' },
  { id: 2, name: 'Jane Smith', email: 'jane@utm.edu', role: 'club_manager', status: 'active', joinedAt: '2024-11-14' },
  { id: 3, name: 'Bob Wilson', email: 'bob@utm.edu', role: 'user', status: 'suspended', joinedAt: '2024-11-13' },
  { id: 4, name: 'Alice Brown', email: 'alice@utm.edu', role: 'admin', status: 'active', joinedAt: '2024-11-10' },
  { id: 5, name: 'Charlie Davis', email: 'charlie@utm.edu', role: 'user', status: 'active', joinedAt: '2024-11-08' }
]);

const filteredUsers = computed(() => {
  const query = searchQuery.value.toLowerCase();
  if (!query) return users.value;
  return users.value.filter(user =>
    user.name.toLowerCase().includes(query) ||
    user.email.toLowerCase().includes(query)
  );
});

const getInitials = (name) => {
  return name.split(' ').map(n => n[0]).join('').toUpperCase();
};

const viewUser = (user) => {
  console.log('View user:', user);
};

const toggleStatus = async (user) => {
  const newStatus = user.status === 'active' ? 'suspended' : 'active';
  try {
    await apiClient.put(`/admin/users/${user.id}/status`, { status: newStatus });
    user.status = newStatus;
  } catch (err) {
    user.status = newStatus; // Optimistic update for demo
  }
};

onMounted(async () => {
  try {
    const response = await apiClient.get('/admin/users');
    if (response.data?.data) {
      users.value = response.data.data;
    }
  } catch (err) {
    // Use mock data
  }
});
</script>

<style scoped>
.admin-users {
  max-width: 1200px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
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

.search-input {
  padding: 0.5rem 1rem;
  border: 1px solid #e2e8f0;
  border-radius: 0.5rem;
  width: 250px;
}

.table-card {
  background: #fff;
  border-radius: 1rem;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th, td {
  padding: 1rem;
  text-align: left;
  border-bottom: 1px solid #e2e8f0;
}

th {
  font-weight: 600;
  color: #64748b;
  font-size: 0.75rem;
  text-transform: uppercase;
  background: #f8fafc;
}

.user-cell {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #3b82f6;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.75rem;
  font-weight: 600;
}

.role-badge {
  padding: 0.25rem 0.5rem;
  border-radius: 999px;
  font-size: 0.75rem;
  font-weight: 600;
}

.role-badge.admin {
  background: #f3e8ff;
  color: #7c3aed;
}

.role-badge.club_manager {
  background: #dbeafe;
  color: #1d4ed8;
}

.role-badge.user {
  background: #f1f5f9;
  color: #475569;
}

.status-badge {
  padding: 0.25rem 0.5rem;
  border-radius: 999px;
  font-size: 0.75rem;
  font-weight: 600;
}

.status-badge.active {
  background: #dcfce7;
  color: #166534;
}

.status-badge.suspended {
  background: #fee2e2;
  color: #991b1b;
}

.action-buttons {
  display: flex;
  gap: 0.5rem;
}

.action-buttons button {
  border: none;
  background: #f1f5f9;
  padding: 0.35rem 0.5rem;
  border-radius: 0.375rem;
  cursor: pointer;
}

.action-buttons button:hover {
  background: #e2e8f0;
}
</style>
