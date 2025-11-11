<template>
  <section class="create">
    <h2>创建活动</h2>
    <p>在接入后端 API 前，这里展示表单骨架供后续开发。</p>
    <form @submit.prevent>
      <label>活动名称<input type="text" placeholder="请输入活动名称" /></label>
      <label>日期<input type="date" /></label>
      <label>地点<input type="text" placeholder="活动地点" /></label>
      <label>简介<textarea rows="4" placeholder="活动介绍"></textarea></label>
      <button type="submit">提交活动</button>
    </form>
  </section>
</template>

<script setup>
// 表单逻辑与 API 集成后续补充
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
