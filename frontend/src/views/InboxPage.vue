<template>
  <div class="inbox-container">
    <div class="handle-box">
      <span class="page-title">消息中心</span>
    </div>

    <el-tabs v-model="activeTab" @tab-change="onTabChange">
      <el-tab-pane label="收件箱" name="inbox">
        <el-table height="500px" border size="small" :data="tableData" style="width:100%">
          <el-table-column label="标题" prop="title" min-width="200" align="left" show-overflow-tooltip>
            <template v-slot="scope">
              <span class="msg-title" :class="{ unread: !scope.row.isRead }">
                <span v-if="!scope.row.isRead" class="unread-dot" />
                {{ scope.row.title }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="发件人" prop="fromUserName" width="100" align="center" />
          <el-table-column label="内容" prop="content" min-width="250" align="left" show-overflow-tooltip />
          <el-table-column label="时间" prop="createTime" width="170" align="center" sortable />
          <el-table-column label="操作" width="100" align="center">
            <template v-slot="scope">
              <el-button class="edit-btn" size="small" @click="viewMessage(scope.row)">查看</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-wrap">
          <el-pagination
            background
            layout="total, prev, pager, next"
            :total="total"
            :page-size="pageSize"
            :current-page="page"
            @current-change="onPageChange"
          />
        </div>
      </el-tab-pane>

      <!-- 管理员：反馈管理 -->
      <el-tab-pane label="反馈管理" name="feedback" v-if="isAdmin">
        <el-table height="500px" border size="small" :data="feedbackData" style="width:100%">
          <el-table-column label="ID" prop="id" width="60" align="center" />
          <el-table-column label="用户" prop="userName" width="100" align="center" />
          <el-table-column label="类型" prop="feedbackType" width="80" align="center">
            <template v-slot="scope">
              <el-tag :type="scope.row.feedbackType === 'bug' ? 'danger' : 'info'" size="small">
                {{ scope.row.feedbackType === 'bug' ? 'Bug' : '建议' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="内容" prop="content" min-width="200" align="left" show-overflow-tooltip />
          <el-table-column label="时间" prop="createTime" width="170" align="center" />
          <el-table-column label="状态" width="80" align="center">
            <template v-slot="scope">
              <el-tag :type="scope.row.replyContent ? 'success' : 'info'" size="small">
                {{ scope.row.replyContent ? '已回复' : '待回复' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100" align="center">
            <template v-slot="scope">
              <el-button class="edit-btn" size="small" @click="openReplyDialog(scope.row)">回复</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-wrap">
          <el-pagination
            background
            layout="total, prev, pager, next"
            :total="fbTotal"
            :page-size="fbPageSize"
            :current-page="fbPage"
            @current-change="onFbPageChange"
          />
        </div>
      </el-tab-pane>

      <!-- 超级管理员：申请管理 -->
      <el-tab-pane label="申请管理" name="applications" v-if="isSuperAdmin">
        <el-table height="500px" border size="small" :data="applicationData" style="width:100%">
          <el-table-column label="ID" prop="id" width="60" align="center" />
          <el-table-column label="申请人" prop="userName" width="100" align="center" />
          <el-table-column label="理由" prop="reason" min-width="180" align="left" show-overflow-tooltip />
          <el-table-column label="承诺" prop="commitment" min-width="150" align="left" show-overflow-tooltip />
          <el-table-column label="状态" width="80" align="center">
            <template v-slot="scope">
              <el-tag :type="appStatusType(scope.row.status)" size="small">
                {{ appStatusLabel(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="时间" prop="createTime" width="170" align="center" />
          <el-table-column label="操作" width="120" align="center" v-if="isSuperAdmin">
            <template v-slot="scope">
              <el-button v-if="scope.row.status === 0" class="edit-btn" size="small" @click="openAppReview(scope.row)">审核</el-button>
              <span v-else class="super-badge">已处理</span>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-wrap">
          <el-pagination
            background
            layout="total, prev, pager, next"
            :total="appTotal"
            :page-size="appPageSize"
            :current-page="appPage"
            @current-change="onAppPageChange"
          />
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 查看消息 -->
    <el-dialog title="消息详情" v-model="msgVisible" width="500px">
      <div class="msg-detail">
        <div class="msg-field"><span class="label">标题：</span>{{ currentMsg.title }}</div>
        <div class="msg-field"><span class="label">发件人：</span>{{ currentMsg.fromUserName }}</div>
        <div class="msg-field"><span class="label">时间：</span>{{ currentMsg.createTime }}</div>
        <div class="msg-content">{{ currentMsg.content }}</div>
      </div>
      <template #footer>
        <el-button @click="msgVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 回复反馈 -->
    <el-dialog title="回复反馈" v-model="replyVisible" width="500px">
      <div class="reply-origin">
        <div class="msg-field"><span class="label">用户：</span>{{ replyTarget?.userName }}</div>
        <div class="msg-field"><span class="label">内容：</span>{{ replyTarget?.content }}</div>
      </div>
      <el-input v-model="replyContent" type="textarea" :rows="4" placeholder="请输入回复内容" />
      <template #footer>
        <el-button @click="replyVisible = false">取消</el-button>
        <el-button type="primary" :loading="replyLoading" @click="confirmReply">确定回复</el-button>
      </template>
    </el-dialog>

    <!-- 审核申请 -->
    <el-dialog title="审核申请" v-model="reviewVisible" width="520px">
      <div class="reply-origin">
        <div class="msg-field"><span class="label">申请人：</span>{{ reviewTarget?.userName }}</div>
        <div class="msg-field"><span class="label">理由：</span>{{ reviewTarget?.reason }}</div>
        <div class="msg-field"><span class="label">承诺：</span>{{ reviewTarget?.commitment }}</div>
        <div class="msg-field" v-if="reviewTarget?.phone"><span class="label">联系方式：</span>{{ reviewTarget?.phone }}</div>
        <div class="msg-field" v-if="reviewTarget?.email"><span class="label">邮箱：</span>{{ reviewTarget?.email }}</div>
      </div>
      <el-input v-model="reviewReply" type="textarea" :rows="3" placeholder="审核意见（可选）" />
      <template #footer>
        <el-button @click="reviewVisible = false">取消</el-button>
        <el-button type="danger" :loading="reviewLoading" @click="confirmReview('reject')">拒绝</el-button>
        <el-button type="primary" :loading="reviewLoading" @click="confirmReview('approve')">通过</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { HttpManager } from "@/api";

export default defineComponent({
  setup() {
    const userId = Number(sessionStorage.getItem("userId"));
    const isAdmin = Number(sessionStorage.getItem("roleId")) === 1;
    const isSuperAdmin = userId === 1;

    const activeTab = ref("inbox");

    // ---- 收件箱 ----
    const tableData = ref<any[]>([]);
    const total = ref(0);
    const page = ref(1);
    const pageSize = ref(20);
    const msgVisible = ref(false);
    const currentMsg = ref<any>({});

    async function loadMessages() {
      try {
        const res: any = await HttpManager.getMessageList(page.value, pageSize.value);
        if (res.success) {
          tableData.value = res.data?.items || [];
          total.value = res.data?.total || 0;
        }
      } catch { /* ignore */ }
    }

    function onPageChange(p: number) { page.value = p; loadMessages(); }

    async function viewMessage(row: any) {
      currentMsg.value = row;
      msgVisible.value = true;
      if (!row.isRead) {
        await HttpManager.markMessageRead(row.id);
        row.isRead = 1;
      }
    }

    // ---- 反馈管理 ----
    const feedbackData = ref<any[]>([]);
    const fbTotal = ref(0);
    const fbPage = ref(1);
    const fbPageSize = ref(20);
    const replyVisible = ref(false);
    const replyTarget = ref<any>(null);
    const replyContent = ref("");
    const replyLoading = ref(false);

    async function loadFeedback() {
      if (!isAdmin) return;
      try {
        const res: any = await HttpManager.getFeedbackList(fbPage.value, fbPageSize.value);
        if (res.success) {
          feedbackData.value = res.data?.items || [];
          fbTotal.value = res.data?.total || 0;
        }
      } catch { /* ignore */ }
    }

    function onFbPageChange(p: number) { fbPage.value = p; loadFeedback(); }

    function openReplyDialog(row: any) {
      replyTarget.value = row;
      replyContent.value = "";
      replyVisible.value = true;
    }

    async function confirmReply() {
      if (!replyContent.value.trim()) { ElMessage.warning("请输入回复内容"); return; }
      replyLoading.value = true;
      try {
        const res: any = await HttpManager.replyFeedback({
          id: replyTarget.value.id,
          replyContent: replyContent.value.trim()
        });
        if (res.success) {
          ElMessage.success("回复成功");
          replyVisible.value = false;
          loadFeedback();
        } else {
          ElMessage.error(res.message || "回复失败");
        }
      } catch { ElMessage.error("回复失败"); }
      replyLoading.value = false;
    }

    // ---- 申请管理 ----
    const applicationData = ref<any[]>([]);
    const appTotal = ref(0);
    const appPage = ref(1);
    const appPageSize = ref(20);
    const reviewVisible = ref(false);
    const reviewTarget = ref<any>(null);
    const reviewReply = ref("");
    const reviewLoading = ref(false);

    async function loadApplications() {
      if (!isSuperAdmin) return;
      try {
        const res: any = await HttpManager.getApplicationList(appPage.value, appPageSize.value);
        if (res.success) {
          applicationData.value = res.data?.items || [];
          appTotal.value = res.data?.total || 0;
        }
      } catch { /* ignore */ }
    }

    function onAppPageChange(p: number) { appPage.value = p; loadApplications(); }

    function openAppReview(row: any) {
      reviewTarget.value = row;
      reviewReply.value = "";
      reviewVisible.value = true;
    }

    async function confirmReview(action: string) {
      reviewLoading.value = true;
      try {
        const res: any = await HttpManager.reviewApplication({
          id: reviewTarget.value.id,
          action: action,
          replyContent: reviewReply.value.trim()
        });
        if (res.success) {
          ElMessage.success("操作成功");
          reviewVisible.value = false;
          loadApplications();
        } else {
          ElMessage.error(res.message || "操作失败");
        }
      } catch { ElMessage.error("操作失败"); }
      reviewLoading.value = false;
    }

    function appStatusType(status: number): string {
      if (status === 0) return 'warning';
      if (status === 1) return 'success';
      return 'danger';
    }
    function appStatusLabel(status: number): string {
      if (status === 0) return '待审核';
      if (status === 1) return '已通过';
      return '已拒绝';
    }

    function onTabChange() {
      if (activeTab.value === 'feedback') loadFeedback();
      if (activeTab.value === 'applications') loadApplications();
    }

    onMounted(() => {
      loadMessages();
      if (isAdmin) loadFeedback();
      if (isSuperAdmin) loadApplications();
    });

    return {
      activeTab, isAdmin, isSuperAdmin,
      tableData, total, page, pageSize, onPageChange, viewMessage,
      msgVisible, currentMsg,
      feedbackData, fbTotal, fbPage, fbPageSize, onFbPageChange,
      replyVisible, replyTarget, replyContent, replyLoading,
      openReplyDialog, confirmReply,
      applicationData, appTotal, appPage, appPageSize, onAppPageChange,
      reviewVisible, reviewTarget, reviewReply, reviewLoading,
      openAppReview, confirmReview, onTabChange,
      appStatusType, appStatusLabel,
    };
  }
});
</script>

<style scoped>
.inbox-container {
  padding: 20px;
  background: var(--bg-page);
  min-height: calc(100vh - 60px);
}
.handle-box { margin-bottom: 20px; }
.page-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}
.msg-title { font-size: 13px; color: var(--text-primary); }
.msg-title.unread { font-weight: 600; }
.unread-dot {
  display: inline-block;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #b74dff;
  margin-right: 6px;
  vertical-align: middle;
}
.pagination-wrap { margin-top: 16px; display: flex; justify-content: flex-end; }
.super-badge { font-size: 12px; color: var(--text-secondary); font-style: italic; }

.msg-detail, .reply-origin { margin-bottom: 16px; }
.msg-field { font-size: 14px; color: var(--text-primary); margin-bottom: 8px; }
.msg-field .label { color: var(--text-secondary); }
.msg-content {
  margin-top: 12px;
  padding: 12px;
  background: var(--bg-page);
  border-radius: 6px;
  font-size: 14px;
  color: var(--text-primary);
  line-height: 1.6;
  white-space: pre-wrap;
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
body.dark .msg-content { background: #1a1a2e; }

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
:deep(.el-dialog .el-button--primary) {
  background: #b74dff !important;
  border-color: #b74dff !important;
}
:deep(.el-dialog .el-button--primary:hover) {
  background: #c87aff !important;
  border-color: #c87aff !important;
}
:deep(.el-tabs__item.is-active) { color: #b74dff !important; }
:deep(.el-tabs__active-bar) { background: #b74dff !important; }
</style>
