<template>
  <AdminLayout>
    <div class="admin-events">
      <header class="page-header">
        <div>
          <h1>Event Management</h1>
          <p>Review and manage events</p>
        </div>
        <div class="header-actions">
          <select v-model="statusFilter" class="filter-select">
            <option value="">All Status</option>
            <option value="pending">Pending</option>
            <option value="approved">Approved</option>
            <option value="rejected">Rejected</option>
          </select>
        </div>
      </header>

      <div class="table-card">
        <table>
          <thead>
            <tr>
              <th>Event</th>
              <th>Organizer</th>
              <th>Date</th>
              <th>Location</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="event in filteredEvents" :key="event.id">
              <td>
                <div class="event-cell">
                  <strong>{{ event.title }}</strong>
                  <span>{{ event.description }}</span>
                </div>
              </td>
              <td>{{ event.organizer }}</td>
              <td>{{ event.date }}</td>
              <td>{{ event.location }}</td>
              <td>
                <span :class="['status-badge', event.status]">{{ event.status }}</span>
              </td>
              <td>
                <div class="action-buttons">
                  <button v-if="event.status === 'pending'" @click="approveEvent(event)" class="approve" title="Approve">✓</button>
                  <button v-if="event.status === 'pending'" @click="rejectEvent(event)" class="reject" title="Reject">✗</button>
                  <button @click="viewEvent(event)" title="View">👁️</button>
                  <button @click="deleteEvent(event)" class="delete" title="Delete">🗑️</button>
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

const statusFilter = ref('');
const events = ref([
  { id: 1, title: 'Tech Workshop', description: 'Learn web development', organizer: 'CS Club', date: '2024-11-20', location: 'DV 2080', status: 'approved' },
  { id: 2, title: 'Music Night', description: 'Live performances', organizer: 'Music Society', date: '2024-11-22', location: 'South Field', status: 'pending' },
  { id: 3, title: 'Career Fair', description: 'Meet employers', organizer: 'Career Center', date: '2024-11-25', location: 'Main Hall', status: 'approved' },
  { id: 4, title: 'Art Exhibition', description: 'Student artworks', organizer: 'Art Club', date: '2024-11-28', location: 'Gallery', status: 'pending' },
  { id: 5, title: 'Debate Competition', description: 'Inter-club debate', organizer: 'Debate Society', date: '2024-12-01', location: 'Auditorium', status: 'rejected' }
]);

const filteredEvents = computed(() => {
  if (!statusFilter.value) return events.value;
  return events.value.filter(event => event.status === statusFilter.value);
});

const approveEvent = async (event) => {
  try {
    await apiClient.put(`/admin/events/${event.id}/status`, { status: 'approved' });
    event.status = 'approved';
  } catch (err) {
    event.status = 'approved';
  }
};

const rejectEvent = async (event) => {
  try {
    await apiClient.put(`/admin/events/${event.id}/status`, { status: 'rejected' });
    event.status = 'rejected';
  } catch (err) {
    event.status = 'rejected';
  }
};

const viewEvent = (event) => {
  console.log('View event:', event);
};

const deleteEvent = async (event) => {
  if (!confirm('Are you sure you want to delete this event?')) return;
  try {
    await apiClient.delete(`/admin/events/${event.id}`);
    events.value = events.value.filter(e => e.id !== event.id);
  } catch (err) {
    events.value = events.value.filter(e => e.id !== event.id);
  }
};

onMounted(async () => {
  try {
    const response = await apiClient.get('/admin/events');
    if (response.data?.data) {
      events.value = response.data.data;
    }
  } catch (err) {
    // Use mock data
  }
});
</script>

<style scoped>
.admin-events {
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

.filter-select {
  padding: 0.5rem 1rem;
  border: 1px solid #e2e8f0;
  border-radius: 0.5rem;
  background: #fff;
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

.event-cell {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.event-cell strong {
  color: #0f172a;
}

.event-cell span {
  font-size: 0.875rem;
  color: #64748b;
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

.action-buttons button.approve:hover {
  background: #dcfce7;
}

.action-buttons button.reject:hover {
  background: #fee2e2;
}

.action-buttons button.delete:hover {
  background: #fee2e2;
}
</style>
