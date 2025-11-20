<template>
  <div class="create-event-page">
    <header class="page-header">
      <h1>Create New Event</h1>
      <p>Fill in the details to create your event</p>
    </header>

    <form class="event-form" @submit.prevent="handleSubmit">
      <div v-if="error" class="error-message">{{ error }}</div>
      <div v-if="success" class="success-message">Event created successfully!</div>

      <div class="form-section">
        <h2>Basic Information</h2>

        <div class="form-group">
          <label for="title">Event Title *</label>
          <input
            id="title"
            v-model="form.title"
            type="text"
            placeholder="Enter event title"
            required
          />
        </div>

        <div class="form-group">
          <label for="description">Short Description *</label>
          <input
            id="description"
            v-model="form.description"
            type="text"
            placeholder="Brief description (max 200 characters)"
            maxlength="200"
            required
          />
        </div>

        <div class="form-group">
          <label for="longDescription">Full Description</label>
          <textarea
            id="longDescription"
            v-model="form.longDescription"
            rows="5"
            placeholder="Detailed event description"
          ></textarea>
        </div>
      </div>

      <div class="form-section">
        <h2>Date & Time</h2>

        <div class="form-row">
          <div class="form-group">
            <label for="date">Date *</label>
            <input
              id="date"
              v-model="form.date"
              type="date"
              required
            />
          </div>

          <div class="form-group">
            <label for="startTime">Start Time *</label>
            <input
              id="startTime"
              v-model="form.startTime"
              type="time"
              required
            />
          </div>

          <div class="form-group">
            <label for="endTime">End Time *</label>
            <input
              id="endTime"
              v-model="form.endTime"
              type="time"
              required
            />
          </div>
        </div>
      </div>

      <div class="form-section">
        <h2>Location</h2>

        <div class="form-row">
          <div class="form-group">
            <label for="building">Building *</label>
            <select id="building" v-model="form.buildingId" required>
              <option value="">Select building</option>
              <option value="dv">Davis Building (DV)</option>
              <option value="cc">Communication Culture (CC)</option>
              <option value="ib">Instructional Building (IB)</option>
              <option value="mn">Kaneff Centre (MN)</option>
              <option value="other">Other</option>
            </select>
          </div>

          <div class="form-group">
            <label for="room">Room Number</label>
            <input
              id="room"
              v-model="form.room"
              type="text"
              placeholder="e.g., 2080"
            />
          </div>
        </div>
      </div>

      <div class="form-section">
        <h2>Event Details</h2>

        <div class="form-group">
          <label for="tags">Tags</label>
          <div class="tags-container">
            <button
              v-for="tag in availableTags"
              :key="tag"
              type="button"
              :class="['tag-btn', { active: form.tags.includes(tag) }]"
              @click="toggleTag(tag)"
            >
              {{ tag }}
            </button>
          </div>
        </div>

        <div class="form-group">
          <label for="coverImage">Cover Image URL</label>
          <input
            id="coverImage"
            v-model="form.coverImage"
            type="url"
            placeholder="https://example.com/image.jpg"
          />
        </div>

        <div class="form-group">
          <label>
            <input type="checkbox" v-model="form.registrationRequired" />
            Registration Required
          </label>
        </div>

        <div v-if="form.registrationRequired" class="form-group">
          <label for="registrationNotes">Registration Notes</label>
          <textarea
            id="registrationNotes"
            v-model="form.registrationNotes"
            rows="3"
            placeholder="How to register for this event"
          ></textarea>
        </div>
      </div>

      <div class="form-actions">
        <button type="button" class="cancel-btn" @click="cancel">Cancel</button>
        <button type="submit" class="submit-btn" :disabled="loading">
          {{ loading ? 'Creating...' : 'Create Event' }}
        </button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { useEventStore } from '@/stores/events';

const router = useRouter();
const eventStore = useEventStore();

const loading = ref(false);
const error = ref('');
const success = ref(false);

const availableTags = ['Tech', 'Social', 'Competition', 'Music', 'Academic', 'Sports', 'Cultural', 'Workshop'];

