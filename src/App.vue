<template>
  <a-config-provider :theme="{ token: { colorPrimary: themeColor } }">
    <router-view />
    <div style="margin-top: 24px; text-align:center;">
      <span>切换主题色：</span>
      <a-button type="default" @click="setTheme('#1677ff')" style="margin-right:8px;">默认蓝</a-button>
      <a-button type="default" @click="setTheme('#52c41a')" style="margin-right:8px;">绿色</a-button>
      <a-button type="default" @click="setTheme('#faad14')">橙色</a-button>
    </div>
  </a-config-provider>
</template>

<script setup lang="ts">
import { ref } from "vue";
const themeColor = ref("#1677ff");

function setTheme(color: string) {
  themeColor.value = color;
}

</script>

<style scoped></style>