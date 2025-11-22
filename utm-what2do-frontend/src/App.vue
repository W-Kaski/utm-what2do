<template>
  <MainLayout>
    <RouterView />
  </MainLayout>
</template>

<script setup>
import { onMounted } from 'vue';
import { RouterView } from 'vue-router';
import MainLayout from '@/layouts/MainLayout.vue';
import { useUserStore } from '@/stores/user';

const userStore = useUserStore();

// Restore user state from localStorage on app init
onMounted(() => {
  userStore.initFromStorage();
});
</script>
