<template>
  <div class="container">
    <section>
      <button class="media-btn" @click="clicks()" title="点击开始录制">{{ txt }}</button>
    </section>
    <br>
    <video ref="remote-video" id="remote-video"></video>
    <el-button @click="FullScreen" type="primary">全屏</el-button>
  </div>
</template>

<script>
export default {
  name: "RecordingScreen",
  data() {
    return {
      remoteVideo: undefined,
      txt: "开始共享屏幕",
      status: false,
      stream: null,
    };
  },
  watch: {
    status(a) {
      if (a) {
        this.txt = "停止共享屏幕";
      } else {
        this.txt = "开始共享屏幕";
      }
    },
  },
  mounted() {
    this.remoteVideo = document.querySelector('#remote-video');
    this.remoteVideo.onloadeddata = () => {
      this.remoteVideo.play();
    }
  },
  methods: {
    clicks() {
      if (this.status) {
        this.stopReset();
        this.status = false;
      } else {
        this.startScreen();
      }
    },
    stopReset() {
      this.stream.getTracks().forEach((track) => track.stop());
    },
    startScreen() {
      navigator.mediaDevices.getDisplayMedia({video: true, audio: true}).then(
          (stream) => {
            this.status = !this.status;
            console.log("开始录制");
            this.startTime = new Date().getTime();
            this.stream = stream;
            this.remoteVideo.srcObject = stream
          },
          (error) => console.log(error)
      );
    },
    FullScreen() {
      if (this.remoteVideo.requestFullscreen) {
        this.remoteVideo.requestFullscreen();
      } else if (this.remoteVideo.mozRequestFullScreen) {
        this.remoteVideo.mozRequestFullScreen();
      } else if (this.remoteVideo.webkitRequestFullScreen) {
        this.remoteVideo.webkitRequestFullScreen();
      }
    }
  },
}
</script>

<style scoped>

#remote-video {
  width: 100%;
  height: 100%;
  display: block;
  object-fit: cover;
  border: 1px solid #eee;
  background-color: #F2F6FC;
}

.container {
  width: 450px;
  height: 250px;
  border: 1px solid;
}


.item .unit {
  margin-left: 10px;
}

.item input {
  height: 30px;
  padding: 1px;
  padding-left: 5px;
}

.item input[type="text"] {
  width: 180px;
}

.item input[type="number"] {
  width: 80px;
}

.media-btn {
  background: rgb(146, 224, 214);
  border: none;
  padding: 8px;
  border-radius: 5px;
  cursor: pointer;
  font-weight: 800;
  outline: none;
}

.media-btn:hover {
  background: rgb(155, 235, 224);
}




</style>