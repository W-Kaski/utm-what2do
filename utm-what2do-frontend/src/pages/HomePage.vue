<template>
  <div class="home">
    <section class="hero-card">
      <div class="hero-text">
        <p class="eyebrow">UTM campus activity guide</p>
        <h1>
          Discover. Join. Create.
          <span>What2Do</span>
        </h1>
        <p class="lede">Stay immersed in campus highlights, grab seats at high-quality events, and spark ideas anytime.</p>

        <form class="search" @submit.prevent="handleSearch">
          <label for="search-input" class="sr-only">Search events</label>
          <input
            id="search-input"
            v-model="query"
            type="search"
            placeholder="What do you want to do? e.g., concerts, clubs, open lectures"
          />
          <button type="submit">Search</button>
        </form>

        <div class="quick-tags">
          <span>Popular tags:</span>
          <button v-for="tag in quickFilters" :key="tag" type="button" @click="handleQuickTag(tag)">
            {{ tag }}
          </button>
        </div>

        <div class="hero-actions">
          <RouterLink to="/events" class="primary">Browse events</RouterLink>
          <RouterLink to="/events/new" class="secondary">Create event</RouterLink>
        </div>
      </div>

      <div class="hero-visual">
        <div class="hero-panel">
          <p>This week's picks</p>
          <div class="hero-stats">
            <button type="button" class="stat-block" @click="goToHotTag">
              <strong>{{ featuredHotEvents.length }}</strong>
              <span>Featured events</span>
            </button>
            <button type="button" class="stat-block" @click="goToHotClubs">
              <strong>{{ hotClubs.length }}</strong>
              <span>Spotlight clubs</span>
            </button>
            <button type="button" class="stat-block" @click="goToOfficialEvents">
              <strong>{{ officialEvents.length }}</strong>
              <span>Student support</span>
            </button>
          </div>
          <ul>
            <li
              v-for="event in recommendedEvents"
              :key="event.id"
              role="button"
              tabindex="0"
              @click="goToEventDetail(event.id)"
              @keyup.enter="goToEventDetail(event.id)"
            >
              <strong>{{ event.title }}</strong>
              <span>{{ formatDate(event.date) }} · {{ event.location }}</span>
            </li>
          </ul>
        </div>
      </div>
    </section>

    <section class="panel">
      <header class="panel__header">
        <div>
          <p class="eyebrow eyebrow--soft">Events</p>
          <h2>Starting soon</h2>
        </div>
        <RouterLink to="/events">See all</RouterLink>
      </header>

      <div class="event-grid">
        <article v-for="event in upcomingEvents" :key="event.id" class="event-card">
          <div class="event-card__badge">{{ event.tags?.[0] || 'Event' }}</div>
          <h3>{{ event.title }}</h3>
          <p>{{ event.description }}</p>
          <dl>
            <div>
              <dt>Time</dt>
              <dd>{{ formatDate(event.date) }}</dd>
            </div>
            <div>
              <dt>Location</dt>
              <dd>{{ event.location }}</dd>
            </div>
            <div>
              <dt>Club</dt>
              <dd>{{ event.club }}</dd>
            </div>
          </dl>
          <RouterLink :to="`/events/${event.id}`">View details</RouterLink>
        </article>
      </div>
    </section>

    <section class="panel panel--clubs">
      <header class="panel__header">
        <div>
          <p class="eyebrow eyebrow--soft">Clubs</p>
          <h2>Explore clubs by interest</h2>
        </div>
        <RouterLink to="/clubs">More clubs</RouterLink>
      </header>

      <div class="club-grid">
        <article v-for="club in clubCategories" :key="club.label" class="club-card">
          <span class="club-card__icon" :style="{ background: club.accent }">
            {{ club.icon }}
          </span>
          <div>
            <h3>{{ club.label }}</h3>
            <p>{{ club.description }}</p>
          </div>
          <button type="button">Follow</button>
        </article>
      </div>
    </section>

    <section class="panel panel--insights">
      <div class="insight-card" v-for="insight in insightCards" :key="insight.title">
        <p class="insight-card__label">{{ insight.title }}</p>
        <h3>{{ insight.value }}</h3>
        <p>{{ insight.caption }}</p>
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue';
import { RouterLink, useRouter } from 'vue-router';
import { storeToRefs } from 'pinia';

