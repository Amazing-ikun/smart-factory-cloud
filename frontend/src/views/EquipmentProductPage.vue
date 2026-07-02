<template>
  <div class="ep-container">
    <div class="handle-box" v-if="isAdmin">
      <el-button type="primary" @click="openAddDialog">添加关联</el-button>
    </div>

    <el-table height="550px" border size="small" :data="tableData" style="width:100%">
      <el-table-column label="ID" prop="id" width="60" align="center" sortable />
      <el-table-column label="设备ID" prop="equipmentId" width="80" align="center" sortable />
      <el-table-column label="产品ID" prop="productId" width="80" align="center" sortable />
      <el-table-column label="产能" prop="yield" width="80" align="center" sortable />
      <el-table-column label="产能单位" width="100" align="center">
        <template v-slot="scope">
          <span>{{ unitLabel(scope.row.unit) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="工厂ID" prop="factoryId" width="80" align="center" sortable />
      <el-table-column label="创建时间" prop="createTime" width="160" align="center" sortable />
      <el-table-column label="操作" min-width="100" align="center" v-if="isAdmin">
        <template v-slot="scope">
          <div class="action-btns">
            <el-button class="del-btn" size="small" @click="deleteRow(scope.row.id)">删除</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加对话框 -->
    <el-dialog title="添加设备-产品关联" v-model="addVisible" width="480px">
      <el-form label-width="100px" :model="addForm">
        <el-form-item label="设备ID"><el-input v-model="addForm.equipmentId" /></el-form-item>
        <el-form-item label="产品ID"><el-input v-model="addForm.productId" /></el-form-item>
        <el-form-item label="产能"><el-input v-model="addForm.yield" /></el-form-item>
        <el-form-item label="产能单位">
          <el-select v-model="addForm.unit">
            <el-option label="天" :value="10" />
            <el-option label="月" :value="20" />
            <el-option label="年" :value="30" />
            <el-option label="小时" :value="40" />
          </el-select>
        </el-form-item>
        <el-form-item label="工厂ID"><el-input v-model="addForm.factoryId" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addVisible = false">取消</el-button>
        <el-button type="primary" :loading="addLoading" @click="addEP">确定</el-button>
      </template>
    </el-dialog>

    <!-- 删除确认 -->
    <el-dialog title="确认删除" v-model="delVisible" width="360px" top="30vh">
      <p>确定要删除该关联吗？</p>
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
    const addVisible = ref(false);
    const addLoading = ref(false);
    const addForm = reactive({ equipmentId: "", productId: "", yield: "", unit: 10, factoryId: "" });
    const delVisible = ref(false);
    const deleteId = ref<number | null>(null);

    function unitLabel(u: number): string {
      if (u === 10) return '天';
      if (u === 20) return '月';
      if (u === 30) return '年';
      if (u === 40) return '小时';
      return '';
    }

    function openAddDialog() {
      addForm.equipmentId = ""; addForm.productId = ""; addForm.yield = "";
      addForm.unit = 10; addForm.factoryId = "";
      addVisible.value = true;
    }

    async function addEP() {
      addLoading.value = true;
      try {
        const data = { ...addForm, equipmentId: Number(addForm.equipmentId), productId: Number(addForm.productId), yield: addForm.yield ? Number(addForm.yield) : undefined, factoryId: addForm.factoryId ? Number(addForm.factoryId) : undefined };
        const res: any = await HttpManager.addEquipmentProduct(data);
        if (res.success) { ElMessage.success("添加成功"); addVisible.value = false; getData(); }
        else { ElMessage.error(res.message || "添加失败"); }
      } catch { ElMessage.error("添加失败"); }
      addLoading.value = false;
    }

    function deleteRow(id: number) { deleteId.value = id; delVisible.value = true; }

    async function confirmDelete() {
      if (deleteId.value == null) return;
      try {
        const res: any = await HttpManager.deleteEquipmentProduct(deleteId.value);
        if (res.success) { ElMessage.success("删除成功"); getData(); }
        else { ElMessage.error(res.message || "删除失败"); }
      } catch { ElMessage.error("删除失败"); }
      delVisible.value = false;
    }

    async function getData() {
      try {
        const res: any = await HttpManager.getEquipmentProductList();
        if (res.success) tableData.value = res.data || [];
      } catch { /* ignore */ }
    }

    onMounted(() => getData());

    return { tableData, isAdmin, addVisible, addLoading, addForm, unitLabel, openAddDialog, addEP, delVisible, deleteRow, confirmDelete };
  }
});
</script>

<style scoped>
.ep-container { padding: 20px; background: var(--bg-page); min-height: calc(100vh - 60px); }
.handle-box { margin-bottom: 20px; }
.handle-box :deep(.el-button--primary) { background: #b74dff !important; border-color: #b74dff !important; }
.handle-box :deep(.el-button--primary:hover) { background: #c87aff !important; border-color: #c87aff !important; }
:deep(.el-dialog .el-button--primary) { background: #b74dff !important; border-color: #b74dff !important; }
:deep(.el-dialog .el-button--primary:hover) { background: #c87aff !important; border-color: #c87aff !important; }
.action-btns { display: flex; gap: 6px; justify-content: center; }
.del-btn { background: #f56c6c !important; color: #fff !important; border: none !important; padding: 5px 10px !important; font-size: 12px !important; min-height: 28px !important; }
.del-btn:hover { background: #f78989 !important; }
:deep(.el-table) { --el-table-bg-color: var(--bg-card); --el-table-header-bg-color: var(--bg-card); --el-table-tr-bg-color: var(--bg-card); --el-table-border-color: var(--border-color); --el-table-row-hover-bg-color: var(--table-row-hover-bg); }
:deep(.el-table__body-wrapper), :deep(.el-table__body), :deep(.el-table__empty-block), :deep(.el-table__inner-wrapper), :deep(.el-table .el-table__body td.el-table__cell) { background: var(--bg-card); }
</style>
