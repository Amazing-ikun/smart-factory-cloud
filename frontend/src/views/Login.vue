<template>
  <div class="login-root">
    <!-- 背景渐变光带 -->
    <div class="glow-bar"></div>
    <!-- 右下角装饰圆环 -->
    <div class="ring ring-outer"></div>
    <div class="ring ring-inner"></div>

    <!-- ====== 初始落地页 ====== -->
    <transition name="fade">
      <div v-if="showLanding" class="landing" key="landing">
        <div class="landing-content">
          <h1 class="hero-title">
            Everything you need<br>
            to operate & manage<br>
            Smart Factory Cloud
          </h1>
          <p class="hero-sub">智能制造云工厂管理平台</p>
          <button class="join-btn" @click="switchToLogin">
            <span>JOIN US</span>
            <span class="arrow-icon">&nbsp;&rarr;</span>
          </button>
        </div>
      </div>
    </transition>

    <!-- ====== 登录页 ====== -->
    <transition name="slide">
      <div v-if="showLogin" class="login-panel" key="login">
        <div class="login-form">
          <h2 class="login-title">LOG IN TO THE SYSTEM</h2>
          <p class="login-welcome">欢迎来到智能制造云工厂管理平台</p>

          <el-form
            ref="formEl"
            :model="ruleForm"
            :rules="rules"
            label-position="top"
            class="login-el-form"
          >
            <el-form-item label="用户名" prop="username">
              <el-input
                v-model="ruleForm.username"
                placeholder="请输入用户名"
                @keyup.enter="submitForm"
              />
            </el-form-item>
            <el-form-item label="登录密码" prop="password">
              <el-input
                type="password"
                v-model="ruleForm.password"
                placeholder="请输入密码"
                @keyup.enter="submitForm"
                show-password
              />
            </el-form-item>

            <div class="form-footer">
              <label class="remember">
                <el-checkbox v-model="rememberPwd" size="small" />
                <span class="remember-text">记住密码</span>
              </label>
              <span class="forgot">忘记密码</span>
            </div>

            <el-button
              class="confirm-btn"
              type="primary"
              :loading="loading"
              @click="submitForm"
            >
              确认登录
            </el-button>
          </el-form>
        </div>
      </div>
    </transition>

    <!-- 注册对话框 -->
    <el-dialog title="用户注册" v-model="registerVisible" width="400px" top="25vh"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-form label-width="80px" :model="registerForm">
        <el-form-item label="用户名">
          <el-input v-model="registerForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="registerForm.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input type="password" v-model="registerForm.password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="确认密码">
          <el-input type="password" v-model="registerForm.confirmPassword" placeholder="请再次输入密码" />
        </el-form-item>
        <el-form-item label="角色">
          <el-radio-group v-model="registerForm.roleId">
            <el-radio :value="2">普通用户</el-radio>
            <el-radio :value="1">管理员</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="registerVisible = false">取消</el-button>
        <el-button type="primary" :loading="registerLoading" @click="handleRegister">注册</el-button>
      </template>
    </el-dialog>

    <!-- 滑块验证码 -->
    <CaptchaDialog
      v-if="captchaVisible"
      @verified="onCaptchaVerified"
      @close="captchaVisible = false"
    />
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, reactive } from "vue";
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { HttpManager } from "@/api/index";
import { imageUrl } from "@/api/request";
import CaptchaDialog from "@/components/CaptchaDialog.vue";

interface ResultResponse<T = any> {
  success: boolean;
  message: string;
  data?: T;
}

interface UserData {
  id: number;
  userName: string;
  userRealName?: string;
  roleId?: number;
  [key: string]: any;
}

