<template>
  <div class="dw-container">
    <div class="handle-box">
      <span class="page-title">日报工记录</span>
    </div>

    <el-table height="550px" border size="small" :data="tableData" style="width:100%">
      <el-table-column label="ID" prop="id" width="60" align="center" sortable />
      <el-table-column label="调度ID" prop="scheduleId" width="80" align="center" sortable />
      <el-table-column label="设备ID" prop="equipmentId" width="80" align="center" sortable />
      <el-table-column label="设备编号" prop="equipmentSeq" width="120" align="center" show-overflow-tooltip sortable />
      <el-table-column label="加工数量" prop="workingCount" width="90" align="center" sortable />
      <el-table-column label="合格数量" prop="qualifiedCount" width="90" align="center" sortable />
      <el-table-column label="不合格数量" prop="unqualifiedCount" width="90" align="center" sortable />
      <el-table-column label="完成标识" width="80" align="center" sortable sort-by="completeFlag">
        <template v-slot="scope">
          <el-tag :type="scope.row.completeFlag === 0 ? 'success' : 'info'" size="small">{{ scope.row.completeFlag === 0 ? '已完成' : '未完成' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="工厂ID" prop="factoryId" width="80" align="center" sortable />
      <el-table-column label="开始时间" prop="startTime" width="170" align="center" sortable />
      <el-table-column label="结束时间" prop="endTime" width="170" align="center" sortable />
      <el-table-column label="备注" prop="bak" min-width="120" align="left" show-overflow-tooltip />
    </el-table>

    <div class="pagination-wrap">
      <el-pagination background layout="total, prev, pager, next, sizes" :total="total" :page-size="pageSize" :current-page="page" :page-sizes="[10, 20, 50]" @current-change="onPageChange" @size-change="onSizeChange" />
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { HttpManager } from "@/api";

export default defineComponent({
  setup() {
    const tableData = ref([]);
    const total = ref(0);
    const page = ref(1);
    const pageSize = ref(10);

    async function getData() {
      try {
        const res: any = await HttpManager.getDailyWorkList(page.value, pageSize.value);
        if (res.success) {
          tableData.value = res.data?.items || [];
          total.value = res.data?.total || 0;
        }
      } catch { /* ignore */ }
    }

    function onPageChange(p: number) { page.value = p; getData(); }
    function onSizeChange(s: number) { pageSize.value = s; page.value = 1; getData(); }

    onMounted(() => getData());

    return { tableData, total, page, pageSize, onPageChange, onSizeChange };
  }
});
</script>

<style scoped>
.dw-container { padding: 20px; background: var(--bg-page); min-height: calc(100vh - 60px); }
.handle-box { margin-bottom: 20px; }
.page-title { font-size: 16px; font-weight: 600; color: var(--text-primary); }
.pagination-wrap { margin-top: 16px; display: flex; justify-content: flex-end; }
.pagination-wrap :deep(.el-pager li.is-active) { background: #b74dff !important; border-color: #b74dff !important; }
:deep(.el-table) { --el-table-bg-color: var(--bg-card); --el-table-header-bg-color: var(--bg-card); --el-table-tr-bg-color: var(--bg-card); --el-table-border-color: var(--border-color); --el-table-row-hover-bg-color: var(--table-row-hover-bg); }
:deep(.el-table__body-wrapper), :deep(.el-table__body), :deep(.el-table__empty-block), :deep(.el-table__inner-wrapper), :deep(.el-table .el-table__body td.el-table__cell) { background: var(--bg-card); }
</style>
