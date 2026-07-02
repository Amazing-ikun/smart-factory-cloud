<template>
  <div class="log-container">
    <div class="handle-box">
      <span class="page-desc">系统操作日志（仅管理员可见）</span>
    </div>

    <el-table height="550px" border size="small" :data="tableData" style="width:100%">
      <el-table-column label="ID" prop="id" width="60" align="center" sortable />
      <el-table-column label="用户" min-width="120" align="center">
        <template v-slot="scope">
          <span>{{ scope.row.userName }}</span>
          <span v-if="scope.row.userId === 1" class="admin-tag">管理员</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" prop="operation" width="80" align="center" sortable>
        <template v-slot="scope">
          <el-tag :type="operationType(scope.row.operation)" size="small">
            {{ scope.row.operation }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="模块" prop="module" width="100" align="center" sortable />
      <el-table-column label="详情" prop="detail" min-width="300" align="left" show-overflow-tooltip />
      <el-table-column label="操作时间" prop="createTime" width="170" align="center" sortable />
    </el-table>

    <div class="pagination-wrap">
      <el-pagination
        background
        layout="total, prev, pager, next, sizes"
        :total="total"
        :page-size="pageSize"
        :current-page="page"
        :page-sizes="[20, 50, 100]"
        @current-change="onPageChange"
        @size-change="onSizeChange"
      />
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
    const pageSize = ref(50);

    function operationType(op: string): string {
      if (op === '查询') return 'info';
      if (op === '新增') return 'success';
      if (op === '修改' || op === '重置密码') return 'warning';
      if (op === '删除') return 'danger';
      if (op === '登录') return 'primary';
      return 'info';
    }

    async function getData() {
      try {
        const res: any = await HttpManager.getLogList(page.value, pageSize.value);
        if (res.success) {
          tableData.value = res.data?.items || [];
          total.value = res.data?.total || 0;
        } else {
          ElMessage.error(res.message || '获取日志失败');
        }
      } catch {
        ElMessage.error('获取日志数据失败');
      }
    }

    function onPageChange(p: number) {
      page.value = p;
      getData();
    }

    function onSizeChange(s: number) {
      pageSize.value = s;
      page.value = 1;
      getData();
    }

    onMounted(() => getData());

    return {
      tableData, total, page, pageSize,
      operationType,
      onPageChange, onSizeChange,
    };
  }
});
</script>

<style scoped>
.log-container {
  padding: 20px;
  background: var(--bg-page);
  min-height: calc(100vh - 60px);
}
.handle-box {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.page-desc {
  font-size: 14px;
  color: var(--text-secondary);
}
.pagination-wrap {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
.pagination-wrap :deep(.el-pager li.is-active) {
  background: #b74dff !important;
  border-color: #b74dff !important;
}
.admin-tag {
  display: inline-block;
  font-size: 10px;
  background: #f56c6c;
  color: #fff;
  border-radius: 3px;
  padding: 1px 5px;
  margin-left: 4px;
  vertical-align: middle;
}

:deep(.el-table) {
  --el-table-bg-color: var(--bg-card);
  --el-table-header-bg-color: var(--bg-card);
  --el-table-tr-bg-color: var(--bg-card);
  --el-table-border-color: var(--border-color);
  --el-table-row-hover-bg-color: var(--table-row-hover-bg);
}
:deep(.el-table__body-wrapper),
:deep(.el-table__body),
:deep(.el-table__empty-block),
:deep(.el-table__inner-wrapper),
:deep(.el-table .el-table__body td.el-table__cell) {
  background: var(--bg-card);
}
</style>
