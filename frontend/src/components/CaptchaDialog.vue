<template>
  <el-dialog
    title="安全验证"
    v-model="dialogVisible"
    width="420px"
    top="18vh"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
    :show-close="true"
    @close="onClose"
    destroy-on-close
  >
    <div class="captcha-body">
      <!-- 图片区 -->
      <div class="captcha-image-area" ref="imageAreaRef">
        <div v-if="loading" class="captcha-loading">加载验证码中...</div>
        <div v-else-if="loadError" class="captcha-error">
          验证码加载失败
          <el-button size="small" @click="loadCaptcha" style="margin-top:6px">重新加载</el-button>
        </div>
        <template v-else>
          <img :src="captchaData.bgImage" class="captcha-bg" />
          <img
            v-if="captchaData.pieceImage"
            :src="captchaData.pieceImage"
            class="captcha-piece"
            :class="{ shake: isShaking }"
            :style="pieceStyle"
          />
        </template>
      </div>

      <!-- 提示条 -->
      <div class="captcha-hint">
        <span v-if="!isVerified && !isFailed" class="hint-text">
          {{ hintText }}
        </span>
        <span v-if="isVerified" class="hint-success">验证通过</span>
        <span v-if="isFailed" class="hint-fail">
          验证失败，剩余 {{ remainingAttempts }} 次机会
          <span v-if="remainingAttempts <= 0">，正在刷新...</span>
        </span>
      </div>

      <!-- 滑块 -->
      <div
        class="slider-track"
        ref="sliderTrackRef"
        @mousedown="onSliderDown"
        @touchstart.prevent="onSliderDown"
      >
        <div class="slider-bg" ref="sliderBgRef">
          <div class="slider-progress" :style="{ width: sliderProgress + '%' }"></div>
        </div>
        <div
          class="slider-thumb"
          :class="{ dragging: isDragging, success: isVerified, fail: isFailed }"
          :style="{ left: sliderThumbLeft + 'px' }"
          @mousedown.stop="onSliderDown"
          @touchstart.stop.prevent="onSliderDown"
        >
          <span v-if="!isVerified && !isFailed">&#9654;</span>
          <span v-if="isVerified">&#10003;</span>
          <span v-if="isFailed">&#10007;</span>
        </div>
        <span class="slider-text" v-if="!isDragging && !isVerified && !isFailed">请拖动滑块完成拼图</span>
      </div>
    </div>
  </el-dialog>
</template>

<script lang="ts">
import { defineComponent, ref, computed, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { HttpManager } from "@/api";

export default defineComponent({
  name: "CaptchaDialog",
  emits: ["verified", "close"],
  setup(props, { emit }) {
    const dialogVisible = ref(true);
    const loading = ref(true);
    const loadError = ref(false);

    const captchaData = ref<any>({
      bgImage: "",
      pieceImage: "",
      token: "",
      pieceBaseSize: 36,
      maxAmplitude: 10,
      imageWidth: 340,
      imageHeight: 200,
    });

    const sliderTrackRef = ref<HTMLElement | null>(null);
    const isDragging = ref(false);
    const sliderPosition = ref(0);
    const sliderMax = ref(280);

    const isVerified = ref(false);
    const isFailed = ref(false);
    const isShaking = ref(false);
    const remainingAttempts = ref(3);
    const failCount = ref(0);

    const pieceStyle = computed(() => {
      if (!captchaData.value.pieceImage) return {};
      return {
        left: sliderPosition.value + "px",
        top: (captchaData.value.targetY || 0) + "px",
      };
    });

    const sliderThumbLeft = computed(() => sliderPosition.value);
    const sliderProgress = computed(() => {
      if (sliderMax.value <= 0) return 0;
      return (sliderPosition.value / sliderMax.value) * 100;
    });

    const hintText = computed(() => {
      if (!captchaData.value.shapeType) return "拖动滑块完成拼图验证";
      return {
        triangle: "按住滑块拖动完成三角拼图验证",
        star: "按住滑块拖动完成星芒拼图验证",
        puzzle: "按住滑块拖动完成拼图形验证",
      }[captchaData.value.shapeType] || "拖动滑块完成拼图验证";
    });

    async function loadCaptcha() {
      loading.value = true;
      loadError.value = false;
      isVerified.value = false;
      isFailed.value = false;
      isShaking.value = false;
      isDragging.value = false;
      sliderPosition.value = 0;
      failCount.value = 0;
      remainingAttempts.value = 3;

      try {
        const res: any = await HttpManager.getCaptcha();
        if (res.success && res.data) {
          captchaData.value = res.data;
          const pw = res.data.pieceBaseSize + res.data.maxAmplitude;
          sliderMax.value = Math.max(res.data.imageWidth - pw - 4, 10);
        } else {
          loadError.value = true;
        }
      } catch {
        loadError.value = true;
      } finally {
        loading.value = false;
      }
    }

    function getClientX(e: MouseEvent | TouchEvent): number {
      if ('touches' in e) return e.touches[0].clientX;
      return e.clientX;
    }

    function onSliderDown(e: MouseEvent | TouchEvent) {
      if (isVerified.value || isFailed.value) return;
      isDragging.value = true;
      const startClientX = getClientX(e);
      const startPos = sliderPosition.value;

      function onMove(ev: MouseEvent | TouchEvent) {
        const cx = 'touches' in ev ? ev.touches[0].clientX : ev.clientX;
        const delta = cx - startClientX;
        sliderPosition.value = Math.max(0, Math.min(sliderMax.value, startPos + delta));
      }

      function onUp() {
        isDragging.value = false;
        document.removeEventListener("mousemove", onMove);
        document.removeEventListener("mouseup", onUp);
        document.removeEventListener("touchmove", onMove);
        document.removeEventListener("touchend", onUp);
        verifyPosition();
      }

      document.addEventListener("mousemove", onMove);
      document.addEventListener("mouseup", onUp);
      document.addEventListener("touchmove", onMove, { passive: true });
      document.addEventListener("touchend", onUp);
    }

    async function verifyPosition() {
      const token = captchaData.value.token;
      const x = Math.round(sliderPosition.value);

      try {
        const res: any = await HttpManager.verifyCaptcha({ token, x });
        if (res.success) {
          isVerified.value = true;
          ElMessage.success("验证成功");
          setTimeout(() => {
            emit("verified", captchaData.value.token);
            dialogVisible.value = false;
          }, 600);
        } else {
          failCaptcha(res.data?.remainingAttempts ?? 0);
        }
      } catch {
        failCaptcha(0);
      }
    }

    function failCaptcha(remaining: number) {
      isFailed.value = true;
      isShaking.value = true;
      remainingAttempts.value = remaining;
      failCount.value++;

      setTimeout(() => {
        isShaking.value = false;
        if (remaining <= 0 || failCount.value >= 3) {
          // Auto reload after 3 failures
          setTimeout(() => {
            loadCaptcha();
          }, 800);
        } else {
          sliderPosition.value = 0;
          isFailed.value = false;
        }
      }, 600);
    }

    function onClose() {
      emit("close");
    }

    onMounted(() => { loadCaptcha(); });

    return {
      dialogVisible, loading, loadError,
      captchaData,
      sliderTrackRef,
      isDragging,
      sliderPosition,
      sliderMax,
      isVerified,
      isFailed,
      isShaking,
      remainingAttempts,
      pieceStyle,
      sliderThumbLeft,
      sliderProgress,
      hintText,
      loadCaptcha,
      onSliderDown,
      onClose,
    };
  },
});
</script>

<style scoped>
.captcha-body {
  padding: 0 4px;
}

/* ====== 图片区 ====== */
.captcha-image-area {
  position: relative;
  width: 340px;
  height: 200px;
  overflow: hidden;
  border-radius: 6px;
  background: #1a1a2e;
  margin: 0 auto 8px auto;
}
.captcha-loading {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: rgba(255,255,255,0.5);
  font-size: 14px;
}
.captcha-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: rgba(255,255,255,0.5);
  font-size: 14px;
}
.captcha-bg {
  width: 340px;
  height: 200px;
  display: block;
  user-select: none;
  -webkit-user-drag: none;
}
.captcha-piece {
  position: absolute;
  left: 0;
  top: 0;
  pointer-events: none;
  user-select: none;
  -webkit-user-drag: none;
}

