<template>
    <a-card style="max-width:400px;margin:40px auto;text-align:center;">
        <h2 style="margin-bottom:16px;">首页</h2>
        <p style="color:#888;margin-bottom:24px;">本项目采用 Tauri + Vue3 + Ant Design Vue，体验高性能桌面与移动开发。</p>
        <a-button type="primary" @click="$router.push('/about')" style="margin-right:16px;">跳转到关于页</a-button>
        <a-button type="default" @click="emit('toggleTheme')">切换深浅色主题</a-button>
    </a-card>
</template>

<script setup lang="ts">
const emit = defineEmits(['toggleTheme']);
</script>
