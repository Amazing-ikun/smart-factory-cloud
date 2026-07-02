<template>
  <div class="profile-container">
    <div class="profile-card">
      <!-- 头像区 + 简介 -->
      <div class="avatar-section">
        <div class="avatar-wrapper" @click="showAvatarActions">
          <div class="avatar">
            <img :src="currentAvatar" />
            <div class="avatar-overlay">
              <el-icon><Camera /></el-icon>
            </div>
          </div>
        </div>
        <div class="user-info">
          <h2>{{ userRealName || userName }}</h2>
          <p class="username">@{{ userName }}</p>
          <p class="role-tag" :class="isAdmin ? 'admin' : 'user'">{{ isAdmin ? '管理员' : '普通用户' }}</p>
        </div>
        <div class="inbox-btn-area" v-if="!isAdmin">
          <el-badge :value="unreadCount" :hidden="unreadCount === 0">
            <el-button class="inbox-icon-btn" @click="goToInbox">
              <el-icon><Message /></el-icon>
            </el-button>
          </el-badge>
        </div>
      </div>

      <el-divider />

      <!-- 非管理员：申请管理员（突出展示） -->
      <template v-if="!isAdmin">
        <div class="apply-promo">
          <div class="apply-promo-icon">&#9733;</div>
          <div class="apply-promo-text">
            <div class="apply-promo-title">申请成为管理员</div>
            <div class="apply-promo-desc">获取更高系统管理权限，管理用户、查看日志等</div>
          </div>
          <el-button class="apply-promo-btn" @click="openApplicationDialog">立即申请</el-button>
        </div>

        <!-- 申请记录 -->
        <div v-if="myApplications.length > 0" class="apply-records">
          <div class="apply-records-title">我的申请记录</div>
          <div v-for="item in myApplications" :key="item.id" class="list-item">
            <div class="list-item-top">
              <el-tag :type="appStatusType(item.status)" size="small">
                {{ appStatusLabel(item.status) }}
              </el-tag>
              <span class="list-time">{{ formatTime(item.createTime) }}</span>
            </div>
            <div class="list-content">理由：{{ item.reason }}</div>
            <div v-if="item.replyContent" class="list-reply">
              <span class="reply-label">审核回复：</span>{{ item.replyContent }}
            </div>
          </div>
        </div>

        <el-divider />
      </template>

      <!-- 简介 -->
      <div class="intro-section">
        <div class="intro-header">
          <span class="section-label">简介</span>
          <el-button v-if="!editingIntro" text size="small" @click="startEditIntro">编辑</el-button>
        </div>
        <div v-if="!editingIntro" class="intro-text" @click="startEditIntro">
          {{ userIntro || '该用户很懒，还没有添加简介' }}
        </div>
        <div v-else class="intro-edit">
          <el-input v-model="editIntroValue" type="textarea" :rows="2" maxlength="100" show-word-limit />
          <div class="intro-actions">
            <el-button size="small" type="primary" @click="saveIntro" :loading="savingIntro">保存</el-button>
            <el-button size="small" @click="cancelEditIntro">取消</el-button>
          </div>
        </div>
      </div>

      <el-divider />

      <!-- 详细信息 -->
      <div class="detail-section">
        <div class="detail-item">
          <span class="label">用户ID</span>
          <span class="value">{{ userId }}</span>
        </div>
        <div class="detail-item">
          <span class="label">用户名</span>
          <span class="value">{{ userName }}</span>
        </div>
        <div class="detail-item">
          <span class="label">姓名</span>
          <span class="value">{{ userRealName || '未设置' }}</span>
        </div>
        <div class="detail-item">
          <span class="label">角色</span>
          <span class="value">{{ isAdmin ? '管理员' : '普通用户' }}</span>
        </div>
      </div>

      <!-- 非管理员：反馈 & 申请管理 -->
      <template v-if="!isAdmin">
        <el-divider />

        <!-- 反馈 -->
        <div class="action-section">
          <div class="section-header">
            <span class="section-label">意见反馈</span>
            <el-button size="small" type="primary" @click="openFeedbackDialog">提交反馈</el-button>
          </div>
          <div class="section-list" v-if="myFeedback.length > 0">
            <div v-for="item in myFeedback" :key="item.id" class="list-item">
              <div class="list-item-top">
                <el-tag :type="item.feedbackType === 'bug' ? 'danger' : 'info'" size="small">
                  {{ item.feedbackType === 'bug' ? 'Bug' : '建议' }}
                </el-tag>
                <span class="list-time">{{ formatTime(item.createTime) }}</span>
              </div>
              <div class="list-content">{{ item.content }}</div>
              <div v-if="item.replyContent" class="list-reply">
                <span class="reply-label">管理员回复：</span>{{ item.replyContent }}
              </div>
            </div>
          </div>
          <el-empty v-else description="暂无反馈记录" :image-size="50" />
        </div>

      </template>
    </div>

    <!-- 头像操作 Popover -->
    <el-dialog title="头像管理" v-model="avatarDialogVisible" width="520px" top="10vh">
      <div class="avatar-dialog-body">
        <div class="current-avatar-preview">
          <img :src="currentAvatar" />
        </div>
        <el-divider>历史头像</el-divider>
        <div class="history-toolbar" v-if="historyList.length > 0">
          <el-checkbox v-model="selectAll" @change="toggleSelectAll">全选</el-checkbox>
          <el-button size="small" type="danger" :disabled="selectedIds.length === 0" @click="batchDelete">
            删除选中 ({{ selectedIds.length }})
          </el-button>
        </div>
        <div class="history-grid" v-if="historyList.length > 0">
          <div v-for="item in historyList" :key="item.id" class="history-item">
            <el-checkbox v-model="item.checked" class="history-checkbox" />
            <img :src="displayUrl(item.avatarUrl)" class="history-img" />
            <div class="history-info">
              <span class="history-time">{{ formatTime(item.createTime) }}</span>
            </div>
            <el-button class="history-del" size="small" circle @click="deleteSingle(item.id)">
              <el-icon><Delete /></el-icon>
            </el-button>
          </div>
        </div>
        <el-empty v-else description="暂无历史头像" :image-size="80" />
      </div>
      <template #footer>
        <el-upload :show-file-list="false" :before-upload="handleUpload" accept="image/*">
          <el-button type="primary" :loading="uploading">从本地选择</el-button>
        </el-upload>
      </template>
    </el-dialog>

    <!-- 反馈对话框 -->
    <el-dialog title="提交反馈" v-model="feedbackVisible" width="520px" :close-on-click-modal="false">
      <el-form label-position="top">
        <el-form-item label="反馈类型">
          <el-radio-group v-model="feedbackForm.type">
            <el-radio value="suggestion">功能建议</el-radio>
            <el-radio value="bug">Bug 反馈</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="反馈内容">
          <el-input
            v-model="feedbackForm.content"
            type="textarea"
            :rows="5"
            maxlength="2000"
            show-word-limit
            placeholder="请详细描述您的建议或发现的Bug（限2000字）"
          />
        </el-form-item>
        <el-form-item label="附件">
          <div class="upload-area">
            <input type="file" ref="feedbackFileRef" multiple accept=".txt,image/*" style="display:none" @change="onFeedbackFilesChange" />
            <el-button size="small" @click="triggerFileSelect">选择文件</el-button>
            <span class="upload-hint">支持 .txt 和图片格式</span>
          </div>
          <div v-if="feedbackFiles.length > 0" class="file-list">
            <div v-for="(f, i) in feedbackFiles" :key="i" class="file-item">
              <span class="file-name">{{ f.name }}</span>
              <el-tag closable size="small" @close="removeFeedbackFile(i)">移除</el-tag>
            </div>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="feedbackVisible = false">取消</el-button>
        <el-button type="primary" :loading="feedbackLoading" @click="submitFeedback">提交</el-button>
      </template>
    </el-dialog>

    <!-- 申请管理员对话框 -->
    <el-dialog title="申请成为管理员" v-model="appDialogVisible" width="520px" :close-on-click-modal="false">
      <el-form label-position="top">
        <el-form-item label="用户名" required>
          <el-input v-model="appForm.userName" disabled :placeholder="userName" />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="appForm.userRealName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="申请理由" required>
          <el-input v-model="appForm.reason" type="textarea" :rows="3" placeholder="请说明申请成为管理员的原因" />
        </el-form-item>
        <el-form-item label="个人承诺">
          <el-input v-model="appForm.commitment" type="textarea" :rows="3" placeholder="可选：做出您的承诺" />
        </el-form-item>
        <el-form-item label="联系方式">
          <el-input v-model="appForm.phone" placeholder="可选：手机号" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="appForm.email" placeholder="可选：邮箱地址" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="appDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="appLoading" @click="submitApplication">提交申请</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from 'vue-router';