/* ====== 提示条 ====== */
.captcha-hint {
  font-size: 13px;
  padding: 4px 2px 8px 2px;
  text-align: center;
  min-height: 22px;
}
.hint-text { color: var(--text-secondary); }
.hint-success { color: #67c23a; font-weight: 500; }
.hint-fail { color: #f56c6c; font-weight: 500; }

/* ====== 滑块 ====== */
.slider-track {
  position: relative;
  width: 340px;
  height: 42px;
  background: var(--bg-page);
  border-radius: 6px;
  border: 1px solid var(--border-color);
  cursor: pointer;
  user-select: none;
  margin: 0 auto;
}
.slider-bg {
  position: absolute;
  inset: 0;
  border-radius: 6px;
  overflow: hidden;
}
.slider-progress {
  height: 100%;
  background: linear-gradient(90deg, rgba(183, 77, 255, 0.15), rgba(183, 77, 255, 0.25));
}
.slider-thumb {
  position: absolute;
  top: -1px;
  left: 0;
  width: 42px;
  height: 42px;
  background: #b74dff;
  border: 2px solid #a33ee8;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 16px;
  cursor: grab;
  z-index: 2;
  transition: box-shadow 0.2s, background 0.2s;
  box-shadow: 0 2px 8px rgba(183, 77, 255, 0.3);
}
.slider-thumb:hover {
  background: #c87aff;
  box-shadow: 0 2px 12px rgba(183, 77, 255, 0.5);
}
.slider-thumb.dragging {
  cursor: grabbing;
  background: #a33ee8;
  box-shadow: 0 2px 16px rgba(183, 77, 255, 0.5);
}
.slider-thumb.success {
  background: #67c23a;
  border-color: #5daf34;
  cursor: default;
}
.slider-thumb.fail {
  background: #f56c6c;
  border-color: #e04040;
  cursor: pointer;
}
.slider-text {
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  font-size: 13px;
  color: var(--text-secondary);
  pointer-events: none;
}

/* ====== 动画 ====== */
.shake {
  animation: shakeAnim 0.4s ease-in-out;
}
@keyframes shakeAnim {
  0%, 100% { transform: translateX(0); }
  20% { transform: translateX(-6px); }
  40% { transform: translateX(6px); }
  60% { transform: translateX(-4px); }
  80% { transform: translateX(4px); }
}

/* ====== 暗色适配 ====== */
body.dark .slider-track {
  background: #1a1a2e;
  border-color: #2a2a4a;
}
body.dark .slider-thumb {
  box-shadow: 0 2px 8px rgba(183, 77, 255, 0.2);
}

/* El dialog button purple */
:deep(.el-dialog) {
  --el-dialog-bg-color: var(--bg-card);
}
</style>
