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
        <div v-if="loading" class="loading">Loading...</div>
        <table v-else>
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
                  <div class="avatar">{{ getInitials(user.displayName || user.username) }}</div>
                  <span>{{ user.displayName || user.username }}</span>
                </div>
              </td>
              <td>{{ user.email }}</td>
              <td>
                <select
                  :value="user.role"
                  @change="handleChangeRole(user, $event.target.value)"
                  class="role-select"
                >
                  <option v-for="role in roleOptions" :key="role" :value="role">
                    {{ role }}
                  </option>
                </select>
              </td>
              <td>
                <span :class="['status-badge', user.deleted === 0 ? 'active' : 'suspended']">
                  {{ user.deleted === 0 ? 'Active' : 'Disabled' }}
                </span>
              </td>
              <td>{{ user.createdAt?.split('T')[0] || user.createdAt }}</td>
              <td>
                <div class="action-buttons">
                  <button @click="toggleStatus(user)" :title="user.deleted === 0 ? 'Disable' : 'Enable'">
                    {{ user.deleted === 0 ? '🔒' : '🔓' }}
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>

        <!-- Pagination -->
        <div class="pagination" v-if="totalPages > 1">
          <button @click="changePage(1)" :disabled="currentPage === 1">First</button>
          <button @click="changePage(currentPage - 1)" :disabled="currentPage === 1">Prev</button>
          <span class="page-info">Page {{ currentPage }} of {{ totalPages }} ({{ total }} users)</span>
          <button @click="changePage(currentPage + 1)" :disabled="currentPage === totalPages">Next</button>
          <button @click="changePage(totalPages)" :disabled="currentPage === totalPages">Last</button>
        </div>
      </div>
    </div>
  </AdminLayout>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import AdminLayout from '@/layouts/AdminLayout.vue';
import { getUserList, updateUserRole, updateUserStatus } from '@/api/admin';

const searchQuery = ref('');
const users = ref([]);
const loading = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// Role options for dropdown
const roleOptions = ['USER', 'CLUB_MANAGER', 'ADMIN'];

const filteredUsers = computed(() => {
  return users.value;
});

const totalPages = computed(() => Math.ceil(total.value / pageSize.value));

const loadUsers = async () => {
  try {
    loading.value = true;
    const response = await getUserList({
      current: currentPage.value,
      size: pageSize.value,
      keyword: searchQuery.value || undefined
    });
    if (response.data?.data) {
      const pageData = response.data.data;
      users.value = pageData.records || [];
      total.value = pageData.total || 0;
    }
  } catch (err) {
    console.error('Failed to load users:', err);
    // Use mock data for demo
    users.value = [
      { id: 1, username: 'john_doe', displayName: 'John Doe', email: 'john@utm.edu', role: 'USER', deleted: 0, createdAt: '2024-11-15' },
      { id: 2, username: 'jane_smith', displayName: 'Jane Smith', email: 'jane@utm.edu', role: 'CLUB_MANAGER', deleted: 0, createdAt: '2024-11-14' },
      { id: 3, username: 'bob_wilson', displayName: 'Bob Wilson', email: 'bob@utm.edu', role: 'USER', deleted: 1, createdAt: '2024-11-13' },
      { id: 4, username: 'alice_brown', displayName: 'Alice Brown', email: 'alice@utm.edu', role: 'ADMIN', deleted: 0, createdAt: '2024-11-10' }
    ];
    total.value = users.value.length;
  } finally {
    loading.value = false;
  }
};

const getInitials = (name) => {
  if (!name) return '?';
  return name.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2);
};

const handleChangeRole = async (user, newRole) => {
  if (!confirm(`Change ${user.displayName || user.username}'s role to ${newRole}?`)) return;

  try {
    await updateUserRole(user.id, newRole);
    user.role = newRole;
    alert('Role updated successfully');
  } catch (err) {
    alert('Failed to update role: ' + (err.message || 'Unknown error'));
  }
};

const toggleStatus = async (user) => {
  const newStatus = user.deleted === 0 ? 1 : 0;
  const action = newStatus === 1 ? 'disable' : 'enable';
  if (!confirm(`Are you sure you want to ${action} this user?`)) return;

  try {
    await updateUserStatus(user.id, newStatus);
    user.deleted = newStatus;
    alert(`User ${action}d successfully`);
  } catch (err) {
    alert('Failed to update status: ' + (err.message || 'Unknown error'));
  }
};

const handleSearch = () => {
  currentPage.value = 1;
  loadUsers();
};

const changePage = (page) => {
  if (page < 1 || page > totalPages.value) return;
  currentPage.value = page;
  loadUsers();
};

// Debounce search
let searchTimeout;
watch(searchQuery, () => {
  clearTimeout(searchTimeout);
  searchTimeout = setTimeout(handleSearch, 500);
});

onMounted(() => {
  loadUsers();
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

.loading {
  text-align: center;
  padding: 2rem;
  color: #64748b;
}

.role-select {
  padding: 0.25rem 0.5rem;
  border: 1px solid #e2e8f0;
  border-radius: 0.375rem;
  font-size: 0.75rem;
  background: #fff;
  cursor: pointer;
}

.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 1rem;
  border-top: 1px solid #e2e8f0;
}

.pagination button {
  padding: 0.5rem 0.75rem;
  border: 1px solid #e2e8f0;
  border-radius: 0.375rem;
  background: #fff;
  cursor: pointer;
  font-size: 0.875rem;
}

.pagination button:hover:not(:disabled) {
  background: #f1f5f9;
}

.pagination button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  margin: 0 1rem;
  font-size: 0.875rem;
  color: #64748b;
}
</style>
