<template>
  <section class="profile-page">
    <div class="cover" :style="coverStyle">
      <button type="button" class="cover-btn" @click="triggerCoverUpload">Change cover</button>
      <input
        ref="coverInput"
        class="sr-only"
        type="file"
        accept="image/*"
        @change="handleCoverChange"
      />
    </div>

    <div class="profile-header">
      <img class="avatar" :src="user.avatar" :alt="user.name" />
      <div>
        <h1>{{ user.name }}</h1>
        <p>{{ user.bio }}</p>
      </div>
    </div>

    <nav class="tab-bar">
      <button
        v-for="tab in tabs"
        :key="tab.id"
        type="button"
        :class="['tab-btn', { active: activeTab === tab.id }]"
        @click="activeTab = tab.id"
      >
        <span>{{ tab.icon }}</span>
        {{ tab.label }}
      </button>
    </nav>

    <section v-if="activeTab === 'favorites'" class="panel">
      <header class="panel-header">
        <h2>Saved events</h2>
        <p>{{ favoriteEvents.length }} events</p>
      </header>
      <div v-if="favoriteEvents.length" class="favorites-grid">
        <EventCard v-for="event in favoriteEvents" :key="event.id" :event="event" />
      </div>
      <div v-else class="empty">
        <p>No favorites yet. Head over to the events page and tap the star icon.</p>
        <RouterLink class="primary" :to="{ name: 'events' }">Browse events</RouterLink>
      </div>
    </section>

    <section v-else class="panel settings">
      <header class="panel-header">
        <h2>Settings</h2>
        <p>Manage your profile</p>
      </header>
      <div class="setting-field">
        <label>Display name</label>
        <input v-model="editableName" type="text" />
      </div>
      <div class="setting-field">
        <label>Bio</label>
        <textarea v-model="editableBio" rows="3"></textarea>
      </div>
      <div class="actions">
        <button type="button" class="primary" @click="applyProfileChanges">Save</button>
        <button type="button" class="ghost" @click="resetProfileChanges">Reset</button>
      </div>
    </section>
  </section>
</template>

<script setup>
import { computed, ref } from 'vue';
import { RouterLink } from 'vue-router';
import { storeToRefs } from 'pinia';

import EventCard from '@/components/EventCard.vue';
import { useEventStore } from '@/stores/events';
import { useUserStore } from '@/stores/user';

const userStore = useUserStore();
const eventStore = useEventStore();
const { favorites, coverImage } = storeToRefs(userStore);

const fallbackAvatar =
  'https://images.unsplash.com/photo-1504593811423-6dd665756598?auto=format&fit=crop&w=200&q=80';

const user = computed(() => ({
  name: userStore.name || 'UTM Explorer',
  bio: userStore.bio || 'Immerse yourself in campus events and save your favorite sparks of inspiration.',
  avatar: userStore.avatar || fallbackAvatar
}));

const coverInput = ref(null);
const activeTab = ref('favorites');
const editableName = ref(user.value.name);
const editableBio = ref(user.value.bio);

const tabs = [
  { id: 'favorites', label: 'Favorites', icon: '⭐' },
  { id: 'settings', label: 'Settings', icon: '⚙️' }
];

const coverStyle = computed(() => ({
  backgroundImage: `linear-gradient(120deg, rgba(15,23,42,0.55), rgba(15,23,42,0.1)), url(${coverImage.value})`
}));

const favoriteEvents = computed(() =>
  eventStore.events.filter((event) => favorites.value.includes(event.id))
);

const triggerCoverUpload = () => {
  coverInput.value?.click();
};

const handleCoverChange = (event) => {
  const file = event.target.files?.[0];
  if (!file) return;
  const reader = new FileReader();
  reader.onload = (e) => {
    userStore.setCoverImage(e.target?.result);
  };
  reader.readAsDataURL(file);
};

const applyProfileChanges = async () => {
  try {
    await userStore.updateProfile({
      displayName: editableName.value,
      bio: editableBio.value
    });
    // Also update local state
    userStore.name = editableName.value;
    userStore.bio = editableBio.value;
  } catch (err) {
    // Still update locally for demo
    userStore.name = editableName.value;
    userStore.bio = editableBio.value;
  }
};

const resetProfileChanges = () => {
  editableName.value = user.value.name;
  editableBio.value = user.value.bio;
};
</script>

<style scoped>
.profile-page {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.cover {
  height: 240px;
  border-radius: 1.5rem;
  background-size: cover;
  background-position: center;
  position: relative;
  box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.2), 0 25px 45px rgba(15, 23, 42, 0.12);
}

.cover-btn {
  position: absolute;
  right: 1.25rem;
  bottom: 1.25rem;
  border: none;
  border-radius: 999px;
  padding: 0.5rem 1.25rem;
  font-weight: 600;
  background: rgba(255, 255, 255, 0.85);
  color: #111827;
}

.profile-header {
  display: flex;
  gap: 1.25rem;
  align-items: center;
  padding: 0.5rem 0 1rem;
  border-bottom: 1px solid rgba(15, 23, 42, 0.1);
  margin-top: 0;
}

.avatar {
  width: 96px;
  height: 96px;
  border-radius: 999px;
  border: 4px solid #fff;
  object-fit: cover;
}

.profile-header h1 {
  margin: 0 0 0.25rem;
}

.profile-header p {
  margin: 0;
  color: #475569;
}

.tab-bar {
  display: flex;
  gap: 1.5rem;
  padding: 0.85rem 1rem;
  border-radius: 1rem;
  background: #0f172a;
  color: #cbd5f5;
  align-items: center;
}

.tab-btn {
  border: none;
  background: transparent;
  color: inherit;
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  gap: 0.4rem;
  position: relative;
  padding: 0.25rem 0;
  font-size: 0.95rem;
}

.tab-btn.active {
  color: #fcd34d;
}

.tab-btn.active::after {
  content: '';
  position: absolute;
  left: 0;
  right: 0;
  bottom: -0.4rem;
  height: 3px;
  border-radius: 999px;
  background: #38bdf8;
}

.panel {
  background: #fff;
  border-radius: 1.25rem;
  padding: 1.5rem;
  box-shadow: 0 25px 45px rgba(15, 23, 42, 0.08);
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-bottom: 1rem;
}

.panel-header h2 {
  margin: 0;
}

.panel-header p {
  margin: 0;
  color: #94a3b8;
}

.favorites-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 1rem;
}

.empty {
  text-align: center;
  color: #6b7280;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.primary {
  border: none;
  border-radius: 999px;
  background: #2563eb;
  color: #fff;
  padding: 0.6rem 1.5rem;
  font-weight: 600;
  text-decoration: none;
  display: inline-block;
}

.settings .setting-field {
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
  margin-bottom: 1rem;
}

.settings label {
  font-weight: 600;
  color: #475569;
}

.settings input,
.settings textarea {
  border-radius: 0.75rem;
  border: 1px solid rgba(148, 163, 184, 0.6);
  padding: 0.75rem;
  font-size: 1rem;
  font-family: inherit;
}

.actions {
  display: flex;
  gap: 0.75rem;
}

.ghost {
  border-radius: 999px;
  border: 1px solid rgba(148, 163, 184, 0.6);
  background: transparent;
  color: #475569;
  padding: 0.6rem 1.25rem;
  font-weight: 600;
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
  .profile-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .avatar {
    width: 80px;
    height: 80px;
  }
}
</style>