export default defineComponent({
  name: 'Login',
  components: { CaptchaDialog },
  setup() {
    const router = useRouter();
    const loading = ref(false);
    const showLanding = ref(true);
    const showLogin = ref(false);
    const rememberPwd = ref(false);
    const registerVisible = ref(false);
    const registerLoading = ref(false);
    const captchaVisible = ref(false);
    const pendingLoginData = ref<UserData | null>(null);

    const ruleForm = reactive({
      username: '',
      password: ''
    });

    const rules = reactive({
      username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
      password: [{ required: true, message: "请输入密码", trigger: "blur" }],
    });

    const registerForm = reactive({
      username: '',
      realName: '',
      password: '',
      confirmPassword: '',
      roleId: 2,
    });

    const formEl = ref<any>(null);

    // ---- 记住密码 ----
    const savedPwd = localStorage.getItem("rememberPwd");
    if (savedPwd) {
      const creds = JSON.parse(savedPwd);
      ruleForm.username = creds.username || '';
      ruleForm.password = creds.password || '';
      rememberPwd.value = true;
    }

    function saveRememberPwd() {
      if (rememberPwd.value) {
        localStorage.setItem("rememberPwd", JSON.stringify({ username: ruleForm.username, password: ruleForm.password }));
      } else {
        localStorage.removeItem("rememberPwd");
      }
    }

    function switchToLogin() {
      showLanding.value = false;
      setTimeout(() => {
        showLogin.value = true;
      }, 400);
    }

    function doLogin(data: UserData) {
      sessionStorage.setItem("username", data.userName);
      sessionStorage.setItem("userId", String(data.id));
      sessionStorage.setItem("userRealName", data.userRealName || '');
      sessionStorage.setItem("roleId", String(data.roleId || 2));
      sessionStorage.setItem("userIntro", data.userIntro || '');
      if (data.userAvatar) {
        sessionStorage.setItem("userAvatar", imageUrl(data.userAvatar));
      }
      router.push('/Home/Info');
    }

    async function submitForm() {
      if (!formEl.value) return;
      try { await formEl.value.validate(); } catch { return; }
      if (loading.value) return;
      loading.value = true;

      try {
        const result = await HttpManager.userLogin({
          username: ruleForm.username.trim(),
          password: ruleForm.password.trim()
        }) as ResultResponse<UserData>;

        if (result.success && result.data) {
          // Credentials valid — store data temporarily and show captcha
          pendingLoginData.value = result.data;
          loading.value = false;
          captchaVisible.value = true;
        } else {
          registerVisible.value = true;
          loading.value = false;
        }
      } catch (error: any) {
        registerVisible.value = true;
        loading.value = false;
      }
    }

    function onCaptchaVerified() {
      if (pendingLoginData.value) {
        saveRememberPwd();
        ElMessage({ message: '登录成功', type: 'success', duration: 2000 });
        doLogin(pendingLoginData.value);
        pendingLoginData.value = null;
      }
    }

    async function handleRegister() {
      if (!registerForm.username.trim()) { ElMessage.warning('请输入用户名'); return; }
      if (!registerForm.password.trim()) { ElMessage.warning('请输入密码'); return; }
      if (registerForm.password !== registerForm.confirmPassword) { ElMessage.warning('两次密码输入不一致'); return; }
      registerLoading.value = true;
      try {
        const result = await HttpManager.userRegister({
          userName: registerForm.username.trim(),
          userRealName: registerForm.realName.trim() || registerForm.username.trim(),
          userPasswd: registerForm.password.trim(),
          roleId: registerForm.roleId,
          factoryId: 1,
        }) as ResultResponse<any>;
        if (result.success) {
          ElMessage.success('注册成功，请登录');
          registerVisible.value = false;
        } else {
          ElMessage.error(result.message || '注册失败');
        }
      } catch {
        ElMessage.error('注册失败，请检查网络连接');
      } finally {
        registerLoading.value = false;
      }
    }

    return {
      showLanding, showLogin,
      ruleForm, rules, formEl, loading, rememberPwd,
      captchaVisible, onCaptchaVerified,
      submitForm, switchToLogin, saveRememberPwd,
      registerVisible, registerLoading, registerForm, handleRegister,
    };
  }
});
</script>

<style scoped>
/* ========== 全局 ========== */
.login-root {
  position: relative;
  width: 100%;
  height: 100vh;
  background: #0F0F18;
  display: flex;
  align-items: center;
  overflow: hidden;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
}

/* ========== 右侧渐变光带 ========== */
.glow-bar {
  position: absolute;
  right: 0;
  top: 0;
  width: 260px;
  height: 100%;
  background: linear-gradient(180deg,
    #00d4b0 0%,
    #3b82f6 30%,
    #8b5cf6 55%,
    #d946ef 75%,
    #be185d 100%
  );
  filter: blur(120px);
  opacity: 0.35;
  pointer-events: none;
}

/* ========== 右下角装饰圆环 ========== */
.ring {
  position: absolute;
  right: 60px;
  bottom: 60px;
  border-radius: 50%;
  border: 1px solid rgba(180, 160, 255, 0.2);
  pointer-events: none;
}
.ring-outer {
  width: 300px;
  height: 300px;
}
.ring-inner {
  width: 220px;
  height: 220px;
  right: 100px;
  bottom: 100px;
}