const form = reactive({
  title: '',
  description: '',
  longDescription: '',
  date: '',
  startTime: '',
  endTime: '',
  buildingId: '',
  room: '',
  tags: [],
  coverImage: '',
  registrationRequired: false,
  registrationNotes: ''
});

const toggleTag = (tag) => {
  const index = form.tags.indexOf(tag);
  if (index === -1) {
    form.tags.push(tag);
  } else {
    form.tags.splice(index, 1);
  }
};

const handleSubmit = async () => {
  error.value = '';
  success.value = false;
  loading.value = true;

  try {
    const eventData = {
      title: form.title,
      description: form.description,
      longDescription: form.longDescription,
      date: form.date,
      startTime: form.startTime,
      endTime: form.endTime,
      buildingId: form.buildingId,
      room: form.room,
      tags: form.tags,
      coverImage: form.coverImage || 'https://images.unsplash.com/photo-1540575467063-178a50c2df87?auto=format&fit=crop&w=1200&q=80',
      registrationNotes: form.registrationRequired ? form.registrationNotes : ''
    };

    await eventStore.createEvent(eventData);
    success.value = true;

    setTimeout(() => {
      router.push({ name: 'events' });
    }, 1500);
  } catch (err) {
    error.value = err.message || 'Failed to create event';
  } finally {
    loading.value = false;
  }
};

const cancel = () => {
  router.back();
};
</script>

<style scoped>
.create-event-page {
  max-width: 800px;
  margin: 0 auto;
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

.event-form {
  background: #fff;
  border-radius: 1.5rem;
  padding: 2rem;
  box-shadow: 0 20px 45px rgba(15, 23, 42, 0.08);
}

.error-message {
  background: #fef2f2;
  border: 1px solid #fecaca;
  color: #dc2626;
  padding: 0.75rem 1rem;
  border-radius: 0.75rem;
  margin-bottom: 1.5rem;
}

.success-message {
  background: #f0fdf4;
  border: 1px solid #bbf7d0;
  color: #166534;
  padding: 0.75rem 1rem;
  border-radius: 0.75rem;
  margin-bottom: 1.5rem;
}

.form-section {
  margin-bottom: 2rem;
  padding-bottom: 2rem;
  border-bottom: 1px solid #e2e8f0;
}

.form-section:last-of-type {
  border-bottom: none;
  margin-bottom: 1.5rem;
  padding-bottom: 0;
}

.form-section h2 {
  margin: 0 0 1.25rem;
  font-size: 1.1rem;
  color: #0f172a;
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 600;
  color: #374151;
  font-size: 0.875rem;
}

.form-group input[type="text"],
.form-group input[type="url"],
.form-group input[type="date"],
.form-group input[type="time"],
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 0.75rem 1rem;
  border: 1px solid #e2e8f0;
  border-radius: 0.75rem;
  font-size: 1rem;
  transition: border-color 0.15s ease;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #2563eb;
}

.form-group input[type="checkbox"] {
  margin-right: 0.5rem;
}

.form-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 1rem;
}

.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.tag-btn {
  padding: 0.5rem 1rem;
  border: 1px solid #e2e8f0;
  border-radius: 999px;
  background: #fff;
  font-size: 0.875rem;
  cursor: pointer;
  transition: all 0.15s ease;
}

.tag-btn:hover {
  border-color: #2563eb;
}

.tag-btn.active {
  background: #2563eb;
  color: #fff;
  border-color: #2563eb;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 1rem;
}

.cancel-btn {
  padding: 0.75rem 1.5rem;
  border: 1px solid #e2e8f0;
  border-radius: 0.75rem;
  background: #fff;
  font-weight: 600;
  cursor: pointer;
}

.cancel-btn:hover {
  background: #f1f5f9;
}

.submit-btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 0.75rem;
  background: #2563eb;
  color: #fff;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.15s ease;
}

.submit-btn:hover:not(:disabled) {
  background: #1d4ed8;
}

.submit-btn:disabled {
  background: #94a3b8;
  cursor: not-allowed;
}
</style>
