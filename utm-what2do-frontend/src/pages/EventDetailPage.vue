<template>
  <section v-if="event" class="event-detail">
    <div class="detail-shell">
      <div class="top-bar">
        <button type="button" class="back-chip" aria-label="Go back" @click="goBack">
          <span aria-hidden="true" class="arrow">←</span>
          Back
        </button>
      </div>

      <div class="media-card">
        <img :src="event.coverImage || fallbackCover" :alt="event.title" />
      </div>

      <article class="panel info-card">
        <header class="info-header">
          <p class="eyebrow">Featured Event</p>
          <div class="title-row">
            <h1>{{ event.title }}</h1>
            <button
              type="button"
              class="star-btn"
              :class="{ active: isFavorited }"
              aria-label="Save event"
              @click="toggleFavorite"
            >
              ★
            </button>
          </div>
        </header>
        <ul class="meta-list">
          <li>
            <span class="meta-icon">🕒</span>
            <div>
              <strong>{{ schedule.date }}</strong>
              <span>{{ schedule.time }}</span>
            </div>
          </li>
          <li>
            <span class="meta-icon">📍</span>
            <div>
              <strong>
                <button
                  v-if="event.locationId"
                  type="button"
                  class="link-chip"
                  @click="goToMapLocation"
                >
                  {{ event.location }}
                </button>
                <span v-else>{{ event.location }}</span>
              </strong>
            </div>
          </li>
        </ul>

        <div class="tag-group">
          <span v-for="tag in event.tags" :key="tag" class="chip">{{ tag }}</span>
        </div>
      </article>

      <section v-if="registrationItems.length" class="panel">
        <header class="section-header">
          <span class="section-icon">📝</span>
          <div>
            <p class="section-eyebrow">Registration</p>
            <h2>How to register</h2>
          </div>
        </header>
        <ul class="bullet-list">
          <li v-for="(item, index) in registrationItems" :key="index">{{ item }}</li>
        </ul>
      </section>

      <section class="panel">
        <header class="section-header">
          <span class="section-icon">💬</span>
          <div>
            <p class="section-eyebrow">Description</p>
            <h2>Event overview</h2>
          </div>
        </header>
        <p class="description">{{ longDescription }}</p>
      </section>

      <section v-if="organizer" class="panel organizer">
        <header class="section-header">
          <span class="section-icon">🏛️</span>
          <div>
            <p class="section-eyebrow">Organizer</p>
            <h2>Organizer</h2>
          </div>
        </header>
        <div class="organizer-card">
          <img :src="organizer.avatar" :alt="organizer.name" />
          <div>
            <strong>{{ organizer.name }}</strong>
            <p>{{ organizer.description }}</p>
            <div class="organizer-links">
              <span>{{ organizer.contact }}</span>
              <RouterLink
                v-if="organizer.clubId"
                :to="{ name: 'club-detail', params: { id: organizer.clubId } }"
              >
                View club
              </RouterLink>
            </div>
          </div>
        </div>
      </section>
    </div>
  </section>
  <section v-else class="empty-state">
    <h2>Event not found</h2>
    <p>The event may have been removed. Please head back and pick another one.</p>
    <RouterLink class="back-link" :to="{ name: 'events' }">Back to events</RouterLink>
  </section>
</template>

<script setup>
import { computed } from 'vue';
import { RouterLink, useRoute, useRouter } from 'vue-router';

import { useEventStore } from '@/stores/events';
import { formatDate } from '@/utils/formatters';
import { useUserStore } from '@/stores/user';

const route = useRoute();
const router = useRouter();
const eventStore = useEventStore();

const userStore = useUserStore();

const event = computed(() => eventStore.events.find((item) => item.id === route.params.id));

const buildDateTime = (date, time) => {
  if (!date || !time) return null;
  const iso = `${date}T${time}`;
  const parsed = new Date(iso);
  return Number.isNaN(parsed.getTime()) ? null : parsed;
};

const schedule = computed(() => {
  if (!event.value) {
    return { date: 'TBD', time: 'TBD' };
  }
  const dateText = formatDate(event.value.date);
  const start = buildDateTime(event.value.date, event.value.startTime);
  const end = buildDateTime(event.value.date, event.value.endTime);
  const formatter = new Intl.DateTimeFormat('en-CA', { hour: 'numeric', minute: '2-digit' });
  const timeText = start && end ? `${formatter.format(start)} – ${formatter.format(end)}` : 'Time TBD';
  return { date: dateText, time: timeText };
});

const registrationItems = computed(() => event.value?.registration || []);
const longDescription = computed(() => event.value?.longDescription || event.value?.description || 'Details coming soon');
const organizer = computed(() => event.value?.organizer);