/* ========== 落地页 ========== */
.landing {
  position: relative;
  z-index: 2;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
}

.landing-content {
  margin-left: clamp(60px, 10vw, 160px);
  max-width: 700px;
}

.hero-title {
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
  font-weight: 800;
  font-size: clamp(32px, 4.5vw, 56px);
  line-height: 1.25;
  color: #ffffff;
  margin: 0 0 32px 0;
  letter-spacing: -0.02em;
}

.hero-sub {
  font-size: 16px;
  color: #ffffff;
  margin: 0 0 36px 0;
  opacity: 0.7;
  letter-spacing: 0.04em;
}

.join-btn {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  padding: 16px 40px;
  background: #b74dff;
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 0.06em;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.2s, transform 0.15s;
}
.join-btn:hover {
  background: #a33ee8;
  transform: translateY(-1px);
}
.join-btn:active {
  transform: translateY(0);
}
.arrow-icon {
  font-size: 18px;
  letter-spacing: 0;
  line-height: 1;
}

/* ========== 登录面板 ========== */
.login-panel {
  position: relative;
  z-index: 2;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
}

.login-form {
  margin-left: clamp(60px, 10vw, 160px);
  max-width: 420px;
  width: 100%;
}

.login-title {
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
  font-weight: 800;
  font-size: 32px;
  margin: 0 0 8px 0;
  line-height: 1.2;
  background: linear-gradient(90deg, #60a5fa, #c084fc, #f472b6);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}

.login-welcome {
  font-size: 14px;
  color: #ffffff;
  opacity: 0.6;
  margin: 0 0 36px 0;
}

/* Element Plus 表单覆盖 */
.login-el-form {
  width: 100%;
}
.login-el-form :deep(.el-form-item) {
  margin-bottom: 22px;
}
.login-el-form :deep(.el-form-item__label) {
  color: rgba(255,255,255,0.7);
  font-size: 13px;
  line-height: 1;
  padding-bottom: 8px;
}
.login-el-form :deep(.el-input__wrapper) {
  background: transparent;
  border: 1px solid rgba(255,255,255,0.12);
  border-radius: 6px;
  box-shadow: none;
  padding: 4px 12px;
}
.login-el-form :deep(.el-input__wrapper:hover) {
  border-color: rgba(255,255,255,0.25);
}
.login-el-form :deep(.el-input__wrapper.is-focus) {
  border-color: #b74dff;
  box-shadow: 0 0 0 1px rgba(183, 77, 255, 0.2);
}
.login-el-form :deep(.el-input__inner) {
  color: #fff;
  height: 42px;
}
.login-el-form :deep(.el-input__inner::placeholder) {
  color: rgba(255,255,255,0.25);
}
.login-el-form :deep(.el-input__suffix) .el-icon {
  color: rgba(255,255,255,0.35);
}

/* 底部辅助栏 */
.form-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: -8px;
  margin-bottom: 28px;
}
.remember {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
}
.remember-text {
  font-size: 13px;
  color: rgba(255,255,255,0.45);
}
.forgot {
  font-size: 13px;
  color: rgba(255,255,255,0.45);
  cursor: pointer;
}
.forgot:hover {
  color: rgba(255,255,255,0.7);
}

/* 确认按钮 */
.confirm-btn {
  width: 100% !important;
  height: 48px !important;
  background: #b74dff !important;
  border: none !important;
  border-radius: 6px !important;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 0.04em;
  color: #fff !important;
  transition: background 0.2s;
}
.confirm-btn:hover {
  background: #a33ee8 !important;
}

/* ========== 过渡动画 ========== */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.5s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.slide-enter-active {
  transition: opacity 0.6s ease, transform 0.6s ease;
}
.slide-enter-from {
  opacity: 0;
  transform: translateY(30px);
}

/* 暗色模式下 checkbox 样式 */
.login-el-form :deep(.el-checkbox__inner) {
  background: transparent;
  border-color: rgba(255,255,255,0.25);
}
.login-el-form :deep(.el-checkbox__inner:hover) {
  border-color: rgba(255,255,255,0.5);
}
.login-el-form :deep(.el-checkbox.is-checked .el-checkbox__inner) {
  background: #b74dff;
  border-color: #b74dff;
}
</style>
