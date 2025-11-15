<template>
  <section v-if="club" class="club-detail">
    <div class="club-hero" :style="heroStyle">
      <div class="club-hero__overlay"></div>
      <div class="club-hero__content">
        <img :src="club.logo" :alt="club.name" />
        <div>
          <p class="eyebrow">Featured Club</p>
          <h1>{{ club.name }}</h1>
          <p>{{ club.blurb }}</p>
        </div>
      </div>
    </div>

    <div class="club-summary">
      <div>
        <p class="tagline">{{ club.tagline }}</p>
        <div class="stat-grid">
          <div>
            <strong>{{ club.stats.posts }}</strong>
            <span>Posts</span>
          </div>
          <div>
            <strong>{{ club.stats.members }}</strong>
            <span>Members</span>
          </div>
          <div>
            <strong>{{ club.stats.events }}</strong>
            <span>Events</span>
          </div>
        </div>
      </div>
      <div class="summary-actions">
        <button type="button">Contact club</button>
        <RouterLink :to="{ name: 'events' }">View events</RouterLink>
      </div>
    </div>

    <div class="detail-grid">
      <article class="info-block">
        <header>
          <h2>Posts</h2>
          <span>Club updates</span>
        </header>
        <ul>
          <li v-for="post in club.posts" :key="post.id">
            <div>
              <strong>{{ post.title }}</strong>
              <p>{{ post.preview }}</p>
            </div>
            <span>{{ post.time }}</span>
          </li>
        </ul>
      </article>

      <article class="info-block">
        <header>
          <h2>Events</h2>
          <span>Upcoming schedule</span>
        </header>
        <ul>
          <li v-for="event in club.events" :key="event.id">
            <div>
              <strong>{{ event.title }}</strong>
              <p>{{ event.location }}</p>
            </div>
            <span>{{ formatDate(event.date) }}</span>
          </li>
        </ul>
      </article>

      <article class="info-block">
        <header>
          <h2>Members</h2>
          <span>Core team</span>
        </header>
        <ul>
          <li v-for="member in club.members" :key="member.id">
            <div>
              <strong>{{ member.name }}</strong>
              <p>{{ member.role }}</p>
            </div>
          </li>
        </ul>
      </article>
    </div>

    <section class="newest-event">
      <header>
        <p class="eyebrow">Newest Event</p>
        <h2>{{ newestEvent.title }}</h2>
      </header>
      <p>{{ newestEvent.detail }}</p>
      <div class="newest-event__meta">
        <span>{{ formatDate(newestEvent.date) }}</span>
        <RouterLink :to="{ name: 'events' }">Go to registration</RouterLink>
      </div>
    </section>
  </section>

  <section v-else class="empty-state">
    <h2>Club not found</h2>
    <p>Please head back to the club list and pick another one.</p>
    <RouterLink to="/clubs">Back to clubs</RouterLink>
  </section>
</template>

<script setup>
import { computed } from 'vue';
import { RouterLink, useRoute } from 'vue-router';

import { findClubById } from '@/data/clubs';

const route = useRoute();

const club = computed(() => findClubById(route.params.id));

const heroStyle = computed(() => {
  if (!club.value?.coverImage) return {};
  return {
    backgroundImage: `linear-gradient(120deg, rgba(15,23,42,0.75), rgba(15,23,42,0.4)), url(${club.value.coverImage})`
  };
});

const newestEvent = computed(() => {
  if (!club.value) return null;
  return (
    club.value.newestEvent ||
    club.value.events?.[0] || {
      title: 'Event in planning',
      detail: 'Stay tuned for the club\'s next announcement.',
      date: null
    }
  );
});

const formatDate = (isoString) => {
  if (!isoString) return 'TBD';
  try {
    const formatter = new Intl.DateTimeFormat('en-US', {
      month: 'short',
      day: 'numeric'
    });
    return formatter.format(new Date(isoString));
  } catch (error) {
    return isoString;
  }
};
</script>

<style scoped>
.club-detail {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.club-hero {
  position: relative;
  border-radius: 1.75rem;
  padding: 2rem;
  color: #fff;
  background-size: cover;
  background-position: center;
  min-height: 220px;
  overflow: hidden;
}

.club-hero__overlay {
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at top left, rgba(59, 130, 246, 0.5), transparent 55%);
}

.club-hero__content {
  position: relative;
  display: flex;
  gap: 1.5rem;
  align-items: center;
}

.club-hero__content img {
  width: 96px;
  height: 96px;
  border-radius: 999px;
  border: 3px solid rgba(255, 255, 255, 0.8);
  object-fit: cover;
}

.eyebrow {
  text-transform: uppercase;
  letter-spacing: 0.1em;
  font-size: 0.85rem;
  margin: 0 0 0.35rem;
  color: rgba(255, 255, 255, 0.8);
}

.club-summary {
  background: #fff;
  border-radius: 1.5rem;
  padding: 1.5rem;
  box-shadow: 0 25px 45px rgba(15, 23, 42, 0.08);
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1.5rem;
}

.tagline {
  margin: 0 0 1rem;
  font-size: 1.2rem;
  color: #0f172a;
  font-weight: 600;
}

.stat-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 1rem;
}

.stat-grid strong {
  display: block;
  font-size: 1.75rem;
  color: #111827;
}

.stat-grid span {
  color: #94a3b8;
  text-transform: uppercase;
  font-size: 0.8rem;
  letter-spacing: 0.08em;
}

.summary-actions {
  display: flex;
  gap: 0.75rem;
}

.summary-actions button,
.summary-actions a {
  border-radius: 999px;
  padding: 0.75rem 1.5rem;
  border: none;
  font-weight: 600;
  text-decoration: none;
}

.summary-actions button {
  background: #2563eb;
  color: #fff;
}

.summary-actions a {
  border: 1px solid rgba(37, 99, 235, 0.3);
  color: #2563eb;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 1rem;
}

.info-block {
  background: #fff;
  border-radius: 1.25rem;
  padding: 1.25rem;
  box-shadow: 0 15px 35px rgba(15, 23, 42, 0.06);
}

.info-block header {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-bottom: 1rem;
}

.info-block h2 {
  margin: 0;
  font-size: 1.25rem;
}

.info-block span {
  color: #94a3b8;
  font-size: 0.85rem;
}

.info-block ul {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.info-block li {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
  border-bottom: 1px solid rgba(226, 232, 240, 0.7);
  padding-bottom: 0.6rem;
}

.info-block li:last-child {
  border-bottom: none;
}

.info-block strong {
  display: block;
  color: #0f172a;
}

.info-block p {
  margin: 0.2rem 0 0;
  color: #475569;
}

.info-block li span {
  color: #64748b;
}

.newest-event {
  background: linear-gradient(135deg, #eff6ff, #e0e7ff);
  border-radius: 1.5rem;
  padding: 2rem;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.newest-event__meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.newest-event__meta a {
  text-decoration: none;
  border-radius: 999px;
  padding: 0.6rem 1.25rem;
  border: 1px solid rgba(37, 99, 235, 0.3);
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

.empty-state h2 {
  margin: 0;
}

.empty-state a {
  margin-top: 1rem;
  display: inline-block;
  padding: 0.75rem 1.5rem;
  border-radius: 999px;
  background: #2563eb;
  color: #fff;
  text-decoration: none;
}

@media (max-width: 720px) {
  .club-hero__content {
    flex-direction: column;
    text-align: center;
  }

  .club-summary {
    flex-direction: column;
    align-items: stretch;
  }

  .summary-actions {
    flex-direction: column;
  }
}
</style>