import { useEventStore } from '@/stores/events';
import { useFilterStore } from '@/stores/filters';
import { clubs } from '@/data/clubs';
import { HOT_CLUB_THRESHOLD, HOT_EVENT_TAG, HOT_EVENT_THRESHOLD, OFFICIAL_TAG } from '@/constants/highlights';

const router = useRouter();
const quickFilters = ['Academic', 'Tech', 'Music', 'Social', HOT_EVENT_TAG, OFFICIAL_TAG];

const clubCategories = [
  { label: 'Academic', description: 'Lectures, reading circles, research discussions', icon: '📘', accent: 'rgba(14, 165, 233, 0.15)' },
  { label: 'Arts', description: 'Music, theatre, visual arts', icon: '🎭', accent: 'rgba(244, 114, 182, 0.15)' },
  { label: 'Sports', description: 'Team matches and training sessions', icon: '⚽️', accent: 'rgba(74, 222, 128, 0.15)' },
  { label: 'Culture', description: 'Language exchanges and multicultural experiences', icon: '🌏', accent: 'rgba(251, 191, 36, 0.2)' }
];

const eventStore = useEventStore();
const filterStore = useFilterStore();
const { events } = storeToRefs(eventStore);
const { searchTerm: globalSearch } = storeToRefs(filterStore);

const query = ref(globalSearch.value);

watch(globalSearch, (value) => {
  query.value = value;
});

const hotEvents = computed(() =>
  [...events.value].sort((a, b) => (b.signupCount || 0) - (a.signupCount || 0))
);

const featuredHotEvents = computed(() => hotEvents.value.slice(0, 5));

const isWithinNextDays = (isoString, days = 7) => {
  if (!isoString) return false;
  const eventDate = new Date(isoString);
  if (Number.isNaN(eventDate.getTime())) return false;
  const now = new Date();
  const diff = eventDate - now;
  return diff >= 0 && diff <= days * 24 * 60 * 60 * 1000;
};

const recommendedEvents = computed(() => {
  const candidates = hotEvents.value.filter((event) => isWithinNextDays(event.date));
  if (candidates.length >= 2) {
    return candidates.slice(0, 2);
  }
  return hotEvents.value.slice(0, 2);
});

const officialEvents = computed(() => events.value.filter((event) => event.isOfficial));

const hotClubs = computed(() =>
  clubs
    .filter((club) => Number(club.stats?.members || 0) >= HOT_CLUB_THRESHOLD)
    .sort((a, b) => b.stats.members - a.stats.members)
    .slice(0, 3)
);

const upcomingEvents = computed(() => events.value.slice(0, 6));

const insightCards = computed(() => [
  {
    title: 'Hot Today',
    value: `${featuredHotEvents.value.length} events heating up`,
    caption: `Top RSVP count: ${hotEvents.value[0]?.signupCount || 0}`
  },
  {
    title: 'Spotlight Clubs',
    value: `${hotClubs.value.length} featured`,
    caption: `Members ≥ ${HOT_CLUB_THRESHOLD}`
  },
  {
    title: 'Student Support',
    value: `${officialEvents.value.length} official events`,
    caption: 'From school-certified hosts'
  }
]);

const formatDate = (isoString) => {
  if (!isoString) return 'TBD';
  try {
    const formatter = new Intl.DateTimeFormat('en-CA', {
      month: 'short',
      day: 'numeric'
    });
    return formatter.format(new Date(isoString));
  } catch (error) {
    return isoString;
  }
};

