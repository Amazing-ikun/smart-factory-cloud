<template>
  <div class="header accent-border">
    <div class="collapse-btn" @click="collapseChage">
      <el-icon v-if="!collapse"><Expand /></el-icon>
      <el-icon v-else><Fold /></el-icon>
    </div>
    <div class="logo">
      <span class="logo-accent">&#9670;</span>
      <span>智能制造云工厂</span>
    </div>
    <div class="header-right">
      <div class="datetime-display">
        <span class="dt-icon">&#9201;</span>
        <span class="dt-text">{{ currentDatetime }}</span>
      </div>
      <div class="header-user-con">
        <div class="user-avator">
          <img :src="userPic" />
        </div>
        <el-dropdown class="user-name" trigger="click" @command="handleCommand">
          <span class="el-dropdown-link">{{ username }}</span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="loginout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, computed, onMounted, onBeforeUnmount } from "vue";
import { useStore } from "vuex";
import { Expand, Fold } from "@element-plus/icons-vue";
import emitter from "@/utils/emitter";
import { RouterName } from "@/enums";

export default defineComponent({
  components: { Expand, Fold },
  setup() {
    const store = useStore();
    const username = sessionStorage.getItem("username") || "管理员";
    const collapse = ref(true);
    const userPic = computed(() => store.getters.userPic);
    const currentDatetime = ref("");
    let timer: number | null = null;

    function updateDatetime() {
      const now = new Date();
      const pad = (n: number) => String(n).padStart(2, "0");
      currentDatetime.value =
        now.getFullYear() + "-" +
        pad(now.getMonth() + 1) + "-" +
        pad(now.getDate()) + "  " +
        pad(now.getHours()) + ":" +
        pad(now.getMinutes()) + ":" +
        pad(now.getSeconds());
    }

    onMounted(() => {
      updateDatetime();
      timer = window.setInterval(updateDatetime, 1000);
    });

    onBeforeUnmount(() => {
      if (timer !== null) clearInterval(timer);
    });

    function collapseChage() {
      collapse.value = !collapse.value;
      emitter.emit("collapse", collapse.value);
    }

    function handleCommand(command: string) {
      if (command === "loginout") {
        sessionStorage.clear();
        window.location.href = RouterName.SignIn;
      }
    }

    return { username, userPic, collapse, collapseChage, handleCommand, currentDatetime };
  }
});
</script>

<style scoped>
.header {
  position: absolute;
  z-index: 100;
  width: 100%;
  height: 60px;
  display: flex;
  align-items: center;
  font-size: 20px;
  color: var(--text-header);
  background: var(--bg-header);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  box-shadow: var(--shadow-header);
}

.collapse-btn { padding: 0 21px; cursor: pointer; }

.header .logo {
  width: 250px;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 20px;
  letter-spacing: 1px;
}
.logo-accent {
  font-size: 14px;
  color: var(--accent-purple);
}

.header-right {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 18px;
  padding-right: 30px;
}
.datetime-display {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: var(--text-secondary);
  font-family: "SF Mono", "Fira Code", "Consolas", monospace;
  letter-spacing: 0.3px;
  padding: 0 12px;
  background: rgba(183, 77, 255, 0.04);
  border: 1px solid rgba(183, 77, 255, 0.08);
  border-radius: 6px;
  height: 30px;
  user-select: none;
}
.dt-icon {
  font-size: 14px;
  color: var(--accent-purple);
  opacity: 0.7;
}
.dt-text {
  background: linear-gradient(90deg, var(--accent-blue), var(--accent-purple));
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  font-weight: 500;
}

.header-right {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 18px;
  padding-right: 30px;
}
.header-user-con { display: flex; align-items: center; }
.user-name { margin-left: 10px; cursor: pointer; }
.user-name :deep(.el-dropdown-link) { color: var(--text-header); }
.user-avator img { width: 40px; height: 40px; border-radius: 50%; }

/* Dark mode dropdown overrides */
:deep(.el-dropdown-menu) {
  background: var(--bg-card);
  border-color: var(--border-color);
}
:deep(.el-dropdown-menu__item) {
  color: var(--text-primary);
}
:deep(.el-dropdown-menu__item:hover) {
  background: var(--glow-purple);
  color: var(--accent-purple);
}
</style>
