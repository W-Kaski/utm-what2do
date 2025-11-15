<template>
  <article class="card">
    <header>
      <h3>{{ event.title }}</h3>
      <small>{{ formattedDate }}</small>
    </header>
    <p class="excerpt">{{ event.description }}</p>
    <footer>
      <span class="location">{{ event.location }}</span>
      <RouterLink :to="`/events/${event.id}`" class="ghost-link">View details →</RouterLink>
    </footer>
  </article>
</template>

<script setup>
import { computed } from 'vue';
import { RouterLink } from 'vue-router';

const props = defineProps({
  event: {
    type: Object,
    required: true
  }
});

const formattedDate = computed(() => {
  if (!props.event.date) return 'Date TBD';
  const formatter = new Intl.DateTimeFormat('en-CA', {
    month: 'long',
    day: 'numeric'
  });
  return formatter.format(new Date(props.event.date));
});
</script>

<style scoped>
.card {
  background: #fff;
  border-radius: 1rem;
  padding: 1.5rem;
  box-shadow: 0 15px 30px rgba(15, 23, 42, 0.06);
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  height: 100%;
}

header {
  display: flex;
  flex-direction: column;
  gap: 0.2rem;
}

header small {
  color: #64748b;
  font-weight: 600;
}

h3 {
  margin: 0;
  font-size: 1.2rem;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.excerpt {
  color: #475569;
  margin: 0;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.9rem;
  margin-top: auto;
}

.location {
  max-width: 50%;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  color: #1f2937;
}

.ghost-link {
  text-decoration: none;
  font-weight: 600;
  color: #1d4ed8;
  border: 1px solid rgba(29, 78, 216, 0.2);
  border-radius: 999px;
  padding: 0.35rem 0.75rem;
  font-size: 0.85rem;
}
</style>
