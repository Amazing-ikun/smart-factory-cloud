<template>
  <div class="dashboard-container">
    <!-- ====== 顶部标题栏 ====== -->
    <div class="header">
      <div class="header-left">
        <div class="header-logo">&#9670;</div>
        <span class="header-title">智能制造</span>
        <span class="header-subtitle">数据看板</span>
      </div>
      <div class="header-right">
        <div class="user-info" @click="goToProfile">
          <span id="displayUsername">{{ currentUser.userRealName || currentUser.userName || '管理员' }}</span>
        </div>
        <el-button class="logout-btn" @click="handleLogout">退出登录</el-button>
      </div>
    </div>

    <!-- ====== 统计卡片 ====== -->
    <div class="row">
      <div class="col" v-for="(stat, index) in statistics" :key="index">
        <div class="stat-card">
          <div class="card-content">
            <div class="card-left"><span class="icon">{{ stat.icon }}</span></div>
            <div class="card-right">
              <div class="card-label">{{ stat.label }}</div>
              <div class="card-num">{{ stat.value }}<span v-if="stat.showSuffix">%</span></div>
              <div class="card-trend" :class="{ down: stat.trendDown }">{{ stat.trend }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- ====== 图表区域 ====== -->
    <div class="chart-row">
      <div class="chart-col">
        <div class="chart-box">
          <div class="chart-header">
            <h3>&#128200; 工厂订单信息</h3>
            <span class="chart-tag">今年</span>
          </div>
          <div class="chart-container" id="orderChart"></div>
        </div>
      </div>
      <div class="chart-col">
        <div class="chart-box">
          <div class="chart-header">
            <h3>&#128202; 订单分类统计</h3>
            <span class="chart-tag">按类型</span>
          </div>
          <div class="chart-container" id="categoryChart"></div>
        </div>
      </div>
    </div>

    <!-- ====== 页脚 ====== -->
    <div class="footer">
      &copy; 2026 智能制造云工厂管理平台 · 数据实时更新
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted, onBeforeUnmount, nextTick } from "vue";
import { useRouter } from 'vue-router';
import * as echarts from "echarts";
import { ElMessage, ElMessageBox } from 'element-plus';
import { HttpManager } from "@/api";
import emitter from "@/utils/emitter";

const router = useRouter();
const currentUser = ref({ userId: '', userName: '', userRealName: '' });

const statistics = reactive([
  { label: '开机率', value: '0.0', showSuffix: true, icon: '📊', trend: '↑ 2.3% 较昨日', trendDown: false },
  { label: '故障率', value: '0.0', showSuffix: true, icon: '⚠️', trend: '↓ 0.8% 较昨日', trendDown: true },
  { label: '运行率', value: '0.0', showSuffix: true, icon: '🚀', trend: '↑ 1.5% 较昨日', trendDown: false },
  { label: '综合率', value: '0.0', showSuffix: true, icon: '🏆', trend: '↑ 0.6% 较昨日', trendDown: false },
]);

let orderChart: echarts.ECharts | null = null;
let categoryChart: echarts.ECharts | null = null;

const barChartSource = ref<Record<string, number>>({});
const pieChartSource = ref<{name: string; value: number}[]>([]);

function getChartColors() {
  const dark = document.body.classList.contains('dark');
  return {
    text: dark ? '#e0e6ed' : '#1a3a5c',
    axis: dark ? '#3a3a5a' : '#e8e8e8',
    split: dark ? '#2a2a4a' : '#e8e8e8',
    cardBg: dark ? '#151528' : '#ffffff',
  };
}

function checkLoginStatus(): boolean {
  const userId = sessionStorage.getItem('userId');
  if (!userId) { router.push({ path: '/' }); return false; }
  currentUser.value.userId = userId;
  currentUser.value.userName = sessionStorage.getItem('username') || '';
  currentUser.value.userRealName = sessionStorage.getItem('userRealName') || '';
  return true;
}

function handleLogout() {
  ElMessageBox.confirm('确认退出登录吗？', '退出确认', {
    confirmButtonText: '确认退出', cancelButtonText: '取消', type: 'warning'
  }).then(() => { sessionStorage.clear(); router.push({ path: '/' }); }).catch(() => {});
}

function goToProfile() {
  router.push('/Home/profile');
}