import { useStore } from "vuex";
import { ElMessage, ElMessageBox } from "element-plus";
import { Camera, Delete, Message } from "@element-plus/icons-vue";
import { HttpManager } from "@/api";
import { imageUrl } from "@/api/request";

const router = useRouter();
const store = useStore();
const userPic = computed(() => store.getters.userPic);

const userId = Number(sessionStorage.getItem("userId")) || 0;
const userName = sessionStorage.getItem("username") || "-";
const userRealName = sessionStorage.getItem("userRealName") || "";
const isAdmin = Number(sessionStorage.getItem("roleId")) === 1;

// ---- 未读消息 ----
const unreadCount = ref(0);
async function loadUnread() {
  try {
    const res: any = await HttpManager.getUnreadCount();
    if (res.success) unreadCount.value = res.data || 0;
  } catch { /* ignore */ }
}
function goToInbox() {
  router.push('/Home/inbox');
}

// ---- Avatar ----
const avatarDialogVisible = ref(false);
const uploading = ref(false);
const historyList = ref<any[]>([]);
const selectAll = ref(false);
const selectedIds = computed(() =>
  historyList.value.filter((i: any) => i.checked).map((i: any) => i.id)
);

const currentAvatar = computed(() => {
  const stored = sessionStorage.getItem("userAvatar");
  if (stored) return imageUrl(stored);
  return userPic.value;
});

