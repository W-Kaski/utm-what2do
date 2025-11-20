<template>
  <AdminLayout>
    <div class="admin-clubs">
      <header class="page-header">
        <div>
          <h1>Club Management</h1>
          <p>Manage registered clubs</p>
        </div>
        <div class="header-actions">
          <input
            v-model="searchQuery"
            type="search"
            placeholder="Search clubs..."
            class="search-input"
          />
        </div>
      </header>

      <div class="table-card">
        <table>
          <thead>
            <tr>
              <th>Club</th>
              <th>Category</th>
              <th>Members</th>
              <th>Events</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="club in filteredClubs" :key="club.id">
              <td>
                <div class="club-cell">
                  <div class="club-logo">{{ club.name[0] }}</div>
                  <div>
                    <strong>{{ club.name }}</strong>
                    <span>{{ club.tagline }}</span>
                  </div>
                </div>
              </td>
              <td>{{ club.category }}</td>
              <td>{{ club.members }}</td>
              <td>{{ club.events }}</td>
              <td>
                <span :class="['status-badge', club.status]">{{ club.status }}</span>
              </td>
              <td>
                <div class="action-buttons">
                  <button @click="viewClub(club)" title="View">👁️</button>
                  <button @click="toggleStatus(club)" title="Toggle Status">
                    {{ club.status === 'active' ? '🔒' : '🔓' }}
                  </button>
                  <button @click="deleteClub(club)" class="delete" title="Delete">🗑️</button>
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
const clubs = ref([
  { id: 1, name: 'CS Club', tagline: 'Code together', category: 'Academic', members: 120, events: 15, status: 'active' },
  { id: 2, name: 'Music Society', tagline: 'Harmonize', category: 'Arts', members: 85, events: 8, status: 'active' },
  { id: 3, name: 'Debate Society', tagline: 'Speak up', category: 'Academic', members: 45, events: 6, status: 'active' },
  { id: 4, name: 'Photography Club', tagline: 'Capture moments', category: 'Arts', members: 60, events: 4, status: 'suspended' },
  { id: 5, name: 'Sports Union', tagline: 'Stay active', category: 'Sports', members: 200, events: 20, status: 'active' }
]);

const filteredClubs = computed(() => {
  const query = searchQuery.value.toLowerCase();
  if (!query) return clubs.value;
  return clubs.value.filter(club =>
    club.name.toLowerCase().includes(query) ||
    club.category.toLowerCase().includes(query)
  );
});

const viewClub = (club) => {
  console.log('View club:', club);
};

const toggleStatus = async (club) => {
  const newStatus = club.status === 'active' ? 'suspended' : 'active';
  try {
    await apiClient.put(`/admin/clubs/${club.id}/status`, { status: newStatus });
    club.status = newStatus;
  } catch (err) {
    club.status = newStatus;
  }
};

const deleteClub = async (club) => {
  if (!confirm('Are you sure you want to delete this club?')) return;
  try {
    await apiClient.delete(`/admin/clubs/${club.id}`);
    clubs.value = clubs.value.filter(c => c.id !== club.id);
  } catch (err) {
    clubs.value = clubs.value.filter(c => c.id !== club.id);
  }
};

onMounted(async () => {
  try {
    const response = await apiClient.get('/admin/clubs');
    if (response.data?.data) {
      clubs.value = response.data.data;
    }
  } catch (err) {
    // Use mock data
  }
});
</script>

<style scoped>
.admin-clubs {
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

.club-cell {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.club-logo {
  width: 40px;
  height: 40px;
  border-radius: 0.5rem;
  background: #3b82f6;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
}

.club-cell strong {
  display: block;
  color: #0f172a;
}

.club-cell span {
  font-size: 0.875rem;
  color: #64748b;
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

.action-buttons button.delete:hover {
  background: #fee2e2;
}
</style>
