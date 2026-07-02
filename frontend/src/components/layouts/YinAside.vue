<template>
  <div class="sidebar" :class="{ collapsed: collapse }">
    <el-menu
      class="sidebar-el-menu"
      :background-color="menuBg"
      :text-color="menuTextColor"
      active-text-color="#b74dff"
      :default-active="activeMenu"
      router
      :collapse="collapse"
    >
      <el-menu-item index="/Home/Info">
        <el-icon><PieChart /></el-icon>
        <span>系统首页</span>
      </el-menu-item>
      <el-menu-item index="/Home/profile">
        <el-icon><User /></el-icon>
        <span>个人中心</span>
      </el-menu-item>
      <el-menu-item index="/Home/product">
        <el-icon><Promotion /></el-icon>
        <span>产品管理</span>
      </el-menu-item>
      <el-menu-item index="/Home/equipment">
        <el-icon><Monitor /></el-icon>
        <span>设备管理</span>
      </el-menu-item>
      <el-menu-item index="/Home/order">
        <el-icon><Tickets /></el-icon>
        <span>订单管理</span>
      </el-menu-item>
      <el-menu-item index="/Home/equipment-product">
        <el-icon><Link /></el-icon>
        <span>设备-产品关联</span>
      </el-menu-item>
      <el-menu-item index="/Home/plan">
        <el-icon><Calendar /></el-icon>
        <span>生产计划</span>
      </el-menu-item>
      <el-menu-item index="/Home/schedule">
        <el-icon><List /></el-icon>
        <span>生产调度</span>
      </el-menu-item>
      <el-menu-item index="/Home/daily-work">
        <el-icon><Finished /></el-icon>
        <span>日报工</span>
      </el-menu-item>
      <el-menu-item index="/Home/logs" v-if="isAdmin">
        <el-icon><Document /></el-icon>
        <span>日志查询</span>
      </el-menu-item>
      <el-menu-item index="/Home/users" v-if="isAdmin">
        <el-icon><UserFilled /></el-icon>
        <span>用户管理</span>
      </el-menu-item>
    </el-menu>
    <div class="theme-switch-area" v-show="!collapse">
      <div class="theme-switch" @click="toggleTheme">
        <div class="g-btn" :class="{ active: isDark }"></div>
      </div>
    </div>
    <div class="theme-switch-collapsed" v-show="collapse" @click="toggleTheme">
      <span class="mini-icon">{{ isDark ? '🌙' : '☀️' }}</span>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed, onMounted } from "vue";
import { useRoute } from "vue-router";
import { PieChart, Promotion, User, Monitor, Tickets, Link, Calendar, List, Finished, Document, UserFilled } from "@element-plus/icons-vue";
import emitter from "@/utils/emitter";

const route = useRoute();
const collapse = ref(false);
emitter.on("collapse", (msg) => {
  collapse.value = msg as boolean;
});

const isDark = ref(localStorage.getItem("theme") !== "light");

const menuBg = computed(() => isDark.value ? "transparent" : "#ffffff");
const menuTextColor = computed(() => isDark.value ? "#c8d0da" : "#333333");

const isAdmin = computed(() => Number(sessionStorage.getItem("roleId")) === 1);

const activeMenu = computed(() => route.path);

onMounted(() => {
  if (isDark.value) {
    document.body.classList.add("dark");
    if (!localStorage.getItem("theme")) {
      localStorage.setItem("theme", "dark");
    }
  }
});

function toggleTheme() {
  isDark.value = !isDark.value;
  if (isDark.value) {
    document.body.classList.add("dark");
    localStorage.setItem("theme", "dark");
  } else {
    document.body.classList.remove("dark");
    localStorage.setItem("theme", "light");
  }
  emitter.emit("themeChange", isDark.value);
}
</script>

<style scoped>
.sidebar {
  display: flex;
  flex-direction: column;
  position: absolute;
  left: 0;
  top: 60px;
  bottom: 0;
  overflow-y: auto;
  background: var(--bg-sidebar);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  z-index: 50;
}
.sidebar::-webkit-scrollbar { width: 0; }
.sidebar > ul { flex: 1; }
.sidebar-el-menu:not(.el-menu--collapse) { width: 150px; }

/* Menu item hover & active — tech purple accent */
.sidebar-el-menu :deep(.el-menu-item:hover) {
  background: var(--sidebar-menu-hover) !important;
}
.sidebar-el-menu :deep(.el-menu-item.is-active) {
  background: rgba(183, 77, 255, 0.08) !important;
  position: relative;
}
.sidebar-el-menu :deep(.el-menu-item.is-active::before) {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 20px;
  background: var(--accent-purple);
  border-radius: 0 3px 3px 0;
}

/* ====== 主题切换按钮区域 ====== */
.theme-switch-area {
  padding: 12px 10px;
  border-top: 1px solid var(--border-color);
  display: flex;
  justify-content: center;
  flex-shrink: 0;
}