const goToEventsWithTerm = (value, options = {}) => {
  const trimmed = value.trim();
  filterStore.setSearchTerm(trimmed);
  const queryParams = trimmed ? { search: trimmed } : {};
  if (options.tag) {
    queryParams.tag = options.tag;
  }
  router
    .push({
      name: options.routeName || 'events',
      query: queryParams
    })
    .catch(() => {});
};

const handleSearch = () => {
  goToEventsWithTerm(query.value || '');
};

const handleQuickTag = (tag) => {
  query.value = tag;
  goToEventsWithTerm(tag);
};

const goToEventDetail = (id) => {
  router.push({ name: 'event-detail', params: { id } });
};

const goToHotTag = () => {
  goToEventsWithTerm('', { routeName: 'explore', tag: HOT_EVENT_TAG });
};

const goToHotClubs = () => {
  router.push({ name: 'clubs', query: { filter: 'hot-clubs' } });
};

const goToOfficialEvents = () => {
  router.push({ name: 'events', query: { tag: 'official' } });
};
</script>

<style scoped>
.home {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.hero-card {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 2rem;
  background: linear-gradient(120deg, #eef2ff, #dbeafe);
  padding: 2.5rem;
  border-radius: 2rem;
  position: relative;
  overflow: hidden;
}

.hero-card::after {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at 20% 20%, rgba(59, 130, 246, 0.3), transparent 55%);
  pointer-events: none;
}

.hero-text {
  position: relative;
  z-index: 1;
}

.eyebrow {
  text-transform: uppercase;
  letter-spacing: 0.2em;
  font-size: 0.75rem;
  margin: 0 0 0.5rem;
  color: #64748b;
}

.eyebrow--soft {
  letter-spacing: 0.15em;
}

h1 {
  font-size: clamp(2.2rem, 5vw, 3.2rem);
  margin: 0;
  color: #0f172a;
}

h1 span {
  color: #2563eb;
}

.lede {
  margin: 1rem 0 1.5rem;
  font-size: 1.1rem;
  color: #475569;
}

.search {
  display: flex;
  gap: 0.75rem;
  padding: 0.75rem;
  background: #fff;
  border-radius: 999px;
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.12);
}

.search input {
  border: none;
  flex: 1;
  font-size: 1rem;
  outline: none;
}

.search button {
  border-radius: 999px;
  padding: 0.6rem 1.2rem;
  background: #2563eb;
  color: #fff;
  border: none;
  font-weight: 600;
}

.quick-tags {
  margin-top: 0.75rem;
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  align-items: center;
  color: #475569;
}

.quick-tags button {
  border: none;
  background: rgba(37, 99, 235, 0.12);
  color: #1d4ed8;
  padding: 0.25rem 0.75rem;
  border-radius: 999px;
  font-size: 0.85rem;
}

.hero-actions {
  margin-top: 1.5rem;
  display: flex;
  gap: 0.75rem;
}

.primary,
.secondary {
  border-radius: 999px;
  padding: 0.85rem 1.5rem;
  font-weight: 600;
  text-decoration: none;
  text-align: center;
}

.primary {
  background: #1d4ed8;
  color: #fff;
}

.secondary {
  border: 1px solid rgba(255, 255, 255, 0.5);
  color: #1d4ed8;
  background: rgba(255, 255, 255, 0.8);
}

.hero-visual {
  position: relative;
  z-index: 1;
}

.hero-panel {
  background: #0f172a;
  color: #e2e8f0;
  border-radius: 1.5rem;
  padding: 2rem;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  box-shadow: 0 25px 50px rgba(15, 23, 42, 0.45);
}

.hero-panel p {
  margin: 0;
  color: #94a3b8;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.hero-stats {
  display: flex;
  justify-content: space-between;
  text-align: center;
  gap: 0.75rem;
}

.stat-block {
  background: transparent;
  border: none;
  color: #cbd5f5;
  display: flex;
  flex-direction: column;
  gap: 0.2rem;
  align-items: center;
  justify-content: center;
  padding: 0.5rem;
  cursor: pointer;
}

