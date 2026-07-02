<template>
  <el-dialog title="提示" v-model="visible" width="300px">
    <span>确定要删除该记录吗？</span>
    <template #footer>
      <el-button @click="cancel">取消</el-button>
      <el-button type="primary" @click="confirm">确定</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
  props: { delVisible: Boolean },
  emits: ["confirm", "cancelRow"],
  computed: {
    visible: {
      get() { return this.delVisible; },
      set(val) { this.$emit("cancelRow", val); }
    }
  },
  methods: {
    confirm() { this.$emit("confirm"); },
    cancel() { this.$emit("cancelRow", false); }
  }
});
</script>
