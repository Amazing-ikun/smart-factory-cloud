<template>
  <div class="container">
    <div class="handle-box">
      <div class="handle-left">
        <el-button v-if="isAdmin" type="primary" @click="openAddDialog">添加</el-button>
        <el-button class="export-btn" size="small" @click="exportExcel">导出 Excel</el-button>
        <el-button v-if="isAdmin" class="import-btn" size="small" @click="triggerImport">导入 Excel</el-button>
        <input type="file" ref="importFileRef" accept=".xlsx" style="display:none" @change="importExcel" />
      </div>
      <div class="handle-right">
        <el-input v-model="searchText" placeholder="搜索产品名称" clearable prefix-icon="Search" class="search-input" @input="onSearch" />
      </div>
    </div>

    <el-table height="550px" border size="small" :data="filteredData" style="width:100%">
      <el-table-column label="ID" prop="id" width="60" align="center" sortable />
      <el-table-column label="产品编号" prop="productNum" min-width="120" align="center" show-overflow-tooltip sortable />
      <el-table-column label="产品名称" prop="productName" min-width="120" align="center" show-overflow-tooltip sortable />
      <el-table-column label="工厂ID" prop="factoryId" width="80" align="center" sortable />
      <el-table-column label="产品图片" min-width="140" align="center">
        <template v-slot="scope">
          <div class="img-cell">
            <template v-if="scope.row.productImgUrl">
              <el-image
                class="thumb"
                :src="imageUrl(scope.row.productImgUrl)"
                fit="cover"
                @click="previewUrl = imageUrl(scope.row.productImgUrl); previewVisible = true"
              />
            </template>
            <el-icon v-else class="no-img"><Picture /></el-icon>
          </div>
          <el-button v-if="isAdmin" class="img-up-btn" size="small" @click="triggerImageUpload(scope.row)">{{ scope.row.productImgUrl ? '换图' : '上传' }}</el-button>
        </template>
      </el-table-column>
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
        <img :src="previewUrl" class="preview-img"  alt=""/>
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
    <el-dialog title="添加产品" v-model="addVisible" width="480px">
      <el-form label-width="90px" :model="addForm">
        <el-form-item label="产品编号"><el-input v-model="addForm.productNum" /></el-form-item>
        <el-form-item label="产品名称"><el-input v-model="addForm.productName" /></el-form-item>
        <el-form-item label="工厂ID"><el-input v-model="addForm.factoryId" /></el-form-item>
        <el-form-item label="产品图片">
          <input type="file" accept="image/*" @change="onAddFileChange" />
          <span v-if="addFormFileName" class="file-name">{{ addFormFileName }}</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addVisible = false">取消</el-button>
        <el-button type="primary" :loading="addLoading" @click="addProduct">确定</el-button>
      </template>
    </el-dialog>

    <!-- 编辑对话框 -->
    <el-dialog title="编辑产品" v-model="editVisible" width="480px">
      <el-form label-width="90px" :model="editForm">
        <el-form-item label="产品编号"><el-input v-model="editForm.productNum" /></el-form-item>
        <el-form-item label="产品名称"><el-input v-model="editForm.productName" /></el-form-item>
        <el-form-item label="工厂ID"><el-input v-model="editForm.factoryId" /></el-form-item>
        <el-form-item label="产品图片">
          <div class="edit-img-row">
            <el-image
              v-if="editFormImg"
              class="edit-thumb"
              :src="editFormImg"
              fit="cover"
            />
            <el-icon v-else class="no-img"><Picture /></el-icon>
            <input type="file" accept="image/*" @change="onEditFileChange" />
          </div>
        </el-form-item>
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
      <p>确定要删除该产品吗？</p>
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
import { Picture, Search } from "@element-plus/icons-vue";
import { HttpManager } from "@/api";
import { imageUrl } from "@/api/request";

