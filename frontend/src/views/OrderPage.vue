<template>
  <div class="order-container">
    <div class="handle-box">
      <div class="handle-left">
        <el-button v-if="isAdmin" type="primary" @click="openAddDialog">添加</el-button>
        <el-button class="export-btn" size="small" @click="exportExcel">导出 Excel</el-button>
        <el-button v-if="isAdmin" class="import-btn" size="small" @click="triggerImport">导入 Excel</el-button>
        <input type="file" ref="importFileRef" accept=".xlsx" style="display:none" @change="importExcel" />
      </div>
      <div class="handle-right">
        <el-input v-model="searchText" placeholder="搜索订单编号" clearable prefix-icon="Search" class="search-input" />
      </div>
    </div>

    <el-table height="550px" border size="small" :data="filteredData" style="width:100%">
      <el-table-column label="ID" prop="id" width="60" align="center" sortable />
      <el-table-column label="订单编号" prop="orderSeq" min-width="130" align="center" show-overflow-tooltip sortable />
      <el-table-column label="产品ID" prop="productId" width="80" align="center" sortable />
      <el-table-column label="产品数量" prop="productCount" width="90" align="center" sortable />
      <el-table-column label="订单状态" width="100" align="center" sortable sort-by="orderStatus">
        <template v-slot="scope">
          <el-tag :type="statusType(scope.row.orderStatus)" size="small">
            {{ statusLabel(scope.row.orderStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="工厂ID" prop="factoryId" width="80" align="center" sortable />
      <el-table-column label="截止日期" prop="endDate" width="110" align="center" sortable />
      <el-table-column label="创建时间" prop="createTime" width="160" align="center" sortable />
      <el-table-column label="操作" min-width="240" align="center" v-if="isAdmin">
        <template v-slot="scope">
          <div class="action-btns">
            <template v-if="scope.row.orderStatus === 10">
              <el-button class="status-btn accept" size="small" @click="updateOrderStatus(scope.row.id, 20)">接单</el-button>
              <el-button class="status-btn reject" size="small" @click="updateOrderStatus(scope.row.id, 30)">拒绝</el-button>
            </template>
            <template v-else-if="scope.row.orderStatus === 20">
              <el-button class="status-btn produce" size="small" @click="updateOrderStatus(scope.row.id, 40)">开始生产</el-button>
            </template>
            <template v-else-if="scope.row.orderStatus === 40">
              <el-button class="status-btn complete" size="small" @click="updateOrderStatus(scope.row.id, 50)">完成</el-button>
            </template>
            <el-button class="edit-btn" size="small" @click="editRow(scope.row)">编辑</el-button>
            <el-button class="del-btn" size="small" @click="deleteRow(scope.row.id)">删除</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加对话框 -->
    <el-dialog title="添加订单" v-model="addVisible" width="480px">
      <el-form label-width="100px" :model="addForm">
        <el-form-item label="订单编号"><el-input v-model="addForm.orderSeq" /></el-form-item>
        <el-form-item label="产品ID"><el-input v-model="addForm.productId" /></el-form-item>
        <el-form-item label="产品数量"><el-input v-model="addForm.productCount" /></el-form-item>
        <el-form-item label="截止日期">
          <el-date-picker v-model="addForm.endDate" type="date" value-format="YYYY-MM-DD" style="width:100%" />
        </el-form-item>
        <el-form-item label="工厂ID"><el-input v-model="addForm.factoryId" /></el-form-item>
        <el-form-item label="工厂产能"><el-input v-model="addForm.factoryYield" /></el-form-item>
        <el-form-item label="订单状态">
          <el-select v-model="addForm.orderStatus">
            <el-option label="未接单" :value="10" />
            <el-option label="已接单" :value="20" />
            <el-option label="已拒绝" :value="30" />
            <el-option label="生产中" :value="40" />
            <el-option label="订单完成" :value="50" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addVisible = false">取消</el-button>
        <el-button type="primary" :loading="addLoading" @click="addOrder">确定</el-button>
      </template>
    </el-dialog>

    <!-- 编辑对话框 -->
    <el-dialog title="编辑订单" v-model="editVisible" width="480px">
      <el-form label-width="100px" :model="editForm">
        <el-form-item label="订单编号"><el-input v-model="editForm.orderSeq" /></el-form-item>
        <el-form-item label="产品ID"><el-input v-model="editForm.productId" /></el-form-item>
        <el-form-item label="产品数量"><el-input v-model="editForm.productCount" /></el-form-item>
        <el-form-item label="截止日期">
          <el-date-picker v-model="editForm.endDate" type="date" value-format="YYYY-MM-DD" style="width:100%" />
        </el-form-item>
        <el-form-item label="工厂ID"><el-input v-model="editForm.factoryId" /></el-form-item>
        <el-form-item label="工厂产能"><el-input v-model="editForm.factoryYield" /></el-form-item>
        <el-form-item label="订单状态">
          <el-select v-model="editForm.orderStatus">
            <el-option label="未接单" :value="10" />
            <el-option label="已接单" :value="20" />
            <el-option label="已拒绝" :value="30" />
            <el-option label="生产中" :value="40" />
            <el-option label="订单完成" :value="50" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" :loading="editLoading" @click="saveEdit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 删除确认 -->
    <el-dialog title="确认删除" v-model="delVisible" width="360px" top="30vh">
      <p>确定要删除该订单吗？</p>
      <template #footer>
        <el-button @click="delVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmDelete">删除</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, reactive, computed, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { Search } from "@element-plus/icons-vue";
import { HttpManager } from "@/api";

export default defineComponent({
  components: { Search },
  setup() {
    const tableData = ref<any[]>([]);
    const isAdmin = Number(sessionStorage.getItem("roleId")) === 1;
    const searchText = ref("");
    const importFileRef = ref<HTMLInputElement | null>(null);

    // ---- Excel 导入导出 ----
    async function exportExcel() {
      try { await HttpManager.exportOrderExcel(); ElMessage.success('导出成功'); }
      catch { ElMessage.error('导出失败'); }
    }
    function triggerImport() { importFileRef.value?.click(); }
    async function importExcel(e: Event) {
      const input = e.target as HTMLInputElement;
      const file = input.files?.[0];
      if (!file) return;
      try {
        const res: any = await HttpManager.importOrderExcel(file);
        if (res.success) { ElMessage.success(res.message || '导入成功'); getData(); }
        else { ElMessage.error(res.message || '导入失败'); }
      } catch { ElMessage.error('导入失败'); }
      input.value = '';
    }

    const filteredData = computed(() => {
      if (!searchText.value.trim()) return tableData.value;
      const q = searchText.value.trim().toLowerCase();
      return tableData.value.filter((r: any) => (r.orderSeq || '').toLowerCase().includes(q));
    });

    function statusType(status: number): string {
      if (status === 10) return 'info';
      if (status === 20) return 'primary';
      if (status === 30) return 'danger';
      if (status === 40) return 'warning';
      if (status === 50) return 'success';
      return 'info';
    }

    function statusLabel(status: number): string {
      if (status === 10) return '未接单';
      if (status === 20) return '已接单';
      if (status === 30) return '已拒绝';
      if (status === 40) return '生产中';
      if (status === 50) return '订单完成';
      return '未知';
    }

    // ---- 添加 ----
    const addVisible = ref(false);
    const addLoading = ref(false);
    const addForm = reactive({
      orderSeq: "", productId: "", productCount: "", endDate: "",
      factoryId: "", factoryYield: "", orderStatus: 10
    });

    function openAddDialog() {
      addForm.orderSeq = "";
      addForm.productId = "";
      addForm.productCount = "";
      addForm.endDate = "";
      addForm.factoryId = "";
      addForm.factoryYield = "";
      addForm.orderStatus = 10;
      addVisible.value = true;
    }

    async function addOrder() {
      addLoading.value = true;
      try {
        const formData = {
          ...addForm,
          productId: addForm.productId ? Number(addForm.productId) : undefined,
          productCount: addForm.productCount ? Number(addForm.productCount) : undefined,
          factoryId: addForm.factoryId ? Number(addForm.factoryId) : undefined,
          factoryYield: addForm.factoryYield ? Number(addForm.factoryYield) : undefined,
        };
        const res: any = await HttpManager.addOrder(formData);
        if (res.success) {
          ElMessage.success('添加成功');
          addVisible.value = false;
          getData();
        } else {
          ElMessage.error(res.message || '添加失败');
        }
      } catch {
        ElMessage.error('添加失败');
      } finally {
        addLoading.value = false;
      }
    }

    // ---- 编辑 ----
    const editVisible = ref(false);
    const editLoading = ref(false);
    const editForm = reactive({
      id: "", orderSeq: "", productId: "", productCount: "", endDate: "",
      factoryId: "", factoryYield: "", orderStatus: 10
    });

    function editRow(row: any) {
      editForm.id = row.id;
      editForm.orderSeq = row.orderSeq;
      editForm.productId = String(row.productId);
      editForm.productCount = String(row.productCount);
      editForm.endDate = row.endDate;
      editForm.factoryId = String(row.factoryId);
      editForm.factoryYield = String(row.factoryYield || "");
      editForm.orderStatus = row.orderStatus;
      editVisible.value = true;
    }

    async function saveEdit() {
      editLoading.value = true;
      try {
        const formData = {
          ...editForm,
          id: Number(editForm.id),
          productId: editForm.productId ? Number(editForm.productId) : undefined,
          productCount: editForm.productCount ? Number(editForm.productCount) : undefined,
          factoryId: editForm.factoryId ? Number(editForm.factoryId) : undefined,
          factoryYield: editForm.factoryYield ? Number(editForm.factoryYield) : undefined,
        };
        const res: any = await HttpManager.updateOrder(formData);
        if (res.success) {
          ElMessage.success('修改成功');
          editVisible.value = false;
          getData();
        } else {
          ElMessage.error(res.message || '修改失败');
        }
      } catch {
        ElMessage.error('修改失败');
      } finally {
        editLoading.value = false;
      }
    }

    // ---- 删除 ----
    const delVisible = ref(false);
    const deleteId = ref<number | null>(null);

    function deleteRow(id: number) { deleteId.value = id; delVisible.value = true; }

    async function confirmDelete() {
      if (deleteId.value == null) return;
      try {
        const res: any = await HttpManager.deleteOrder(deleteId.value);
        if (res.success) { ElMessage.success('删除成功'); getData(); }
        else { ElMessage.error(res.message || '删除失败'); }
      } catch { ElMessage.error('删除失败'); }
      delVisible.value = false;
    }

    // ---- 数据加载 ----
    onMounted(() => getData());

    async function updateOrderStatus(id: number, status: number) {
      try {
        const res: any = await HttpManager.updateOrderStatus(id, status);
        if (res.success) {
          ElMessage.success('状态更新成功');
          getData();
        } else {
          ElMessage.error(res.message || '状态更新失败');
        }
      } catch {
        ElMessage.error('状态更新失败');
      }
    }

    async function getData() {
      try {
        const res: any = await HttpManager.getOrderList();
        if (res.success) {
          tableData.value = res.data?.items || res.data || [];
        }
      } catch { ElMessage.error('获取订单数据失败'); }
    }

    return {
      tableData, isAdmin, searchText, filteredData,
      importFileRef, exportExcel, triggerImport, importExcel,
      statusType, statusLabel, updateOrderStatus,
      addVisible, addLoading, addForm, openAddDialog, addOrder,
      editVisible, editLoading, editForm, editRow, saveEdit,
      delVisible, deleteRow, confirmDelete,
    };
  }
});
</script>

<style scoped>
.order-container {
  padding: 20px;
  background: var(--bg-page);
  min-height: calc(100vh - 60px);
}

.handle-box { margin-bottom: 20px; display: flex; justify-content: space-between; align-items: center; }
.handle-left { display: flex; gap: 8px; }
.handle-right { display: flex; gap: 8px; }
.search-input { width: 220px; }
.search-input :deep(.el-input__wrapper) { background: var(--bg-card); }
.export-btn { background: #67c23a !important; color: #fff !important; border: none !important; }
.export-btn:hover { background: #85ce61 !important; }
.import-btn { background: #fff !important; color: #606266 !important; border: 1px solid #dcdfe6 !important; }
.import-btn:hover { color: #b74dff !important; border-color: rgba(183, 77, 255, 0.25) !important; background: rgba(183, 77, 255, 0.06) !important; }
body.dark .import-btn { background: #2a2a4a !important; color: #c8d0da !important; border-color: #3a3a5a !important; }
body.dark .import-btn:hover { background: rgba(183, 77, 255, 0.12) !important; border-color: #b74dff !important; color: #b74dff !important; }
.handle-box :deep(.el-button--primary) {
  background: #b74dff !important;
  border-color: #b74dff !important;
}
.handle-box :deep(.el-button--primary:hover) {
  background: #c87aff !important;
  border-color: #c87aff !important;
}
:deep(.el-dialog .el-button--primary) {
  background: #b74dff !important;
  border-color: #b74dff !important;
}
:deep(.el-dialog .el-button--primary:hover) {
  background: #c87aff !important;
  border-color: #c87aff !important;
}

.action-btns {
  display: flex;
  gap: 6px;
  justify-content: center;
  align-items: center;
}
.edit-btn {
  background: #fff !important;
  color: #606266 !important;
  border: 1px solid #dcdfe6 !important;
  padding: 5px 10px !important;
  font-size: 12px !important;
  min-height: 28px !important;
}
.edit-btn:hover {
  color: #b74dff !important;
  border-color: rgba(183, 77, 255, 0.25) !important;
  background: rgba(183, 77, 255, 0.06) !important;
}
.del-btn {
  background: #f56c6c !important;
  color: #fff !important;
  border: none !important;
  padding: 5px 10px !important;
  font-size: 12px !important;
  min-height: 28px !important;
}
.del-btn:hover {
  background: #f78989 !important;
}

/* Status buttons */
.status-btn { padding: 4px 8px !important; font-size: 11px !important; min-height: 26px !important; border-radius: 4px !important; border: none !important; color: #fff !important; }
.status-btn.accept { background: #67c23a !important; }
.status-btn.accept:hover { background: #85ce61 !important; }
.status-btn.reject { background: #909399 !important; }
.status-btn.reject:hover { background: #a6a9ad !important; }
.status-btn.produce { background: #e6a23c !important; }
.status-btn.produce:hover { background: #ebb563 !important; }
.status-btn.complete { background: #409eff !important; }
.status-btn.complete:hover { background: #66b1ff !important; }

/* Table dark mode */
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
body.dark .edit-btn {
  background: #2a2a4a !important;
  color: #c8d0da !important;
  border-color: #3a3a5a !important;
}
body.dark .edit-btn:hover {
  background: rgba(183, 77, 255, 0.12) !important;
  border-color: #b74dff !important;
  color: #b74dff !important;
}
</style>