function displayUrl(url: string) { return imageUrl(url); }

function showAvatarActions() {
  avatarDialogVisible.value = true;
  loadHistory();
}

async function loadHistory() {
  try {
    const res: any = await HttpManager.getAvatarHistory(userId);
    if (res.success) {
      historyList.value = (res.data || []).map((i: any) => ({ ...i, checked: false }));
    }
  } catch { /* ignore */ }
  selectAll.value = false;
}

function toggleSelectAll(val: boolean) {
  historyList.value.forEach((i: any) => (i.checked = val));
}

async function deleteSingle(id: number) {
  try {
    const res: any = await HttpManager.deleteAvatarHistory(id);
    if (res.success) { ElMessage.success("已删除"); await loadHistory(); }
  } catch { ElMessage.error("删除失败"); }
}

async function batchDelete() {
  const ids = selectedIds.value;
  if (ids.length === 0) return;
  try {
    const res: any = await HttpManager.deleteAvatarHistoryBatch(ids);
    if (res.success) { ElMessage.success(`已删除 ${ids.length} 条记录`); await loadHistory(); }
  } catch { ElMessage.error("删除失败"); }
}

async function handleUpload(file: File): Promise<boolean> {
  const validTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
  if (!validTypes.includes(file.type)) { ElMessage.warning('仅支持 JPG/PNG/GIF/WebP 格式'); return false; }
  if (file.size > 5 * 1024 * 1024) { ElMessage.warning('图片不能超过 5MB'); return false; }
  uploading.value = true;
  try {
    const res: any = await HttpManager.uploadUserAvatar(userId, file);
    if (res.success) {
      ElMessage.success("头像更新成功");
      const fullUrl = imageUrl(res.data);
      sessionStorage.setItem("userAvatar", fullUrl);
      store.commit("setUserPic", fullUrl);
      await loadHistory();
    } else { ElMessage.error(res.message || "上传失败"); }
  } catch { ElMessage.error("上传失败"); }
  uploading.value = false;
  return false;
}