export default defineComponent({
  components: { Picture, Search },
  setup() {
    const tableData = ref<any[]>([]);
    const total = ref(0);
    const page = ref(1);
    const pageSize = ref(10);
    const isAdmin = Number(sessionStorage.getItem("roleId")) === 1;
    const searchText = ref("");

    const filteredData = computed(() => {
      if (!searchText.value.trim()) return tableData.value;
      const q = searchText.value.trim().toLowerCase();
      return tableData.value.filter((r: any) => (r.productName || '').toLowerCase().includes(q));
    });

    function onSearch() { /* computed handles filtering */ }

    // ---- Excel 导入导出 ----
    const importFileRef = ref<HTMLInputElement | null>(null);

    async function exportExcel() {
      try {
        await HttpManager.exportProductExcel();
        ElMessage.success('导出成功');
      } catch { ElMessage.error('导出失败'); }
    }

    function triggerImport() { importFileRef.value?.click(); }

    async function importExcel(e: Event) {
      const input = e.target as HTMLInputElement;
      const file = input.files?.[0];
      if (!file) return;
      try {
        const res: any = await HttpManager.importProductExcel(file);
        if (res.success) { ElMessage.success(res.message || '导入成功'); getData(); }
        else { ElMessage.error(res.message || '导入失败'); }
      } catch { ElMessage.error('导入失败'); }
      input.value = '';
    }

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
        const res: any = await HttpManager.uploadProductImage(row.id, file);
        if (res.success) {
          ElMessage.success('图片上传成功');
          row.productImgUrl = res.data;
          await getData();
        } else {
          console.error('上传失败:', res.message);
          ElMessage.error(res.message || '上传失败');
        }
      } catch (err) {
        console.error('上传异常:', err);
        ElMessage.error('图片上传失败');
      }
      input.value = '';
      uploadTargetRow.value = null;
    }

    // ---- 添加 ----
    const addVisible = ref(false);
    const addLoading = ref(false);
    const addForm = reactive({ productNum: "", productName: "", factoryId: "" });
    const addFile = ref<File | null>(null);
    const addFormFileName = ref("");

    function openAddDialog() {
      addForm.productNum = "";
      addForm.productName = "";
      addForm.factoryId = "";
      addFile.value = null;
      addFormFileName.value = "";
      addVisible.value = true;
    }

    function onAddFileChange(e: Event) {
      const input = e.target as HTMLInputElement;
      addFile.value = input.files?.[0] || null;
      addFormFileName.value = addFile.value ? addFile.value.name : "";
    }

    async function addProduct() {
      addLoading.value = true;
      try {
        const res: any = await HttpManager.addProduct(addForm);
        if (res.success) {
          if (addFile.value && res.data) {
            await HttpManager.uploadProductImage(res.data, addFile.value);
          }
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
    const editForm = reactive({ id: "", productNum: "", productName: "", factoryId: "" });
    const editFormImg = ref("");
    const editFile = ref<File | null>(null);

    function editRow(row: any) {
      editForm.id = row.id;
      editForm.productNum = row.productNum;
      editForm.productName = row.productName;
      editForm.factoryId = row.factoryId;
      editFormImg.value = imageUrl(row.productImgUrl);
      editFile.value = null;
      editVisible.value = true;
    }

    function onEditFileChange(e: Event) {
      const input = e.target as HTMLInputElement;
      editFile.value = input.files?.[0] || null;
      if (editFile.value) {
        editFormImg.value = URL.createObjectURL(editFile.value);
      }
    }

    async function saveEdit() {
      editLoading.value = true;
      try {
        const res: any = await HttpManager.updateProduct(editForm);
        if (res.success) {
          if (editFile.value) {
            await HttpManager.uploadProductImage(Number(editForm.id), editFile.value);
          }
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
        const res: any = await HttpManager.deleteProduct(deleteId.value);
        if (res.success) { ElMessage.success('删除成功'); getData(); }
        else { ElMessage.error(res.message || '删除失败'); }
      } catch { ElMessage.error('删除失败'); }
      delVisible.value = false;
    }

    // ---- 数据加载 ----
    onMounted(() => getData());

    async function getData() {
      try {
        const res: any = await HttpManager.getProductList(page.value, pageSize.value);
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
      } catch { ElMessage.error('获取产品数据失败'); }
    }

    function onPageChange(p: number) { page.value = p; getData(); }
    function onSizeChange(s: number) { pageSize.value = s; page.value = 1; getData(); }

    return {
      tableData, total, page, pageSize,
      isAdmin, searchText, filteredData, onSearch,
      importFileRef, exportExcel, triggerImport, importExcel,
      imageUrl,
      onPageChange, onSizeChange,
      // 预览
      previewVisible, previewUrl,
      // 上传
      fileInputRef, triggerImageUpload, handleFileSelected,
      // 添加
      addVisible, addLoading, addForm, addFormFileName, openAddDialog, onAddFileChange, addProduct,
      // 编辑
      editVisible, editLoading, editForm, editFormImg, editRow, onEditFileChange, saveEdit,
      // 删除
      delVisible, deleteRow, confirmDelete,
    };
  }
});
</script>

<style scoped>
.container { padding: 20px; background: var(--bg-page); min-height: calc(100vh - 60px); }
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

/* Dialog primary buttons */
:deep(.el-dialog .el-button--primary) {
  background: #b74dff !important;
  border-color: #b74dff !important;
}
:deep(.el-dialog .el-button--primary:hover) {
  background: #c87aff !important;
  border-color: #c87aff !important;
}

/* ── 操作按钮行 ── */
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

/* ── dark mode ── */
:deep(.el-table) {
  --el-table-bg-color: var(--bg-card);
  --el-table-header-bg-color: var(--bg-card);
  --el-table-tr-bg-color: var(--bg-card);
  --el-table-border-color: var(--border-color);
  --el-table-row-hover-bg-color: var(--table-row-hover-bg);
}
/* 表格 body 空白区域背景 */
:deep(.el-table__body-wrapper) {
  background: var(--bg-card);
}
/* 表格体块级背景 */
:deep(.el-table__body) {
  background: var(--bg-card);
}
/* 固定高度表格末尾空白区域 */
:deep(.el-table__empty-block) {
  background: var(--bg-card);
}
/* 表格 Footer / scrollbar 区域 */
:deep(.el-table__inner-wrapper) {
  background: var(--bg-card);
}
/* 行内 td 背景 */
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
body.dark .no-img { color: #3a3a5a; }
body.dark .thumb,
body.dark .edit-thumb { border-color: #3a3a5a; }
body.dark .file-name { color: #8892a4; }

.pagination-wrap { margin-top: 16px; display: flex; justify-content: flex-end; }
.pagination-wrap :deep(.el-pager li.is-active) { background: #b74dff !important; border-color: #b74dff !important; }

/* ── 图片列 ── */
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
  flex-shrink: 0;
  border: 1px solid #ebeef5;
}
.no-img {
  font-size: 28px;
  color: #dcdfe6;
  flex-shrink: 0;
}
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

/* ── 图片预览 ── */
.preview-wrap {
  display: flex;
  justify-content: center;
  align-items: center;
}
.preview-img {
  max-width: 100%;
  max-height: 70vh;
  object-fit: contain;
}

/* ── 编辑框内图片 ── */
.edit-img-row {
  display: flex;
  align-items: center;
  gap: 10px;
}
.edit-thumb {
  width: 60px;
  height: 60px;
  border-radius: 4px;
  border: 1px solid #ebeef5;
  flex-shrink: 0;
}
.file-name {
  margin-left: 8px;
  font-size: 12px;
  color: #909399;
}
</style>
