<template>
  <div class="users-container">
    <div class="handle-box">
      <div class="handle-left" v-if="isSuperAdmin">
        <el-button type="primary" @click="openAddDialog">添加用户</el-button>
      </div>
      <div class="handle-right">
        <el-badge :value="unreadCount" :hidden="unreadCount === 0">
          <el-button class="inbox-btn" @click="goToInbox">
            <el-icon><Message /></el-icon>
            <span>消息</span>
          </el-button>
        </el-badge>
      </div>
    </div>

    <el-table height="550px" border size="small" :data="tableData" style="width:100%">
      <el-table-column label="ID" prop="id" width="60" align="center" sortable />
      <el-table-column label="用户名" prop="userName" min-width="100" align="center" show-overflow-tooltip sortable />
      <el-table-column label="姓名" prop="userRealName" min-width="100" align="center" show-overflow-tooltip sortable />
      <el-table-column label="角色" width="100" align="center" sortable sort-by="roleId">
        <template v-slot="scope">
          <el-tag :type="scope.row.roleId === 1 ? 'danger' : 'success'" size="small">
            {{ scope.row.roleId === 1 ? '管理员' : '普通用户' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="工号" prop="userJobNum" min-width="110" align="center" show-overflow-tooltip sortable />
      <el-table-column label="手机号" prop="userPhoneNum" min-width="130" align="center" sortable />
      <el-table-column label="邮箱" prop="userEmail" min-width="160" align="center" show-overflow-tooltip sortable />
      <el-table-column label="工厂ID" prop="factoryId" width="80" align="center" sortable />
      <el-table-column label="状态" width="80" align="center" sortable sort-by="userStatus">
        <template v-slot="scope">
          <el-tag :type="scope.row.userStatus === 0 ? 'success' : 'info'" size="small">
            {{ scope.row.userStatus === 0 ? '正常' : '锁定' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="160" align="center" sortable />
      <el-table-column label="操作" min-width="180" align="center" v-if="isSuperAdmin">
        <template v-slot="scope">
          <div class="action-btns" v-if="scope.row.id !== 1">
            <el-button class="edit-btn" size="small" @click="editRow(scope.row)">编辑</el-button>
            <el-button class="reset-pwd-btn" size="small" @click="resetPassword(scope.row)">重置密码</el-button>
            <el-button class="del-btn" size="small" @click="deleteRow(scope.row)">删除</el-button>
          </div>
          <span v-else class="super-badge">SuperAdmin</span>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加对话框 -->
    <el-dialog title="添加用户" v-model="addVisible" width="480px">
      <el-form label-width="90px" :model="addForm">
        <el-form-item label="用户名"><el-input v-model="addForm.userName" /></el-form-item>
        <el-form-item label="姓名"><el-input v-model="addForm.userRealName" /></el-form-item>
        <el-form-item label="密码"><el-input type="password" v-model="addForm.userPasswd" /></el-form-item>
        <el-form-item label="角色">
          <el-radio-group v-model="addForm.roleId">
            <el-radio :value="1">管理员</el-radio>
            <el-radio :value="2">普通用户</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="工号"><el-input v-model="addForm.userJobNum" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="addForm.userPhoneNum" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="addForm.userEmail" /></el-form-item>
        <el-form-item label="工厂ID"><el-input v-model="addForm.factoryId" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addVisible = false">取消</el-button>
        <el-button type="primary" :loading="addLoading" @click="addUser">确定</el-button>
      </template>
    </el-dialog>

    <!-- 编辑对话框 -->
    <el-dialog title="编辑用户" v-model="editVisible" width="480px">
      <el-form label-width="90px" :model="editForm">
        <el-form-item label="用户名"><el-input v-model="editForm.userName" /></el-form-item>
        <el-form-item label="姓名"><el-input v-model="editForm.userRealName" /></el-form-item>
        <el-form-item label="角色">
          <el-radio-group v-model="editForm.roleId">
            <el-radio :value="1">管理员</el-radio>
            <el-radio :value="2">普通用户</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="用户状态">
          <el-select v-model="editForm.userStatus">
            <el-option label="正常" :value="0" />
            <el-option label="锁定" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="工号"><el-input v-model="editForm.userJobNum" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="editForm.userPhoneNum" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="editForm.userEmail" /></el-form-item>
        <el-form-item label="工厂ID"><el-input v-model="editForm.factoryId" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" :loading="editLoading" @click="saveEdit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 重置密码对话框 -->
    <el-dialog title="重置密码" v-model="pwdVisible" width="380px" top="25vh">
      <el-form label-width="80px">
        <el-form-item label="新密码">
          <el-input type="password" v-model="pwdValue" placeholder="请输入新密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pwdVisible = false">取消</el-button>
        <el-button type="primary" :loading="pwdLoading" @click="confirmResetPwd">确定</el-button>
      </template>
    </el-dialog>

    <!-- 删除确认 -->
    <el-dialog title="确认删除" v-model="delVisible" width="360px" top="30vh">
      <p>确定要删除用户 <strong>{{ delTarget?.userName }}</strong> 吗？</p>
      <template #footer>
        <el-button @click="delVisible = false">取消</el-button>
        <el-button type="danger" :loading="delLoading" @click="confirmDelete">删除</el-button>
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
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, reactive, onMounted } from "vue";
import { useRouter } from 'vue-router';
import { ElMessage } from "element-plus";
import { Message } from "@element-plus/icons-vue";
import { HttpManager } from "@/api";

export default defineComponent({
  components: { Message },
  setup() {
    const router = useRouter();
    const tableData = ref<any[]>([]);
    const total = ref(0);
    const page = ref(1);
    const pageSize = ref(10);
    const isSuperAdmin = Number(sessionStorage.getItem("userId")) === 1;
    const unreadCount = ref(0);

    async function loadUnread() {
      try {
        const res: any = await HttpManager.getUnreadCount();
        if (res.success) unreadCount.value = res.data || 0;
      } catch { /* ignore */ }
    }

    function goToInbox() { router.push('/Home/inbox'); }

    // ---- 数据加载 ----
    async function getData() {
      try {
        const res: any = await HttpManager.getUserList(page.value, pageSize.value);
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

    // ---- 添加 ----
    const addVisible = ref(false);
    const addLoading = ref(false);
    const addForm = reactive<any>({
      userName: "", userRealName: "", userPasswd: "", roleId: 2,
      userJobNum: "", userPhoneNum: "", userEmail: "", factoryId: 1,
    });

    function openAddDialog() {
      addForm.userName = "";
      addForm.userRealName = "";
      addForm.userPasswd = "";
      addForm.roleId = 2;
      addForm.userJobNum = "";
      addForm.userPhoneNum = "";
      addForm.userEmail = "";
      addForm.factoryId = 1;
      addVisible.value = true;
    }

    async function addUser() {
      if (!addForm.userName.trim()) { ElMessage.warning("请输入用户名"); return; }
      if (!addForm.userPasswd.trim()) { ElMessage.warning("请输入密码"); return; }
      addLoading.value = true;
      try {
        const res: any = await HttpManager.createUser(addForm);
        if (res.success) {
          ElMessage.success("创建成功");
          addVisible.value = false;
          getData();
        } else {
          ElMessage.error(res.message || "创建失败");
        }
      } catch { ElMessage.error("创建失败"); }
      addLoading.value = false;
    }

    // ---- 编辑 ----
    const editVisible = ref(false);
    const editLoading = ref(false);
    const editForm = reactive<any>({});

    function editRow(row: any) {
      editForm.id = row.id;
      editForm.userName = row.userName;
      editForm.userRealName = row.userRealName || "";
      editForm.roleId = row.roleId;
      editForm.userStatus = row.userStatus;
      editForm.userJobNum = row.userJobNum || "";
      editForm.userPhoneNum = row.userPhoneNum || "";
      editForm.userEmail = row.userEmail || "";
      editForm.factoryId = row.factoryId;
      editVisible.value = true;
    }

    async function saveEdit() {
      if (!editForm.userName.trim()) { ElMessage.warning("请输入用户名"); return; }
      editLoading.value = true;
      try {
        const res: any = await HttpManager.updateUser(editForm);
        if (res.success) {
          ElMessage.success("修改成功");
          editVisible.value = false;
          getData();
        } else {
          ElMessage.error(res.message || "修改失败");
        }
      } catch { ElMessage.error("修改失败"); }
      editLoading.value = false;
    }

    // ---- 重置密码 ----
    const pwdVisible = ref(false);
    const pwdLoading = ref(false);
    const pwdTarget = ref<any>(null);
    const pwdValue = ref("");

    function resetPassword(row: any) {
      pwdTarget.value = row;
      pwdValue.value = "123456";
      pwdVisible.value = true;
    }

    async function confirmResetPwd() {
      if (!pwdValue.value.trim()) { ElMessage.warning("请输入新密码"); return; }
      pwdLoading.value = true;
      try {
        const res: any = await HttpManager.resetUserPassword(pwdTarget.value.id, pwdValue.value.trim());
        if (res.success) {
          ElMessage.success("密码已重置");
          pwdVisible.value = false;
        } else {
          ElMessage.error(res.message || "重置失败");
        }
      } catch { ElMessage.error("重置失败"); }
      pwdLoading.value = false;
    }

    // ---- 删除 ----
    const delVisible = ref(false);
    const delLoading = ref(false);
    const delTarget = ref<any>(null);

    function deleteRow(row: any) {
      delTarget.value = row;
      delVisible.value = true;
    }

    async function confirmDelete() {
      if (!delTarget.value) return;
      delLoading.value = true;
      try {
        const res: any = await HttpManager.deleteUser(delTarget.value.id);
        if (res.success) {
          ElMessage.success("删除成功");
          delVisible.value = false;
          getData();
        } else {
          ElMessage.error(res.message || "删除失败");
        }
      } catch { ElMessage.error("删除失败"); }
      delLoading.value = false;
    }

    onMounted(() => {
      getData();
      loadUnread();
    });

    return {
      tableData, isSuperAdmin, total, page, pageSize,
      onPageChange, onSizeChange, unreadCount, goToInbox,
      addVisible, addLoading, addForm, openAddDialog, addUser,
      editVisible, editLoading, editForm, editRow, saveEdit,
      pwdVisible, pwdLoading, pwdValue, resetPassword, confirmResetPwd,
      delVisible, delLoading, delTarget, deleteRow, confirmDelete,
    };
  }
});
</script>

<style scoped>
.users-container {
  padding: 20px;
  background: var(--bg-page);
  min-height: calc(100vh - 60px);
}

.handle-box {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.handle-left { display: flex; gap: 8px; }
.handle-right { display: flex; gap: 8px; }
.inbox-btn {
  background: transparent !important;
  border: 1px solid var(--border-color) !important;
  color: var(--text-primary) !important;
  font-size: 13px !important;
  display: inline-flex !important;
  align-items: center !important;
  gap: 4px !important;
}
.inbox-btn:hover {
  border-color: #b74dff !important;
  color: #b74dff !important;
}
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

.pagination-wrap { margin-top: 16px; display: flex; justify-content: flex-end; }
.pagination-wrap :deep(.el-pager li.is-active) { background: #b74dff !important; border-color: #b74dff !important; }

.action-btns {
  display: flex;
  gap: 4px;
  justify-content: center;
  align-items: center;
  flex-wrap: wrap;
}

.edit-btn {
  background: #fff !important;
  color: #606266 !important;
  border: 1px solid #dcdfe6 !important;
  padding: 4px 8px !important;
  font-size: 12px !important;
  min-height: 26px !important;
}
.edit-btn:hover {
  color: #b74dff !important;
  border-color: rgba(183, 77, 255, 0.25) !important;
  background: rgba(183, 77, 255, 0.06) !important;
}

.reset-pwd-btn {
  background: #fff !important;
  color: #e6a23c !important;
  border: 1px solid #e6a23c !important;
  padding: 4px 8px !important;
  font-size: 12px !important;
  min-height: 26px !important;
}
.reset-pwd-btn:hover {
  background: #fdf6ec !important;
  border-color: #e6a23c !important;
}

.del-btn {
  background: #f56c6c !important;
  color: #fff !important;
  border: none !important;
  padding: 4px 8px !important;
  font-size: 12px !important;
  min-height: 26px !important;
}
.del-btn:hover {
  background: #f78989 !important;
}

.super-badge {
  font-size: 11px;
  color: var(--text-secondary);
  font-style: italic;
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
body.dark .reset-pwd-btn {
  background: #2a2a4a !important;
  color: #e6a23c !important;
  border-color: #e6a23c !important;
}
body.dark .reset-pwd-btn:hover {
  background: #3a2a1a !important;
}
</style>