/* 切换按钮容器 — 缩放适配侧边栏 */
.theme-switch {
  width: 110px;
  height: 45px;
  overflow: hidden;
  border-radius: 45px;
  cursor: pointer;
  flex-shrink: 0;
  background: transparent;
}

/* ── 日月渐变按钮样式 ── */
.g-btn {
  position: relative;
  width: 220px;
  height: 90px;
  transform: scale(0.5);
  transform-origin: left top;
  background:
    radial-gradient(circle at 18% 20px, #fff, #fff 6px, transparent 7px, transparent),
    radial-gradient(circle at 35% 45px, #fff, #fff 1px, transparent 2px, transparent),
    radial-gradient(circle at 10% 70px, #fff, #fff 2.5px, transparent 3.5px, transparent),
    radial-gradient(circle at 25% 15px, #fff, #fff 3px, transparent 4px, transparent),
    radial-gradient(circle at 15% 50px, #fff, #fff 1.5px, transparent 2.5px, transparent),
    radial-gradient(circle at 30% 75px, #fff, #fff 5px, transparent 6px, transparent),
    radial-gradient(circle at 5% 30px, #fff, #fff 0.5px, transparent 1.5px, transparent),
    radial-gradient(circle at 25% 60px, #fff, #fff 0.5px, transparent 1.5px, transparent),
    radial-gradient(circle at 7% 35px, #fff, #fff 0.5px, transparent 1.5px, transparent),
    linear-gradient(90deg, #2b303e, #2b303e 50%, #5a81b4 50%, #5a81b4);
  background-repeat: no-repeat;
  background-size: 200% 100%;
  background-position: 100% 0;
  border-radius: 90px;
  box-shadow:
    0 -3px 4px #999,
    inset 0 3px 5px #333,
    0 4px 4px #ffe,
    inset 0 -3px 5px #ddd;
  cursor: pointer;
  overflow: hidden;
  transition: 0.5s all;
}
.g-btn::before,
.g-btn::after {
  content: "";
  position: absolute;
  transition: 0.5s all;
}
.g-btn::before {
  width: 75px;
  height: 75px;
  border-radius: 50%;
  background: #e9cb50;
  inset: 7.5px;
  box-shadow:
    0 0 5px #333,
    inset 2px 2px 3px #f8f4e4,
    inset -2px -2px 3px #665613;
  z-index: 1;
}
.g-btn::after {
  width: 70px;
  height: 70px;
  inset: 10px;
  border-radius: 50%;
  box-shadow:
    10px 60px 0 10px #fff,
    65px 60px 0 5px #fff,
    95px 70px 0 10px #fff,
    135px 45px 0 5px #fff,
    170px 35px 0 10px #fff,
    195px -5px 0 10px #fff,
    -10px 0 0 50px rgba(255, 255, 255, .2),
    15px 0 0 50px rgba(255, 255, 255, .15),
    40px 0 0 50px rgba(255, 255, 255, .21),
    10px 40px 0 10px #abc1d9,
    70px 35px 0 10px #abc1d9,
    95px 40px 0 10px #abc1d9,
    135px 20px 0 10px #abc1d9,
    155px 15px 0 10px #abc1d9,
    190px -20px 0 10px #abc1d9;
}
.g-btn:hover::before {
  filter: contrast(90%) brightness(110%);
  scale: 1.05;
}

/* 夜间模式 */
.g-btn.active {
  background-position: 0 0;
}
.g-btn.active::before {
  translate: 130px;
  background:
    radial-gradient(circle at 50% 20px, #939aa5, #939aa5 6.5px, transparent 7px, transparent),
    radial-gradient(circle at 35% 45px, #939aa5, #939aa5 11.5px, transparent 12px, transparent),
    radial-gradient(circle at 72% 50px, #939aa5, #939aa5 8.5px, transparent 9px, transparent),
    radial-gradient(#cbcdda, #cbcdda);
}
.g-btn.active::after {
  transform: translate(130px);
  box-shadow:
    10px 60px 0 10px transparent,
    65px 60px 0 5px transparent,
    95px 70px 0 10px transparent,
    135px 45px 0 5px transparent,
    170px 35px 0 10px transparent,
    195px -5px 0 10px transparent,
    10px 0 0 50px rgba(255, 255, 255, .2),
    -15px 0 0 50px rgba(255, 255, 255, .15),
    -40px 0 0 50px rgba(255, 255, 255, .21),
    10px 40px 0 10px transparent,
    70px 35px 0 10px transparent,
    95px 40px 0 10px transparent,
    135px 20px 0 10px transparent,
    155px 15px 0 10px transparent,
    190px -20px 0 10px transparent;
}

/* 折叠时迷你图标 */
.theme-switch-collapsed {
  padding: 8px 0;
  display: flex;
  justify-content: center;
  cursor: pointer;
  flex-shrink: 0;
  border-top: 1px solid var(--border-color);
  line-height: 1;
}
.theme-switch-collapsed:hover {
  background: rgba(255,255,255,0.05);
}
.mini-icon {
  font-size: 20px;
  line-height: 1;
}
</style>
