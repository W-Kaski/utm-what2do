<template>
  <div class="auth-page">
    <div class="auth-card">
      <header class="auth-header">
        <h1>{{ isLogin ? 'Welcome Back' : 'Create Account' }}</h1>
        <p>{{ isLogin ? 'Sign in to continue to UTM What2Do' : 'Join UTM What2Do community' }}</p>
      </header>

      <form class="auth-form" @submit.prevent="handleSubmit">
        <div v-if="error" class="error-message">
          {{ error }}
        </div>

        <div class="form-group">
          <label for="username">Username</label>
          <input
            id="username"
            v-model="form.username"
            type="text"
            :placeholder="isLogin ? 'Enter your username' : 'Choose a username'"
            required
          />
        </div>

        <div v-if="!isLogin" class="form-group">
          <label for="email">Email</label>
          <input
            id="email"
            v-model="form.email"
            type="email"
            placeholder="Enter your email"
            required
          />
        </div>

        <div class="form-group">
          <label for="password">Password</label>
          <input
            id="password"
            v-model="form.password"
            type="password"
            placeholder="Enter your password"
            required
            minlength="6"
          />
        </div>

        <div v-if="!isLogin" class="form-group">
          <label for="confirmPassword">Confirm Password</label>
          <input
            id="confirmPassword"
            v-model="form.confirmPassword"
            type="password"
            placeholder="Confirm your password"
            required
          />
        </div>

        <button type="submit" class="submit-btn" :disabled="loading">
          {{ loading ? 'Please wait...' : (isLogin ? 'Sign In' : 'Create Account') }}
        </button>
      </form>

      <footer class="auth-footer">
        <p v-if="isLogin">
          Don't have an account?
          <button type="button" @click="toggleMode">Sign up</button>
        </p>
        <p v-else>
          Already have an account?
          <button type="button" @click="toggleMode">Sign in</button>
        </p>
      </footer>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/user';

const router = useRouter();
const userStore = useUserStore();

const isLogin = ref(true);
const loading = ref(false);
const error = ref('');

const form = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
});

const toggleMode = () => {
  isLogin.value = !isLogin.value;
  error.value = '';
  form.username = '';
  form.email = '';
  form.password = '';
  form.confirmPassword = '';
};

const handleSubmit = async () => {
  error.value = '';

  if (!isLogin.value && form.password !== form.confirmPassword) {
    error.value = 'Passwords do not match';
    return;
  }

  loading.value = true;

  try {
    if (isLogin.value) {
      await userStore.login({
        username: form.username,
        password: form.password
      });
    } else {
      await userStore.register({
        username: form.username,
        email: form.email,
        password: form.password
      });
      // After registration, login automatically
      await userStore.login({
        username: form.username,
        password: form.password
      });
    }
    router.push({ name: 'home' });
  } catch (err) {
    error.value = err.response?.data?.message || userStore.error || 'An error occurred';
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.auth-card {
  background: #fff;
  border-radius: 1.5rem;
  padding: 2.5rem;
  width: 100%;
  max-width: 420px;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.15);
}

.auth-header {
  text-align: center;
  margin-bottom: 2rem;
}

.auth-header h1 {
  margin: 0;
  font-size: 1.75rem;
  color: #0f172a;
}

.auth-header p {
  margin: 0.5rem 0 0;
  color: #64748b;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.error-message {
  background: #fef2f2;
  border: 1px solid #fecaca;
  color: #dc2626;
  padding: 0.75rem 1rem;
  border-radius: 0.75rem;
  font-size: 0.875rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.form-group label {
  font-weight: 600;
  color: #374151;
  font-size: 0.875rem;
}

.form-group input {
  padding: 0.75rem 1rem;
  border: 1px solid #e2e8f0;
  border-radius: 0.75rem;
  font-size: 1rem;
  transition: border-color 0.15s ease, box-shadow 0.15s ease;
}

.form-group input:focus {
  outline: none;
  border-color: #2563eb;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
}

.submit-btn {
  margin-top: 0.5rem;
  padding: 0.875rem;
  border: none;
  border-radius: 0.75rem;
  background: #2563eb;
  color: #fff;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.15s ease, transform 0.15s ease;
}

.submit-btn:hover:not(:disabled) {
  background: #1d4ed8;
  transform: translateY(-1px);
}

.submit-btn:disabled {
  background: #94a3b8;
  cursor: not-allowed;
}

.auth-footer {
  margin-top: 1.5rem;
  text-align: center;
}

.auth-footer p {
  margin: 0;
  color: #64748b;
}

.auth-footer button {
  border: none;
  background: none;
  color: #2563eb;
  font-weight: 600;
  cursor: pointer;
  padding: 0;
}

.auth-footer button:hover {
  text-decoration: underline;
}
</style>
