<template>
  <div class="drawer-container">
    <div>

      <h3 class="drawer-title">系统配置</h3>
      <div class="drawer-item">
        <span>开启声音提醒</span>
        <el-switch
            v-model="voice"
            active-color="#13ce66"
            inactive-color="#ff4949"
            class="drawer-switch"
            @change="voiceChange"
        >
        </el-switch>
      </div>

      <div class="drawer-item">
        <span>开启浏览器通知</span>
        <el-switch
            v-model="browserJitter"
            active-color="#13ce66"
            inactive-color="#ff4949"
            class="drawer-switch"
            @change="browserJitterChange"
        >
        </el-switch>
      </div>
      <div class="drawer-item">
        <el-button @click="outLogin" type="text">退出登录</el-button>
      </div>
    </div>
  </div>
</template>

<script>
import { removeToken} from "@/utils/auth";
import {outLogin} from "@/api/login"

export default {
  name: "SystemSettings",
  data(){
    return {
      voice: this.$store.getters.voice,
      browserJitter: this.$store.getters.browserJitter
    }
  },
  methods: {
    outLogin(){
      removeToken()
      outLogin()
      this.$router.replace({path: '/'})
    },
    voiceChange(){
      this.$store.dispatch("SetVoice", this.voice)
    },
    browserJitterChange(){
      this.$store.dispatch("SetBrowser", this.voice)
    }

  }
}
</script>

<style lang="scss" scoped>
.drawer-container {
  padding: 24px;
  font-size: 14px;
  line-height: 1.5;
  word-wrap: break-word;

  .drawer-title {
    margin-bottom: 12px;
    color: rgba(0, 0, 0, .85);
    font-size: 14px;
    line-height: 22px;
  }

  .drawer-item {
    color: rgba(0, 0, 0, .65);
    font-size: 14px;
    padding: 12px 0;
  }

  .drawer-switch {
    float: right
  }
}
</style>