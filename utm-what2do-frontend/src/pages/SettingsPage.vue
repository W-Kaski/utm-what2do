<template>
  <div class="settings-page">
    <header class="page-header">
      <h1>Settings</h1>
      <p>Manage your account preferences</p>
    </header>

    <div class="settings-content">
      <aside class="settings-nav">
        <button
          v-for="section in sections"
          :key="section.id"
          :class="['nav-btn', { active: activeSection === section.id }]"
          @click="activeSection = section.id"
        >
          <span class="nav-icon">{{ section.icon }}</span>
          {{ section.label }}
        </button>
      </aside>

      <div class="settings-panel">
        <!-- Account Settings -->
        <div v-if="activeSection === 'account'" class="panel-section">
          <h2>Account Settings</h2>

          <div class="form-group">
            <label>Email</label>
            <input type="email" v-model="settings.email" />
          </div>

          <div class="form-group">
            <label>Username</label>
            <input type="text" v-model="settings.username" />
          </div>

          <div class="form-group">
            <label>Display Name</label>
            <input type="text" v-model="settings.displayName" />
          </div>

          <button class="save-btn" @click="saveSettings">Save Changes</button>
        </div>

        <!-- Notifications -->
        <div v-if="activeSection === 'notifications'" class="panel-section">
          <h2>Notification Preferences</h2>

          <div class="toggle-group">
            <label>
              <input type="checkbox" v-model="settings.notifications.email" />
              Email Notifications
            </label>
            <p>Receive updates about events and posts via email</p>
          </div>

          <div class="toggle-group">
            <label>
              <input type="checkbox" v-model="settings.notifications.push" />
              Push Notifications
            </label>
            <p>Get notified about new activities in real-time</p>
          </div>

          <div class="toggle-group">
            <label>
              <input type="checkbox" v-model="settings.notifications.events" />
              Event Reminders
            </label>
            <p>Receive reminders before events you've saved</p>
          </div>

          <button class="save-btn" @click="saveSettings">Save Preferences</button>
        </div>

        <!-- Privacy -->
        <div v-if="activeSection === 'privacy'" class="panel-section">
          <h2>Privacy Settings</h2>

          <div class="toggle-group">
            <label>
              <input type="checkbox" v-model="settings.privacy.profilePublic" />
              Public Profile
            </label>
            <p>Allow others to view your profile</p>
          </div>

          <div class="toggle-group">
            <label>
              <input type="checkbox" v-model="settings.privacy.showActivity" />
              Show Activity
            </label>
            <p>Display your recent activity on your profile</p>
          </div>

          <button class="save-btn" @click="saveSettings">Save Privacy Settings</button>
        </div>

        <!-- Appearance -->
        <div v-if="activeSection === 'appearance'" class="panel-section">
          <h2>Appearance</h2>

          <div class="form-group">
            <label>Theme</label>
            <select v-model="settings.theme">
              <option value="light">Light</option>
              <option value="dark">Dark</option>
              <option value="system">System</option>
            </select>
          </div>

          <button class="save-btn" @click="saveSettings">Save Appearance</button>
        </div>

        <!-- Danger Zone -->
        <div v-if="activeSection === 'danger'" class="panel-section danger">
          <h2>Danger Zone</h2>

          <div class="danger-action">
            <div>
              <strong>Delete Account</strong>
              <p>Permanently delete your account and all data</p>
            </div>
            <button class="danger-btn" @click="confirmDelete">Delete Account</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { useUserStore } from '@/stores/user';

const userStore = useUserStore();

const sections = [
  { id: 'account', label: 'Account', icon: '👤' },
  { id: 'notifications', label: 'Notifications', icon: '🔔' },
  { id: 'privacy', label: 'Privacy', icon: '🔒' },
  { id: 'appearance', label: 'Appearance', icon: '🎨' },
  { id: 'danger', label: 'Danger Zone', icon: '⚠️' }
];

const activeSection = ref('account');

const settings = reactive({
  email: userStore.email || '',
  username: userStore.username || '',
  displayName: userStore.name || '',
  notifications: {
    email: true,
    push: true,
    events: true
  },
  privacy: {
    profilePublic: true,
    showActivity: true
  },
  theme: 'light'
});

const saveSettings = async () => {
  try {
    await userStore.updateProfile({
      email: settings.email,
      username: settings.username,
      displayName: settings.displayName
    });
    alert('Settings saved successfully!');
  } catch (err) {
    alert('Failed to save settings');
  }
};

const confirmDelete = () => {
  if (confirm('Are you sure you want to delete your account? This action cannot be undone.')) {
    // Handle account deletion
    console.log('Delete account');
  }
};
</script>

<style scoped>
.settings-page {
  max-width: 1000px;
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

.settings-content {
  display: grid;
  grid-template-columns: 200px 1fr;
  gap: 1.5rem;
}

.settings-nav {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.nav-btn {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.75rem 1rem;
  border: none;
  border-radius: 0.75rem;
  background: transparent;
  color: #64748b;
  font-weight: 500;
  text-align: left;
  cursor: pointer;
  transition: all 0.15s ease;
}

.nav-btn:hover {
  background: #f1f5f9;
  color: #0f172a;
}

.nav-btn.active {
  background: #2563eb;
  color: #fff;
}

.settings-panel {
  background: #fff;
  border-radius: 1rem;
  padding: 2rem;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
}

.panel-section h2 {
  margin: 0 0 1.5rem;
  font-size: 1.25rem;
  color: #0f172a;
}

.form-group {
  margin-bottom: 1.25rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 600;
  color: #374151;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 0.75rem 1rem;
  border: 1px solid #e2e8f0;
  border-radius: 0.5rem;
  font-size: 1rem;
}

.toggle-group {
  margin-bottom: 1.25rem;
  padding-bottom: 1.25rem;
  border-bottom: 1px solid #f1f5f9;
}

.toggle-group label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-weight: 600;
  color: #0f172a;
  cursor: pointer;
}

.toggle-group p {
  margin: 0.25rem 0 0 1.5rem;
  font-size: 0.875rem;
  color: #64748b;
}

.save-btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 0.5rem;
  background: #2563eb;
  color: #fff;
  font-weight: 600;
  cursor: pointer;
}

.save-btn:hover {
  background: #1d4ed8;
}

.panel-section.danger h2 {
  color: #dc2626;
}

.danger-action {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  background: #fef2f2;
  border-radius: 0.5rem;
}

.danger-action strong {
  color: #991b1b;
}

.danger-action p {
  margin: 0.25rem 0 0;
  font-size: 0.875rem;
  color: #dc2626;
}

.danger-btn {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 0.5rem;
  background: #dc2626;
  color: #fff;
  font-weight: 600;
  cursor: pointer;
}

.danger-btn:hover {
  background: #b91c1c;
}

@media (max-width: 768px) {
  .settings-content {
    grid-template-columns: 1fr;
  }

  .settings-nav {
    flex-direction: row;
    overflow-x: auto;
  }
}
</style>
