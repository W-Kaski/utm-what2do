<template>
  <section class="create">
    <h2>Create event</h2>
    <p>This is a placeholder form until we wire it up to the backend API.</p>
    <form @submit.prevent>
      <label>Event name<input type="text" placeholder="Enter the event name" /></label>
      <label>Date<input type="date" /></label>
      <label>Location<input type="text" placeholder="Event location" /></label>
      <label>Description<textarea rows="4" placeholder="Event description"></textarea></label>
      <button type="submit">Submit event</button>
    </form>
  </section>
</template>

<script setup>
// Form logic and API integration will be added later.
</script>

<style scoped>
.create {
  background: #fff;
  padding: 2rem;
  border-radius: 1rem;
}

form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  margin-top: 1rem;
}

label {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  font-weight: 600;
}

input,
textarea {
  padding: 0.75rem;
  border-radius: 0.75rem;
  border: 1px solid #d1d5db;
  font-size: 1rem;
}
</style>