const fallbackCover =
  'https://images.unsplash.com/photo-1503676260728-1c00da094a0b?auto=format&fit=crop&w=1200&q=80';

const isFavorited = computed(() => {
  if (!event.value) return false;
  return userStore.isFavorited(event.value.id);
});

const toggleFavorite = () => {
  if (!event.value) return;
  userStore.toggleFavorite(event.value.id);
};

const goToMapLocation = () => {
  if (!event.value?.locationId) return;
  router.push({ name: 'map', query: { focus: event.value.locationId } });
};

const goBack = () => {
  if (window.history.length > 1) {
    router.back();
  } else {
    router.push({ name: 'events' });
  }
};
</script>

<style scoped>
.event-detail {
  display: flex;
  justify-content: center;
}

.detail-shell {
  width: 100%;
  max-width: 1200px;
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.top-bar {
  display: flex;
}

.back-chip {
  border: none;
  background: #1d4ed8;
  color: #fff;
  font-size: 0.95rem;
  padding: 0.4rem 0.85rem;
  border-radius: 999px;
  display: inline-flex;
  align-items: center;
  gap: 0.35rem;
  font-weight: 600;
  box-shadow: 0 15px 30px rgba(37, 99, 235, 0.3);
}

.back-chip .arrow {
  display: inline-flex;
  align-items: center;
}

.media-card img {
  width: 100%;
  border-radius: 1.25rem;
  object-fit: cover;
  max-height: 260px;
  box-shadow: 0 25px 45px rgba(15, 23, 42, 0.2);
}

.panel {
  background: #fff;
  border-radius: 1.25rem;
  padding: 1.5rem;
  box-shadow: 0 25px 45px rgba(15, 23, 42, 0.08);
}

.info-header {
  margin-bottom: 1rem;
}

.title-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
}

.star-btn {
  border: none;
  background: transparent;
  font-size: 2rem;
  color: #cbd5f5;
  cursor: pointer;
  transition: color 0.2s ease;
}

.star-btn.active {
  color: #facc15;
}

.eyebrow {
  text-transform: uppercase;
  letter-spacing: 0.1em;
  font-size: 0.75rem;
  color: #94a3b8;
  margin: 0 0 0.35rem;
}

.info-card h1 {
  margin: 0;
}

.meta-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.meta-list li {
  display: flex;
  gap: 0.75rem;
  font-size: 0.95rem;
}

.meta-icon {
  width: 36px;
  height: 36px;
  border-radius: 0.75rem;
  background: #eef2ff;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.meta-list strong {
  display: block;
  color: #0f172a;
}

.link-chip {
  border: none;
  background: transparent;
  color: #2563eb;
  font-weight: 700;
  cursor: pointer;
  padding: 0;
  font-size: 1.05rem;
  letter-spacing: 0.02em;
}

.tag-group {
  margin-top: 1rem;
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.chip {
  padding: 0.3rem 0.9rem;
  border-radius: 999px;
  font-size: 0.85rem;
  border: 1px solid rgba(37, 99, 235, 0.2);
  color: #2563eb;
  background: rgba(37, 99, 235, 0.08);
}

.section-header {
  display: flex;
  gap: 0.75rem;
  align-items: center;
  margin-bottom: 1rem;
}

.section-icon {
  font-size: 1.25rem;
}

.section-eyebrow {
  margin: 0;
  font-size: 0.8rem;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: #94a3b8;
}

.section-header h2 {
  margin: 0.15rem 0 0;
}

.bullet-list {
  margin: 0;
  padding-left: 1.25rem;
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
  color: #374151;
}

.description {
  margin: 0;
  color: #374151;
  line-height: 1.6;
}

.organizer-card {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.organizer-card img {
  width: 60px;
  height: 60px;
  border-radius: 999px;
  object-fit: cover;
  border: 2px solid rgba(226, 232, 240, 0.8);
}

.organizer-card strong {
  display: block;
  font-size: 1rem;
}

.organizer-card p {
  margin: 0.25rem 0;
  color: #475569;
}

.organizer-links {
  display: flex;
  gap: 0.75rem;
  font-size: 0.9rem;
  color: #64748b;
}

.organizer-links a {
  text-decoration: none;
  color: #2563eb;
  font-weight: 600;
}

.empty-state {
  background: #fff;
  padding: 3rem;
  border-radius: 1.5rem;
  text-align: center;
  box-shadow: 0 20px 45px rgba(15, 23, 42, 0.08);
}

.back-link {
  display: inline-block;
  margin-top: 1rem;
  padding: 0.75rem 1.5rem;
  border-radius: 999px;
  background: #2563eb;
  color: #fff;
  text-decoration: none;
}

@media (max-width: 640px) {
  .panel {
    padding: 1.25rem;
  }
}
</style>
