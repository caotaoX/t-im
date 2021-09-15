<template>
  <div>
    <div class="container">
      <div class="video-box">
        <video ref="remote-video" id="remote-video"></video>
        <video ref="local-video" id="local-video" muted></video>
      </div>
      <div class="logger" id="logger"></div>
    </div>
    <br>
    <div style="text-align: right">
      <el-button type="primary" size="mini" @click="drawerClose">关 闭</el-button>
    </div>
  </div>
</template>

<script>
export default {
  name: "VideoCall",
  props: ['videoMsg'],
  data() {
    return {
      message: '',
      localVideo: undefined,
      remoteVideo: undefined,
      stream: undefined,
      peer: undefined,
      target: undefined

    }
  },
  mounted() {
    this.localVideo = document.querySelector('#local-video');
    this.remoteVideo = document.querySelector('#remote-video');
    this.localVideo.onloadeddata = () => {
      this.message.log('播放本地视频');
      this.localVideo.play();
    }
    this.remoteVideo.onloadeddata = () => {
      this.message.log('播放对方视频');
      this.remoteVideo.play();
    }

    this.message = {
      el: document.querySelector('.logger'),
      log(msg) {
        this.el.innerHTML += `<span>${new Date().toLocaleTimeString()}：${msg}</span><br/>`;
      },
      error(msg) {
        this.el.innerHTML += `<span class="error">${new Date().toLocaleTimeString()}：${msg}</span><br/>`;
      }
    }

    const PeerConnection = window.RTCPeerConnection || window.mozRTCPeerConnection || window.webkitRTCPeerConnection;
    !PeerConnection && this.message.error('浏览器不支持WebRTC！');
    this.peer = new PeerConnection();
    this.peer.ontrack = e => {
      if (e && e.streams) {
        this.message.log('收到对方音频/视频流数据...');
        this.remoteVideo.srcObject = e.streams[0];
      }
    };

    this.peer.onicecandidate = e => {
      if (e.candidate) {
        // this.message.log('发送通知给对方');
        let data = JSON.stringify({
          type: this.target + '_ice',
          iceCandiddate: e.candidate
        });
        this.$emit('sendMsg', data);
      } else {
        this.message.log('发送完成！');
      }
    };
    this.message.log(this.videoMsg)
  },
  created() {
  },
  methods: {
    async startLive(offerSdp) {
      try {
        this.message.log('尝试调取本地摄像头/麦克风');
        this.stream = await navigator.mediaDevices.getUserMedia({video: true, audio: true});
        this.message.log('摄像头/麦克风获取成功！');
        this.localVideo.srcObject = this.stream;
      } catch {
        this.message.error('摄像头/麦克风获取失败！');
        return;
      }
      this.message.log('将媒体轨道添加到轨道集');
      this.stream.getTracks().forEach(track => {
        this.peer.addTrack(track, this.stream);
      });
      if (!offerSdp) {
        this.message.log('创建本地SDP');
        const offer = await this.peer.createOffer();
        await this.peer.setLocalDescription(offer);
        this.message.log(`传输发起方本地SDP`);
        this.$emit('sendMsg', JSON.stringify(offer));
      } else {
        this.message.log('接收到发送方SDP');
        await this.peer.setRemoteDescription(offerSdp);
        this.message.log('创建接收方（应答）SDP');
        const answer = await this.peer.createAnswer();
        this.message.log(`传输接收方（应答）SDP`);
        this.socket.send(JSON.stringify(answer));
        await this.peer.setLocalDescription(answer);
      }
    },

    drawerClose() {
      try {
        this.stream.getTracks().forEach(function (track) {
          track.stop();
        });
      } catch (e) {
      }
      this.$emit('fatherMethod');
    },
    updateTarget(v){
      if(v == 'offer'){
        this.startLive()
      }
    },
    updatePeerValue(v){
      console.log(v)
            if (v == 'NO') {
              this.drawerClose()
            }
            if (v != null && v != '' && v != undefined) {
              console.log(v)
              const {type, sdp, iceCandidate} = JSON.parse(v)
              if (type === 'answer') {
                this.peer.setRemoteDescription(new RTCSessionDescription({type, sdp}));
              } else if (type === 'answer_ice') {
                this.peer.addIceCandidate(iceCandidate);
              } else if (type === 'offer') {
                this.startLive(new RTCSessionDescription({type, sdp}));
              } else if (type === 'offer_ice') {
                this.peer.addIceCandidate(iceCandidate);
              }
            }

    }
  },

}
</script>

<style scoped>
.container {
  width: 100%;
  display: flex;
  display: -webkit-flex;
  justify-content: space-around;
  padding-top: 20px;
}

.video-box {
  position: relative;
  width: 800px;
  height: 400px;
}

#remote-video {
  width: 100%;
  height: 100%;
  display: block;
  object-fit: cover;
  border: 1px solid #eee;
  background-color: #F2F6FC;
}

#local-video {
  position: absolute;
  right: 0;
  bottom: 0;
  width: 240px;
  height: 120px;
  object-fit: cover;
  border: 1px solid #eee;
  background-color: #EBEEF5;
}

.logger {
  width: 30%;
  padding: 14px;
  line-height: 1.5;
  color: #4fbf40;
  border-radius: 6px;
  background-color: #272727;
}

.logger .error {
  color: #DD4A68;
}
</style>