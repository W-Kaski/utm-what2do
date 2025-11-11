<template>
  <div class="marker" :style="positionStyle" @click="$emit('select', event.id)">
    <span>{{ event.title }}</span>
  </div>
</template>

<script setup>
const props = defineProps({
  event: {
    type: Object,
    required: true
  }
});

const positionStyle = {
  left: '50%',
  top: '50%'
};
</script>

<style scoped>
.marker {
  position: absolute;
  transform: translate(-50%, -50%);
  background: #22d3ee;
  padding: 0.35rem 0.6rem;
  border-radius: 999px;
  font-size: 0.75rem;
  color: #0f172a;
  box-shadow: 0 10px 20px rgba(15, 23, 42, 0.15);
}
</style>