// ---- Intro ----
const editingIntro = ref(false);
const savingIntro = ref(false);
const userIntro = ref(sessionStorage.getItem("userIntro") || "");
const editIntroValue = ref("");

function startEditIntro() { editIntroValue.value = userIntro.value; editingIntro.value = true; }
function cancelEditIntro() { editingIntro.value = false; }

async function saveIntro() {
  if (!editIntroValue.value.trim()) { ElMessage.warning("简介不能为空"); return; }
  savingIntro.value = true;
  try {
    const res: any = await HttpManager.updateUserIntro(userId, editIntroValue.value.trim());
    if (res.success) {
      userIntro.value = editIntroValue.value.trim();
      sessionStorage.setItem("userIntro", userIntro.value);
      editingIntro.value = false;
      ElMessage.success("简介更新成功");
    } else { ElMessage.error(res.message || "更新失败"); }
  } catch { ElMessage.error("更新失败"); }
  savingIntro.value = false;
}

// ---- Feedback ----
const feedbackVisible = ref(false);
const feedbackLoading = ref(false);
const feedbackForm = ref({ type: 'suggestion', content: '' });
const feedbackFiles = ref<File[]>([]);
const feedbackFileRef = ref<HTMLInputElement | null>(null);
const myFeedback = ref<any[]>([]);

function openFeedbackDialog() {
  feedbackForm.value = { type: 'suggestion', content: '' };
  feedbackFiles.value = [];
  feedbackVisible.value = true;
}

function triggerFileSelect() { feedbackFileRef.value?.click(); }

function onFeedbackFilesChange(e: Event) {
  const input = e.target as HTMLInputElement;
  if (input.files) {
    for (const f of Array.from(input.files)) {
      feedbackFiles.value.push(f);
    }
  }
  input.value = '';
}

function removeFeedbackFile(i: number) { feedbackFiles.value.splice(i, 1); }

async function submitFeedback() {
  if (!feedbackForm.value.content.trim()) { ElMessage.warning("请输入反馈内容"); return; }
  feedbackLoading.value = true;
  try {
    let attachments = "";
    if (feedbackFiles.value.length > 0) {
      const paths: string[] = [];
      for (const f of feedbackFiles.value) {
        const res: any = await HttpManager.uploadFeedbackFile(f);
        if (res.success) paths.push(res.data);
      }
      attachments = paths.join(",");
    }
    const res: any = await HttpManager.addFeedback({
      content: feedbackForm.value.content.trim(),
      feedbackType: feedbackForm.value.type,
      attachments
    });
    if (res.success) {
      ElMessage.success("提交成功");
      feedbackVisible.value = false;
      loadMyFeedback();
    } else { ElMessage.error(res.message || "提交失败"); }
  } catch { ElMessage.error("提交失败"); }
  feedbackLoading.value = false;
}

async function loadMyFeedback() {
  try {
    const res: any = await HttpManager.getMyFeedback();
    if (res.success) myFeedback.value = res.data || [];
  } catch { /* ignore */ }
}

// ---- Admin Application ----
const appDialogVisible = ref(false);
const appLoading = ref(false);
const appForm = ref({ userName: '', userRealName: '', reason: '', commitment: '', phone: '', email: '' });
const myApplications = ref<any[]>([]);

function openApplicationDialog() {
  appForm.value = {
    userName: userName,
    userRealName: userRealName,
    reason: '',
    commitment: '',
    phone: '',
    email: ''
  };
  appDialogVisible.value = true;
}

async function submitApplication() {
  if (!appForm.value.reason.trim()) { ElMessage.warning("请填写申请理由"); return; }
  appLoading.value = true;
  try {
    const res: any = await HttpManager.addApplication({
      userName: userName,
      userRealName: appForm.value.userRealName,
      reason: appForm.value.reason.trim(),
      commitment: appForm.value.commitment.trim(),
      phone: appForm.value.phone.trim(),
      email: appForm.value.email.trim()
    });
    if (res.success) {
      ElMessage.success("申请已提交，请等待审核");
      appDialogVisible.value = false;
      loadMyApplications();
    } else { ElMessage.error(res.message || "提交失败"); }
  } catch { ElMessage.error("提交失败"); }
  appLoading.value = false;
}

