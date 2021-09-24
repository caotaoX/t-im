<template>
  <div style="width: 100%;height: 100%">
    <video :src="path" controls="controls" width="100%" height="100%" ></video>
  </div>
</template>

<script>
import constant from "@/assets/constant/constant.js"
export default {
  name: "VideoLook",
  data(){
    return {
      path: constant.HTTP_IP_PROT + '/video/0'
    }

  }
}
</script>

<style scoped>

</style>