.hero-stats strong {
  display: block;
  font-size: 1.75rem;
  color: #fff;
}

.stat-block span {
  color: #94a3b8;
  font-size: 0.85rem;
}

.hero-panel ul {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.hero-panel li {
  padding: 0.75rem;
  border-radius: 1rem;
  background: rgba(148, 163, 184, 0.15);
  cursor: pointer;
}

.hero-panel li strong {
  display: block;
  color: #fff;
}

.hero-panel li:hover {
  background: rgba(59, 130, 246, 0.25);
}

.panel {
  background: #fff;
  border-radius: 1.5rem;
  padding: 2rem;
  box-shadow: 0 20px 45px rgba(15, 23, 42, 0.08);
}

.panel__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.panel__header h2 {
  margin: 0.25rem 0 0;
  font-size: 1.75rem;
  color: #0f172a;
}

.panel__header a {
  text-decoration: none;
  color: #2563eb;
  font-weight: 600;
}

.event-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 1.5rem;
}

.event-card {
  border: 1px solid rgba(226, 232, 240, 0.8);
  border-radius: 1.25rem;
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  background: linear-gradient(180deg, rgba(248, 250, 252, 1), #fff);
}

.event-card__badge {
  width: fit-content;
  padding: 0.25rem 0.75rem;
  border-radius: 999px;
  font-size: 0.8rem;
  background: rgba(59, 130, 246, 0.12);
  color: #2563eb;
  font-weight: 600;
}

.event-card h3 {
  margin: 0;
  font-size: 1.25rem;
  color: #0f172a;
}

.event-card p {
  margin: 0;
  color: #475569;
  min-height: 48px;
}

.event-card dl {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0.5rem 1.5rem;
  margin: 0;
}

.event-card dt {
  font-size: 0.75rem;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: #94a3b8;
}

.event-card dd {
  margin: 0;
  color: #111827;
  font-weight: 600;
}

.event-card a {
  margin-top: auto;
  text-decoration: none;
  font-weight: 600;
  color: #2563eb;
}

.club-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1rem;
}

.club-card {
  border: 1px solid rgba(226, 232, 240, 0.8);
  border-radius: 1rem;
  padding: 1.25rem;
  display: flex;
  align-items: center;
  gap: 1rem;
  background: #f8fafc;
}

.club-card__icon {
  width: 48px;
  height: 48px;
  border-radius: 1rem;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
}

.club-card h3 {
  margin: 0;
}

.club-card p {
  margin: 0.2rem 0 0;
  color: #475569;
}

.club-card button {
  margin-left: auto;
  border: none;
  background: #fff;
  border-radius: 999px;
  padding: 0.4rem 1rem;
  font-weight: 600;
  color: #2563eb;
  border: 1px solid rgba(37, 99, 235, 0.2);
}

.panel--insights {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 1rem;
  padding: 0;
  background: transparent;
  box-shadow: none;
}

.insight-card {
  padding: 1.5rem;
  border-radius: 1.25rem;
  background: #fff;
  border: 1px solid rgba(226, 232, 240, 0.8);
  box-shadow: 0 15px 25px rgba(15, 23, 42, 0.06);
}

.insight-card__label {
  text-transform: uppercase;
  letter-spacing: 0.1em;
  font-size: 0.75rem;
  color: #94a3b8;
  margin: 0;
}

.insight-card h3 {
  margin: 0.5rem 0;
  font-size: 1.75rem;
  color: #0f172a;
}

.insight-card p {
  margin: 0;
  color: #475569;
}

.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  border: 0;
}

@media (max-width: 640px) {
  .hero-card {
    padding: 1.75rem;
  }

  .search {
    flex-direction: column;
    border-radius: 1.5rem;
  }

  .search button {
    width: 100%;
  }

  .event-card dl {
    grid-template-columns: 1fr;
  }
}
</style>