function initCharts() {
  const colors = getChartColors();
  const barKeys = Object.keys(barChartSource.value);
  const barVals = Object.values(barChartSource.value);
  const pieColors = ['#b74dff', '#60a5fa', '#f472b6', '#f59e0b', '#10b981'];

  const orderChartDom = document.getElementById('orderChart');
  if (orderChartDom) {
    orderChart = echarts.init(orderChartDom);
    orderChart.setOption({
      tooltip: { trigger: 'axis' },
      grid: { left: '8%', right: '5%', top: 48, bottom: 36 },
      xAxis: {
        type: 'category',
        data: barKeys.length > 0 ? barKeys : ['暂无数据'],
        axisLabel: { color: colors.text },
        axisLine: { lineStyle: { color: colors.axis } },
      },
      yAxis: {
        type: 'value',
        name: '订单数量',
        nameTextStyle: { color: colors.text },
        axisLabel: { color: colors.text },
        axisLine: { lineStyle: { color: colors.axis } },
        splitLine: { lineStyle: { color: colors.split } },
      },
      series: [{ data: barVals.length > 0 ? barVals : [0], type: 'bar', itemStyle: { color: '#b74dff' } }]
    });
  }
  const categoryChartDom = document.getElementById('categoryChart');
  if (categoryChartDom) {
    categoryChart = echarts.init(categoryChartDom);
    const pieData = pieChartSource.value.length > 0
      ? pieChartSource.value.map((item, i) => ({ ...item, itemStyle: { color: pieColors[i % pieColors.length] } }))
      : [{ value: 1, name: '暂无数据', itemStyle: { color: '#ddd' } }];
    categoryChart.setOption({
      tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
      legend: { orient: 'vertical', right: '5%', top: 'center', textStyle: { color: colors.text } },
      series: [{
        type: 'pie', radius: ['45%','70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: colors.cardBg,
          borderWidth: 2
        },
        label: { show: false, position: 'center' },
        emphasis: {
          label: { show: true, fontSize: 28, fontWeight: 'bold', color: colors.text }
        },
        labelLine: { show: false },
        data: pieData,
      }]
    });
  }
  const handleResize = () => { orderChart?.resize(); categoryChart?.resize(); };
  window.addEventListener('resize', handleResize);
  return () => window.removeEventListener('resize', handleResize);
}

function updateChartTheme() {
  const colors = getChartColors();
  orderChart?.setOption({
    xAxis: {
      axisLabel: { color: colors.text },
      axisLine: { lineStyle: { color: colors.axis } },
    },
    yAxis: {
      nameTextStyle: { color: colors.text },
      axisLabel: { color: colors.text },
      axisLine: { lineStyle: { color: colors.axis } },
      splitLine: { lineStyle: { color: colors.split } },
    },
  });
  const updatedColors = getChartColors();
  categoryChart?.setOption({
    series: [{
      itemStyle: { borderColor: updatedColors.cardBg },
      emphasis: {
        label: { show: true, fontSize: 28, fontWeight: 'bold', color: updatedColors.text }
      }
    }]
  });
}

async function loadStats() {
  try {
    const res: any = await HttpManager.getDashboardStats();
    if (res.success && res.data) {
      statistics[0].value = res.data.uptimeRate.toFixed(1);
      statistics[1].value = res.data.failureRate.toFixed(1);
      statistics[2].value = res.data.operationRate.toFixed(1);
      statistics[3].value = res.data.compositeRate.toFixed(1);
      if (res.data.barChartData) barChartSource.value = res.data.barChartData;
      if (res.data.pieChartData) pieChartSource.value = res.data.pieChartData;
    }
  } catch {
    // 加载失败时保持默认值
  }
}

emitter.on("themeChange", () => {
  if (orderChart && categoryChart) {
    updateChartTheme();
  }
});

onMounted(async () => {
  if (!checkLoginStatus()) return;
  await loadStats();
  await nextTick();
  const cleanup = initCharts();
  onBeforeUnmount(() => { cleanup(); orderChart?.dispose(); categoryChart?.dispose(); });
});
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.dashboard-container {
  padding: 20px;
  background: var(--bg-page);
  min-height: 100vh;
}

/* ========== 顶部标题栏 ========== */
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(135deg, #1a1a3a 0%, #2d1a5c 50%, #4a1a6a 100%);
  padding: 16px 30px;
  border-radius: 12px;
  margin-bottom: 24px;
  color: #fff;
  box-shadow: 0 4px 24px rgba(183, 77, 255, 0.15);
  position: relative;
  overflow: hidden;
}
.header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(183, 77, 255, 0.5), transparent);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-logo {
  width: 40px;
  height: 40px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  color: #b74dff;
}

.header-title {
  font-size: 22px;
  font-weight: 600;
  letter-spacing: 2px;
}

.header-subtitle {
  font-size: 14px;
  opacity: 0.8;
  margin-left: 16px;
  padding-left: 16px;
  border-left: 1px solid rgba(255, 255, 255, 0.2);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 14px;
}

