<template>
  <div class="plan-container">
    <div class="handle-box" v-if="isAdmin">
      <el-button type="primary" @click="openAddDialog">添加计划</el-button>
    </div>

    <el-table height="550px" border size="small" :data="tableData" style="width:100%">
      <el-table-column label="ID" prop="id" width="60" align="center" sortable />
      <el-table-column label="计划编号" prop="planSeq" min-width="120" align="center" show-overflow-tooltip sortable />
      <el-table-column label="订单ID" prop="orderId" width="80" align="center" sortable />
      <el-table-column label="产品ID" prop="productId" width="80" align="center" sortable />
      <el-table-column label="计划数量" prop="planCount" width="90" align="center" sortable />
      <el-table-column label="计划状态" width="100" align="center" sortable sort-by="planStatus">
        <template v-slot="scope">
          <el-tag :type="planStatusType(scope.row.planStatus)" size="small">{{ planStatusLabel(scope.row.planStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="开始日期" prop="planStartDate" width="110" align="center" sortable />
      <el-table-column label="结束日期" prop="planEndDate" width="110" align="center" sortable />
      <el-table-column label="工厂ID" prop="factoryId" width="80" align="center" sortable />
      <el-table-column label="创建时间" prop="createTime" width="160" align="center" sortable />
      <el-table-column label="操作" min-width="130" align="center" v-if="isAdmin">
        <template v-slot="scope">
          <div class="action-btns">
            <el-button class="edit-btn" size="small" @click="editRow(scope.row)">编辑</el-button>
            <el-button class="del-btn" size="small" @click="deleteRow(scope.row.id)">删除</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加对话框 -->
    <el-dialog title="添加计划" v-model="addVisible" width="480px">
      <el-form label-width="100px" :model="addForm">
        <el-form-item label="计划编号"><el-input v-model="addForm.planSeq" /></el-form-item>
        <el-form-item label="订单ID"><el-input v-model="addForm.orderId" /></el-form-item>
        <el-form-item label="产品ID"><el-input v-model="addForm.productId" /></el-form-item>
        <el-form-item label="计划数量"><el-input v-model="addForm.planCount" /></el-form-item>
        <el-form-item label="交货日期"><el-date-picker v-model="addForm.deliveryDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
        <el-form-item label="开始日期"><el-date-picker v-model="addForm.planStartDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
        <el-form-item label="结束日期"><el-date-picker v-model="addForm.planEndDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
        <el-form-item label="计划状态">
          <el-select v-model="addForm.planStatus">
            <el-option label="未启动" :value="10" />
            <el-option label="已启动" :value="20" />
            <el-option label="已完成" :value="30" />
          </el-select>
        </el-form-item>
        <el-form-item label="工厂ID"><el-input v-model="addForm.factoryId" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addVisible = false">取消</el-button>
        <el-button type="primary" :loading="addLoading" @click="addPlan">确定</el-button>
      </template>
    </el-dialog>

    <!-- 编辑对话框 -->
    <el-dialog title="编辑计划" v-model="editVisible" width="480px">
      <el-form label-width="100px" :model="editForm">
        <el-form-item label="计划编号"><el-input v-model="editForm.planSeq" /></el-form-item>
        <el-form-item label="订单ID"><el-input v-model="editForm.orderId" /></el-form-item>
        <el-form-item label="产品ID"><el-input v-model="editForm.productId" /></el-form-item>
        <el-form-item label="计划数量"><el-input v-model="editForm.planCount" /></el-form-item>
        <el-form-item label="交货日期"><el-date-picker v-model="editForm.deliveryDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
        <el-form-item label="开始日期"><el-date-picker v-model="editForm.planStartDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
        <el-form-item label="结束日期"><el-date-picker v-model="editForm.planEndDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
        <el-form-item label="计划状态">
          <el-select v-model="editForm.planStatus">
            <el-option label="未启动" :value="10" />
            <el-option label="已启动" :value="20" />
            <el-option label="已完成" :value="30" />
          </el-select>
        </el-form-item>
        <el-form-item label="工厂ID"><el-input v-model="editForm.factoryId" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" :loading="editLoading" @click="saveEdit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 删除确认 -->
    <el-dialog title="确认删除" v-model="delVisible" width="360px" top="30vh">
      <p>确定要删除该计划吗？</p>
      <template #footer>
        <el-button @click="delVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmDelete">删除</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, reactive, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { HttpManager } from "@/api";

export default defineComponent({
  setup() {
    const tableData = ref([]);
    const isAdmin = Number(sessionStorage.getItem("roleId")) === 1;
    const addVisible = ref(false); const addLoading = ref(false);
    const addForm = reactive({ planSeq: "", orderId: "", productId: "", planCount: "", deliveryDate: "", planStartDate: "", planEndDate: "", planStatus: 10, factoryId: "" });
    const editVisible = ref(false); const editLoading = ref(false);
    const editForm = reactive<any>({});
    const delVisible = ref(false); const deleteId = ref<number | null>(null);

    function planStatusType(s: number): string { if (s === 10) return 'info'; if (s === 20) return 'warning'; if (s === 30) return 'success'; return 'info'; }
    function planStatusLabel(s: number): string { if (s === 10) return '未启动'; if (s === 20) return '已启动'; if (s === 30) return '已完成'; return '未知'; }

    function openAddDialog() { Object.keys(addForm).forEach(k => (addForm as any)[k] = ""); addForm.planStatus = 10; addVisible.value = true; }
    function toNum(v: any) { return v !== "" && v !== null && v !== undefined ? Number(v) : undefined; }

    async function addPlan() {
      addLoading.value = true;
      try {
        const res: any = await HttpManager.addPlan({ ...addForm, orderId: toNum(addForm.orderId), productId: toNum(addForm.productId), planCount: toNum(addForm.planCount), factoryId: toNum(addForm.factoryId) });
        if (res.success) { ElMessage.success("添加成功"); addVisible.value = false; getData(); } else { ElMessage.error(res.message || "添加失败"); }
      } catch { ElMessage.error("添加失败"); }
      addLoading.value = false;
    }

    function editRow(row: any) { editForm.id = row.id; editForm.planSeq = row.planSeq; editForm.orderId = String(row.orderId || ""); editForm.productId = String(row.productId || ""); editForm.planCount = String(row.planCount || ""); editForm.deliveryDate = row.deliveryDate; editForm.planStartDate = row.planStartDate; editForm.planEndDate = row.planEndDate; editForm.planStatus = row.planStatus; editForm.factoryId = String(row.factoryId || ""); editVisible.value = true; }

    async function saveEdit() {
      editLoading.value = true;
      try {
        const res: any = await HttpManager.updatePlan({ ...editForm, id: Number(editForm.id), orderId: toNum(editForm.orderId), productId: toNum(editForm.productId), planCount: toNum(editForm.planCount), factoryId: toNum(editForm.factoryId) });
        if (res.success) { ElMessage.success("修改成功"); editVisible.value = false; getData(); } else { ElMessage.error(res.message || "修改失败"); }
      } catch { ElMessage.error("修改失败"); }
      editLoading.value = false;
    }

    function deleteRow(id: number) { deleteId.value = id; delVisible.value = true; }
    async function confirmDelete() { if (deleteId.value == null) return; try { const res: any = await HttpManager.deletePlan(deleteId.value); if (res.success) { ElMessage.success("删除成功"); getData(); } else { ElMessage.error(res.message || "删除失败"); } } catch { ElMessage.error("删除失败"); } delVisible.value = false; }

    async function getData() { try { const res: any = await HttpManager.getPlanList(); if (res.success) tableData.value = res.data || []; } catch { /* ignore */ } }
    onMounted(() => getData());

    return { tableData, isAdmin, addVisible, addLoading, addForm, editVisible, editLoading, editForm, openAddDialog, addPlan, editRow, saveEdit, delVisible, deleteRow, confirmDelete, planStatusType, planStatusLabel };
  }
});
</script>

<style scoped>
.plan-container { padding: 20px; background: var(--bg-page); min-height: calc(100vh - 60px); }
.handle-box { margin-bottom: 20px; }
.handle-box :deep(.el-button--primary) { background: #b74dff !important; border-color: #b74dff !important; }
.handle-box :deep(.el-button--primary:hover) { background: #c87aff !important; border-color: #c87aff !important; }
:deep(.el-dialog .el-button--primary) { background: #b74dff !important; border-color: #b74dff !important; }
:deep(.el-dialog .el-button--primary:hover) { background: #c87aff !important; border-color: #c87aff !important; }
.action-btns { display: flex; gap: 6px; justify-content: center; }
.edit-btn { background: #fff !important; color: #606266 !important; border: 1px solid #dcdfe6 !important; padding: 5px 10px !important; font-size: 12px !important; min-height: 28px !important; }
.edit-btn:hover { color: #b74dff !important; border-color: rgba(183, 77, 255, 0.25) !important; background: rgba(183, 77, 255, 0.06) !important; }
.del-btn { background: #f56c6c !important; color: #fff !important; border: none !important; padding: 5px 10px !important; font-size: 12px !important; min-height: 28px !important; }
.del-btn:hover { background: #f78989 !important; }
body.dark .edit-btn { background: #2a2a4a !important; color: #c8d0da !important; border-color: #3a3a5a !important; }
body.dark .edit-btn:hover { background: rgba(183, 77, 255, 0.12) !important; border-color: #b74dff !important; color: #b74dff !important; }
:deep(.el-table) { --el-table-bg-color: var(--bg-card); --el-table-header-bg-color: var(--bg-card); --el-table-tr-bg-color: var(--bg-card); --el-table-border-color: var(--border-color); --el-table-row-hover-bg-color: var(--table-row-hover-bg); }
:deep(.el-table__body-wrapper), :deep(.el-table__body), :deep(.el-table__empty-block), :deep(.el-table__inner-wrapper), :deep(.el-table .el-table__body td.el-table__cell) { background: var(--bg-card); }
</style>
