<template>
  <a-config-provider
    :theme="{ token: { colorPrimary: themeColor }, algorithm: isDark ? theme.darkAlgorithm : theme.defaultAlgorithm }">
    <div class="app-wrapper">
      <router-view @toggleTheme="toggleTheme" />
      <div class="theme-switcher">
        <a-button type="default" @click="setTheme('#1677ff')" style="margin-right:8px;">默认蓝</a-button>
        <a-button type="default" @click="setTheme('#52c41a')" style="margin-right:8px;">绿色</a-button>
        <a-button type="default" @click="setTheme('#faad14')">橙色</a-button>
      </div>
    </div>
  </a-config-provider>
</template>

<script setup lang="ts">
import { ref, watch } from "vue";
import { theme } from "ant-design-vue";
const themeColor = ref("#1677ff");
const isDark = ref(false);

function setTheme(color: string) {
  themeColor.value = color;
}

function toggleTheme() {
  isDark.value = !isDark.value;
}

watch(isDark, (val) => {
  document.body.style.background = val ? "#18181c" : "#f6f6f6";
  document.body.style.color = val ? "#f6f6f6" : "#18181c";
});
</script>

<style scoped>
.app-wrapper {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  align-items: center;
  padding-top: 48px;
}

.theme-switcher {
  margin-top: 40px;
  padding: 24px 0;
  text-align: center;
  background: transparent;
}
</style>