.header-right .user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  background: rgba(255, 255, 255, 0.08);
  padding: 6px 16px 6px 12px;
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.06);
  cursor: pointer;
  transition: background 0.2s;
}
.header-right .user-info:hover {
  background: rgba(183, 77, 255, 0.2);
}

.header-right .user-avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
}

:deep(.logout-btn) {
  background: rgba(255, 255, 255, 0.06) !important;
  border: 1px solid rgba(255, 255, 255, 0.12) !important;
  color: #fff !important;
  padding: 6px 18px !important;
  border-radius: 20px !important;
  cursor: pointer;
  font-size: 13px;
  transition: all 0.3s;
  height: auto !important;
  line-height: normal !important;
}

:deep(.logout-btn:hover) {
  background: rgba(183, 77, 255, 0.2) !important;
  border-color: rgba(183, 77, 255, 0.3) !important;
}

/* ========== 统计卡片 ========== */
.row {
  display: flex;
  flex-wrap: wrap;
  margin: 0 -10px 20px -10px;
}

.col {
  flex: 0 0 25%;
  max-width: 25%;
  padding: 0 10px;
  box-sizing: border-box;
}

@media (max-width: 992px) {
  .col { flex: 0 0 50%; max-width: 50%; margin-bottom: 16px; }
}

@media (max-width: 576px) {
  .col { flex: 0 0 100%; max-width: 100%; }
}

.stat-card {
  background: var(--bg-card);
  border-radius: 12px;
  box-shadow: var(--shadow-card);
  overflow: hidden;
  transition: transform 0.3s, box-shadow 0.3s;
  height: 100%;
  cursor: default;
  position: relative;
}

.stat-card::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  opacity: 0.5;
}

.col:nth-child(1) .stat-card::after { background: linear-gradient(90deg, #667eea, #764ba2); }
.col:nth-child(2) .stat-card::after { background: linear-gradient(90deg, #f093fb, #f5576c); }
.col:nth-child(3) .stat-card::after { background: linear-gradient(90deg, #4facfe, #00f2fe); }
.col:nth-child(4) .stat-card::after { background: linear-gradient(90deg, #43e97b, #38f9d7); }

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 30px rgba(183, 77, 255, 0.12);
}

.stat-card .card-content {
  display: flex;
  align-items: center;
  height: 110px;
  padding: 20px 24px;
}

.stat-card .card-left {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 56px;
  height: 56px;
  border-radius: 12px;
  font-size: 28px;
  color: #fff;
  flex-shrink: 0;
}

.stat-card .card-left .icon { font-size: 28px; }

.col:nth-child(1) .card-left { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.col:nth-child(2) .card-left { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }
.col:nth-child(3) .card-left { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }
.col:nth-child(4) .card-left { background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); }

.stat-card .card-right {
  flex: 1;
  padding-left: 16px;
}

.stat-card .card-label {
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 4px;
}

.stat-card .card-num {
  font-size: 32px;
  font-weight: 700;
  color: var(--text-primary);
  line-height: 1.2;
}

.stat-card .card-trend {
  font-size: 12px;
  color: #52c41a;
  margin-top: 2px;
}

.stat-card .card-trend.down { color: #ff4d4f; }

/* ========== 图表区域 ========== */
.chart-row {
  display: flex;
  flex-wrap: wrap;
  margin: 0 -10px;
}

.chart-col {
  flex: 0 0 50%;
  max-width: 50%;
  padding: 0 10px;
  box-sizing: border-box;
}

@media (max-width: 768px) {
  .chart-col { flex: 0 0 100%; max-width: 100%; margin-bottom: 16px; }
}

.chart-box {
  background: var(--bg-card);
  border-radius: 12px;
  box-shadow: var(--shadow-card);
  overflow: hidden;
  position: relative;
}
.chart-box::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, #60a5fa, #b74dff, #f472b6);
  opacity: 0.3;
}

.chart-box .chart-header {
  padding: 16px 24px 0 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-box .chart-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
}

.chart-box .chart-header .chart-tag {
  font-size: 12px;
  color: var(--chart-tag-text);
  background: var(--chart-tag-bg);
  padding: 2px 12px;
  border-radius: 12px;
}

.chart-box .chart-container {
  width: 100%;
  height: 280px;
  padding: 8px 4px 4px 4px;
}

/* ========== 页脚 ========== */
.footer {
  text-align: center;
  padding: 24px 0 8px 0;
  font-size: 13px;
  color: var(--text-secondary);
  border-top: 1px solid var(--border-color);
  margin-top: 8px;
}
</style>