async function loadMyApplications() {
  try {
    const res: any = await HttpManager.getMyApplication();
    if (res.success) myApplications.value = res.data || [];
  } catch { /* ignore */ }
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

function formatTime(t: string) {
  if (!t) return "";
  return t.substring(0, 19).replace("T", " ");
}

onMounted(() => {
  if (!isAdmin) {
    loadMyFeedback();
    loadMyApplications();
    loadUnread();
  }
});
</script>

<style scoped>
.profile-container {
  padding: 20px;
  background: var(--bg-page);
  min-height: calc(100vh - 60px);
  display: flex;
  justify-content: center;
}
.profile-card {
  width: 600px;
  background: var(--bg-card);
  border-radius: 12px;
  box-shadow: var(--shadow-card);
  padding: 32px;
  margin-top: 20px;
  align-self: flex-start;
}

/* ====== 头像 ====== */
.avatar-wrapper { cursor: pointer; }
.avatar {
  position: relative;
  width: 80px;
  height: 80px;
  border-radius: 50%;
  overflow: hidden;
}
.avatar img {
  width: 100%; height: 100%; object-fit: cover;
  border-radius: 50%; border: 3px solid var(--border-color); display: block;
}
.avatar-overlay {
  position: absolute; inset: 0; background: rgba(0,0,0,0.4);
  display: flex; align-items: center; justify-content: center;
  color: #fff; font-size: 22px; opacity: 0;
  transition: opacity 0.3s; border-radius: 50%;
}
.avatar-wrapper:hover .avatar-overlay { opacity: 1; }
.avatar-section {
  display: flex; align-items: center; gap: 24px;
  position: relative;
}
.user-info h2 { font-size: 22px; color: var(--text-primary); margin: 0 0 4px 0; }
.user-info .username { font-size: 14px; color: var(--text-secondary); margin: 0 0 8px 0; }
.role-tag { display: inline-block; font-size: 12px; padding: 2px 12px; border-radius: 12px; margin: 0; }
.role-tag.admin { background: #ecf5ff; color: #409eff; }
.role-tag.user { background: #f0f9eb; color: #67c23a; }
body.dark .role-tag.admin { background: #1a2a4a; color: #30a4fc; }
body.dark .role-tag.user { background: #1a3a1a; color: #67c23a; }

.inbox-btn-area { margin-left: auto; }
.inbox-icon-btn {
  background: transparent !important;
  border: 1px solid var(--border-color) !important;
  font-size: 18px !important;
  padding: 8px !important;
}
.inbox-icon-btn:hover {
  border-color: #b74dff !important;
  color: #b74dff !important;
}

/* ====== 简介 ====== */
.intro-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
.section-label { font-size: 14px; font-weight: 600; color: var(--text-primary); }
.intro-text {
  font-size: 14px; color: var(--text-secondary); line-height: 1.6; cursor: pointer;
  padding: 8px 12px; border-radius: 6px; background: var(--bg-page);
}
.intro-text:hover { background: var(--border-color); }
.intro-actions { display: flex; gap: 8px; margin-top: 8px; }

/* ====== 详细信息 ====== */
.detail-section { display: flex; flex-direction: column; gap: 16px; }
.detail-item { display: flex; justify-content: space-between; align-items: center; padding: 8px 0; }
.detail-item .label { font-size: 14px; color: var(--text-secondary); }
.detail-item .value { font-size: 14px; color: var(--text-primary); font-weight: 500; }

/* ====== 操作区块 ====== */
.action-section { }
.section-header {
  display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px;
}
.section-header :deep(.el-button--primary) {
  background: #b74dff !important; border-color: #b74dff !important;
}
.section-header :deep(.el-button--primary:hover) {
  background: #c87aff !important; border-color: #c87aff !important;
}
.section-list { display: flex; flex-direction: column; gap: 8px; }
.list-item {
  padding: 10px 12px; border-radius: 8px;
  background: var(--bg-page); transition: background 0.2s;
}
.list-item:hover { background: var(--border-color); }
.list-item-top { display: flex; align-items: center; gap: 8px; margin-bottom: 6px; }
.list-time { font-size: 12px; color: var(--text-secondary); margin-left: auto; }
.list-content { font-size: 13px; color: var(--text-primary); line-height: 1.5; }
.list-reply {
  margin-top: 6px; font-size: 13px; color: var(--text-secondary);
  padding: 6px 8px; background: var(--bg-card); border-radius: 4px;
}
.reply-label { color: #b74dff; font-weight: 500; }

/* ====== 反馈对话框 ====== */
.upload-area { display: flex; align-items: center; gap: 8px; }
.upload-hint { font-size: 12px; color: var(--text-secondary); }
.file-list { display: flex; flex-direction: column; gap: 4px; margin-top: 8px; }
.file-item { display: flex; align-items: center; gap: 8px; }
.file-name { font-size: 13px; color: var(--text-primary); }

/* ====== 申请管理员推广卡 ====== */
.apply-promo {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px 24px;
  background: linear-gradient(135deg, rgba(183, 77, 255, 0.08), rgba(96, 165, 250, 0.06));
  border: 1px solid rgba(183, 77, 255, 0.2);
  border-radius: 12px;
  margin-bottom: 12px;
}
.apply-promo-icon {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: linear-gradient(135deg, #b74dff, #60a5fa);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: #fff;
  flex-shrink: 0;
}
.apply-promo-text { flex: 1; min-width: 0; }
.apply-promo-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 2px;
}
.apply-promo-desc {
  font-size: 12px;
  color: var(--text-secondary);
}
.apply-promo-btn {
  background: #b74dff !important;
  color: #fff !important;
  border: none !important;
  padding: 10px 22px !important;
  font-size: 14px !important;
  font-weight: 600 !important;
  border-radius: 6px !important;
  flex-shrink: 0;
  letter-spacing: 0.04em;
}
.apply-promo-btn:hover {
  background: #c87aff !important;
}
.apply-records { margin-top: 8px; }
.apply-records-title {
  font-size: 13px;
  font-weight: 500;
  color: var(--text-secondary);
  margin-bottom: 8px;
}
body.dark .apply-promo {
  background: linear-gradient(135deg, rgba(183, 77, 255, 0.12), rgba(96, 165, 250, 0.08));
  border-color: rgba(183, 77, 255, 0.25);
}

/* ====== 头像弹窗 ====== */
.current-avatar-preview { display: flex; justify-content: center; margin-bottom: 8px; }
.current-avatar-preview img {
  width: 120px; height: 120px; border-radius: 50%; object-fit: cover;
  border: 3px solid var(--border-color);
}
.history-toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
.history-grid { display: flex; flex-direction: column; gap: 8px; max-height: 300px; overflow-y: auto; }
.history-item {
  display: flex; align-items: center; gap: 12px; padding: 8px 12px;
  border-radius: 8px; background: var(--bg-page); transition: background 0.2s;
}
.history-item:hover { background: var(--border-color); }
.history-checkbox { flex-shrink: 0; }
.history-img {
  width: 48px; height: 48px; border-radius: 50%; object-fit: cover;
  flex-shrink: 0; border: 2px solid var(--border-color);
}
.history-info { flex: 1; min-width: 0; }
.history-time { font-size: 12px; color: var(--text-secondary); }
.history-del { flex-shrink: 0; }
body.dark .history-item { background: #1a1a2e; }
body.dark .history-item:hover { background: #2a2a4a; }
body.dark .list-item { background: #1a1a2e; }
body.dark .list-item:hover { background: #2a2a4a; }
body.dark .list-reply { background: #151528; }

:deep(.el-dialog) { --el-dialog-bg-color: var(--bg-card); }
:deep(.el-dialog .el-button--primary) {
  background: #b74dff !important; border-color: #b74dff !important;
}
:deep(.el-dialog .el-button--primary:hover) {
  background: #c87aff !important; border-color: #c87aff !important;
}
</style>
