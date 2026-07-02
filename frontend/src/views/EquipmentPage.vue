<template>
  <div class="equipment-container">
    <div class="handle-box">
      <div class="handle-left">
        <el-button v-if="isAdmin" type="primary" @click="openAddDialog">添加</el-button>
        <el-button class="export-btn" size="small" @click="exportExcel">导出 Excel</el-button>
        <el-button v-if="isAdmin" class="import-btn" size="small" @click="triggerImport">导入 Excel</el-button>
        <input type="file" ref="importFileRef" accept=".xlsx" style="display:none" @change="importExcel" />
      </div>
      <div class="handle-right">
        <el-input v-model="searchText" placeholder="搜索设备名称" clearable prefix-icon="Search" class="search-input" />
      </div>
    </div>

    <el-table height="550px" border size="small" :data="filteredData" style="width:100%">
      <el-table-column label="ID" prop="id" width="60" align="center" sortable />
      <el-table-column label="设备编号" prop="equipmentSeq" min-width="120" align="center" show-overflow-tooltip sortable />
      <el-table-column label="设备名称" prop="equipmentName" min-width="120" align="center" show-overflow-tooltip sortable />
      <el-table-column label="设备状态" width="100" align="center" sortable sort-by="equipmentStatus">
        <template v-slot="scope">
          <el-tag :type="statusType(scope.row.equipmentStatus)" size="small">
            {{ statusLabel(scope.row.equipmentStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="设备图片" min-width="140" align="center">
        <template v-slot="scope">
          <div class="img-cell">
            <template v-if="scope.row.equipmentImgUrl">
              <el-image
                class="thumb"
                :src="imageUrl(scope.row.equipmentImgUrl)"
                fit="cover"
                @click="previewUrl = imageUrl(scope.row.equipmentImgUrl); previewVisible = true"
              />
            </template>
            <el-icon v-else class="no-img"><Picture /></el-icon>
          </div>
          <el-button v-if="isAdmin" class="img-up-btn" size="small" @click="triggerImageUpload(scope.row)">{{ scope.row.equipmentImgUrl ? '换图' : '上传' }}</el-button>
        </template>
      </el-table-column>
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

    <!-- 图片预览 -->
    <el-dialog title="图片预览" v-model="previewVisible" width="500px" top="8vh" :close-on-click-modal="true">
      <div class="preview-wrap">
        <img :src="previewUrl" class="preview-img" alt=""/>
      </div>
    </el-dialog>

    <!-- 隐藏的文件输入 -->
    <input
      ref="fileInputRef"
      type="file"
      accept="image/*"
      style="display:none"
      @change="handleFileSelected"
    />

    <!-- 添加对话框 -->
    <el-dialog title="添加设备" v-model="addVisible" width="480px">
      <el-form label-width="90px" :model="addForm">
        <el-form-item label="设备编号"><el-input v-model="addForm.equipmentSeq" /></el-form-item>
        <el-form-item label="设备名称"><el-input v-model="addForm.equipmentName" /></el-form-item>
        <el-form-item label="设备状态">
          <el-select v-model="addForm.equipmentStatus">
            <el-option label="启用" :value="10" />
            <el-option label="停用" :value="20" />
            <el-option label="故障" :value="30" />
          </el-select>
        </el-form-item>
        <el-form-item label="工厂ID"><el-input v-model="addForm.factoryId" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addVisible = false">取消</el-button>
        <el-button type="primary" :loading="addLoading" @click="addEquipment">确定</el-button>
      </template>
    </el-dialog>

    <!-- 编辑对话框 -->
    <el-dialog title="编辑设备" v-model="editVisible" width="480px">
      <el-form label-width="90px" :model="editForm">
        <el-form-item label="设备编号"><el-input v-model="editForm.equipmentSeq" /></el-form-item>
        <el-form-item label="设备名称"><el-input v-model="editForm.equipmentName" /></el-form-item>
        <el-form-item label="设备状态">
          <el-select v-model="editForm.equipmentStatus">
            <el-option label="启用" :value="10" />
            <el-option label="停用" :value="20" />
            <el-option label="故障" :value="30" />
          </el-select>
        </el-form-item>
        <el-form-item label="工厂ID"><el-input v-model="editForm.factoryId" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" :loading="editLoading" @click="saveEdit">确定</el-button>
      </template>
    </el-dialog>

    <div class="pagination-wrap">
      <el-pagination
        background
        layout="total, prev, pager, next, sizes"
        :total="total"
        :page-size="pageSize"
        :current-page="page"
        :page-sizes="[10, 20, 50]"
        @current-change="onPageChange"
        @size-change="onSizeChange"
      />
    </div>

    <!-- 删除确认 -->
    <el-dialog title="确认删除" v-model="delVisible" width="360px" top="30vh">
      <p>确定要删除该设备吗？</p>
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
import { Picture } from "@element-plus/icons-vue";
import { HttpManager } from "@/api";
import { imageUrl } from "@/api/request";

export default defineComponent({
  setup() {
    const tableData = ref<any[]>([]);
    const total = ref(0);
    const page = ref(1);
    const pageSize = ref(10);
    const isAdmin = Number(sessionStorage.getItem("roleId")) === 1;
    const searchText = ref("");
    const importFileRef = ref<HTMLInputElement | null>(null);

    // ---- Excel 导入导出 ----
    async function exportExcel() {
      try { await HttpManager.exportEquipmentExcel(); ElMessage.success('导出成功'); }
      catch { ElMessage.error('导出失败'); }
    }
    function triggerImport() { importFileRef.value?.click(); }
    async function importExcel(e: Event) {
      const input = e.target as HTMLInputElement;
      const file = input.files?.[0];
      if (!file) return;
      try {
        const res: any = await HttpManager.importEquipmentExcel(file);
        if (res.success) { ElMessage.success(res.message || '导入成功'); getData(); }
        else { ElMessage.error(res.message || '导入失败'); }
      } catch { ElMessage.error('导入失败'); }
      input.value = '';
    }

    const filteredData = computed(() => {
      if (!searchText.value.trim()) return tableData.value;
      const q = searchText.value.trim().toLowerCase();
      return tableData.value.filter((r: any) => (r.equipmentName || '').toLowerCase().includes(q));
    });

    // ---- 图片预览 ----
    const previewVisible = ref(false);
    const previewUrl = ref("");

    // ---- 图片上传 ----
    const fileInputRef = ref<HTMLInputElement | null>(null);
    const uploadTargetRow = ref<any>(null);

    function triggerImageUpload(row: any) {
      uploadTargetRow.value = row;
      fileInputRef.value?.click();
    }

    async function handleFileSelected(e: Event) {
      const input = e.target as HTMLInputElement;
      const file = input.files?.[0];
      if (!file || !uploadTargetRow.value) return;
      const row = uploadTargetRow.value;
      try {
        const res: any = await HttpManager.uploadEquipmentImage(row.id, file);
        if (res.success) {
          ElMessage.success('图片上传成功');
          await getData();
        } else {
          ElMessage.error(res.message || '上传失败');
        }
      } catch {
        ElMessage.error('图片上传失败');
      }
      input.value = '';
      uploadTargetRow.value = null;
    }

    function statusType(status: number): string {
      if (status === 10) return 'success';
      if (status === 20) return 'info';
      if (status === 30) return 'danger';
      return 'info';
    }

    function statusLabel(status: number): string {
      if (status === 10) return '启用';
      if (status === 20) return '停用';
      if (status === 30) return '故障';
      return '未知';
    }

    // ---- 添加 ----
    const addVisible = ref(false);
    const addLoading = ref(false);
    const addForm = reactive({ equipmentSeq: "", equipmentName: "", equipmentStatus: 10, factoryId: "" });

    function openAddDialog() {
      addForm.equipmentSeq = "";
      addForm.equipmentName = "";
      addForm.equipmentStatus = 10;
      addForm.factoryId = "";
      addVisible.value = true;
    }

    async function addEquipment() {
      addLoading.value = true;
      try {
        const res: any = await HttpManager.addEquipment(addForm);
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
    const editForm = reactive({ id: "", equipmentSeq: "", equipmentName: "", equipmentStatus: 10, factoryId: "" });

    function editRow(row: any) {
      editForm.id = row.id;
      editForm.equipmentSeq = row.equipmentSeq;
      editForm.equipmentName = row.equipmentName;
      editForm.equipmentStatus = row.equipmentStatus;
      editForm.factoryId = row.factoryId;
      editVisible.value = true;
    }

    async function saveEdit() {
      editLoading.value = true;
      try {
        const res: any = await HttpManager.updateEquipment(editForm);
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
        const res: any = await HttpManager.deleteEquipment(deleteId.value);
        if (res.success) { ElMessage.success('删除成功'); getData(); }
        else { ElMessage.error(res.message || '删除失败'); }
      } catch { ElMessage.error('删除失败'); }
      delVisible.value = false;
    }

    async function getData() {
      try {
        const res: any = await HttpManager.getEquipmentList(page.value, pageSize.value);
        if (res.success) {
          const data = res.data;
          if (data?.items !== undefined) {
            tableData.value = data.items;
            total.value = data.total;
          } else {
            tableData.value = data || [];
            total.value = Array.isArray(data) ? data.length : 0;
          }
        }
      } catch { /* ignore */ }
    }

    function onPageChange(p: number) { page.value = p; getData(); }
    function onSizeChange(s: number) { pageSize.value = s; page.value = 1; getData(); }

    onMounted(() => getData());

    return {
      tableData, isAdmin, total, page, pageSize, searchText, filteredData,
      importFileRef, exportExcel, triggerImport, importExcel,
      onPageChange, onSizeChange,
      imageUrl, Picture,
      previewVisible, previewUrl,
      fileInputRef, triggerImageUpload, handleFileSelected,
      statusType, statusLabel,
      addVisible, addLoading, addForm, openAddDialog, addEquipment,
      editVisible, editLoading, editForm, editRow, saveEdit,
      delVisible, deleteRow, confirmDelete,
    };
  }
});
</script>

<style scoped>
.equipment-container {
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
body.dark :deep(.el-dialog .el-button--primary),
body.dark :deep(.handle-box .el-button--primary) {
  background: #b74dff !important;
  border-color: #b74dff !important;
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

.img-cell {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
}
.thumb {
  width: 36px;
  height: 36px;
  border-radius: 4px;
  cursor: pointer;
  border: 1px solid var(--border-color);
}
.no-img { font-size: 28px; color: #dcdfe6; }
.img-up-btn {
  background: #b74dff !important;
  color: #fff !important;
  border: none !important;
  padding: 5px 10px !important;
  font-size: 12px !important;
  min-height: 28px !important;
  line-height: 1 !important;
  border-radius: 4px !important;
}
.img-up-btn:hover {
  background: #c87aff !important;
}

.preview-wrap { display: flex; justify-content: center; align-items: center; }
.preview-img { max-width: 100%; max-height: 70vh; object-fit: contain; }

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
.pagination-wrap { margin-top: 16px; display: flex; justify-content: flex-end; }
.pagination-wrap :deep(.el-pager li.is-active) { background: #b74dff !important; border-color: #b74dff !important; }
body.dark .no-img { color: #3a3a5a; }
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
