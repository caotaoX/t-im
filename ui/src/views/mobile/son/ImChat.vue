<template>
  <div>
    <van-nav-bar
        :title="contacts.displayName"
        left-arrow
        @click-left="onClickLeft"
    >
      <template #right>
        <van-icon name="ellipsis" size="20" @click="clickEllipsis"/>
      </template>
    </van-nav-bar>
    <div style="height: calc(100vh - 46px);" ref="MaxDivBelow">
      <div class="div-middle" :style="middleHeight" @mousemove="mousemoveEmoji">
        <van-pull-refresh style="height: 100%" v-model="isLoading" :disabled="disabled" @refresh="onRefresh">
          <div class="div-show-mess" id="div-show-mess">
            <div v-if="disabled" style="width: 100%;text-align: center">
              <p style="color: #bfbfbf;font-size: 15px">主人，我已经空了</p>
            </div>
            <template v-for="msg of messagesList">
              <div v-if="msg.fromUser.id != loginUser.id" class="div-mdg-left">
                <div style="padding-left: 5px;width: 100%;">
                  <van-row>
                    <van-col span="4" style="margin-top: 5px">
                      <van-image
                          width="2.4rem"
                          height="2.4rem"
                          radius="3"
                          :src="msg.fromUser.avatar"
                      />
                    </van-col>
                    <van-col span="16">
                      <div style="width: 100%;">
                        <p style="color: #bfbfbf;font-size: 8px;height: 15px;margin: 5px">
                          {{ msg.fromUser.displayName }} {{ timestampFormat(msg.sendTime) }}
                        </p>
                      </div>
                      <div class="bubble-left">
                        <p style="font-size: 20px;margin: 0;font-family: SimSun" v-html="textTransformation(msg.content)"></p>
                      </div>
                    </van-col>
                    <van-col span="4"></van-col>
                  </van-row>
                  </div>
              </div>
              <div v-if="msg.fromUser.id == loginUser.id" class="div-mdg-right">
                <div style="padding-right: 5px;width: 100%;float: right;">
                    <van-row>
                      <van-col span="4"></van-col>
                      <van-col span="16">
                        <div style="width: 100%;">
                          <p style="color: #bfbfbf;font-size: 8px;height: 15px;margin: 5px">
                            {{ msg.fromUser.displayName }} {{ timestampFormat(msg.sendTime) }}
                          </p>
                        </div>
                        <div class="bubble-right">
                          <p style="font-size: 20px;margin: 0;font-family: SimSun" v-html="textTransformation(msg.content)"></p>
                        </div>
                      </van-col>
                      <van-col span="4" style="margin-top: 5px;">
                        <van-image
                            width="2.4rem"
                            height="2.4rem"
                            radius="3"
                            :src="msg.fromUser.avatar"
                        />
                      </van-col>
                    </van-row>
                </div>
              </div>
              <br><br><br><br>
            </template>
          </div>
        </van-pull-refresh>
      </div>
      <div class="div-below" ref="divBelow">
        <van-field
            center
            clearable
            class="field-below"
            @mousemove="mousemoveEmoji"
        >
          <template #input>
<!--            <div contenteditable="true" @change="changeMsg" style="font-size: 15px" v-model="inputValueShow"   class="input-below" >-->
<!--            </div>-->
            <input  type="textarea" v-html="inputValueShow" v-model="inputValue" class="input-below"/>
          </template>
          <template #button>
            <van-button size="small" :disabled="inputValue == ''" @click="send" type="primary">发送</van-button>
          </template>
        </van-field>
        <div class="clearfix" @mousemove="mousemoveEmoji">
          <div class="box">
            <a href="javascript:;">
              <van-icon name="smile-o" @click="clickSmile" size="25"/>
            </a>
          </div>
          <div class="box">
            <a href="javascript:;">
              <van-icon name="photo-o" size="25"/>
            </a>
          </div>
          <div class="box">
            <a href="javascript:;">
              <van-icon name="idcard" size="25"/>
            </a>
          </div>
        </div>
        <div v-if="showEmoji" class="div-below-smile">
          <img :src="value[1]" v-for="(value, key) in emojiMap" :key="key" @click="clickEmoji(value)">
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import emojiData from "@/assets/emoji/emoji";
import { textTransformationImChat, timestampFormat} from "@/utils/ObjUtils";

export default {
  name: "ImChat",
  data() {
    return {
      loginUser: this.$store.getters.user.loginUser,
      contacts: '',
      emojiMap: new Map,
      showEmoji: false,
      isLoading: false,
      disabled: false,
      middleHeight: 'height: 200px',
      messagesList: [],
      inputValue:'',
      inputValueShow: ''
    }
  },
  created() {
    this.intiEmoji()
  },
  mounted() {
    this.setHeight()
  },
  methods: {
    setHeight() {
      try {
        setTimeout(() => {
          let i = this.$refs.MaxDivBelow.clientHeight - this.$refs.divBelow.clientHeight
          this.middleHeight = 'height:' + i + 'px'
        }, 50);
      } catch (e) {
        this.setHeight()
      }

    },
    intiEmoji() {
      emojiData[0].children.forEach(e => {
        this.emojiMap.set('[!' + e.name + ']', e.src)
      })
    },
    putMessagesList(list, next) {
      let num = this.messagesList.length
      this.messagesList = list
      this.disabled = !next
      this.isLoading = false
      if (num == 0) {
        this.crollToBottom()
      }
    },
    putUser(contacts, list) {
      this.contacts = contacts
      if (list == null || list.length == 0) {
        this.$emit('refreshUserMessages', this.contacts);
      } else {
        this.crollToBottom()
      }
    },
    appendMessage(msgList){
        this.messagesList = msgList
        this.crollToBottom()
    },
    crollToBottom() {
      this.$nextTick(() => {
        let box = this.$el.querySelector(".div-show-mess")
        box.scrollTop = box.scrollHeight
      })
    },

    clickEllipsis() {
    },
    clickSmile() {
      this.showEmoji = true
      this.setHeight()
    },
    clickEmoji(v) {
      this.inputValue = this.inputValue + v[0]
      this.inputValueShow = textTransformationImChat(this.inputValue,this.emojiMap)
    },
    mousemoveEmoji() {
      this.showEmoji = false
      this.setHeight()
    },
    onRefresh() {
      this.$emit('refreshUserMessages', this.contacts);
    },
    onClickLeft() {
      this.$emit('onClickLeft');
    },
    changeMsg(v){
      console.log(v)
    },
    send(){
      console.log(this.inputValue)
    },
    textTransformation(text) {
      return textTransformationImChat(text, this.emojiMap)
    },
    timestampFormat(timestamp) {
      return timestampFormat(timestamp)
    }
  }
}
</script>

<style scoped>
.div-middle {

}

.div-show-mess {
  height: 100%;
  /*width: 100%;*/
  overflow-x: hidden;
  overflow-y: auto;
}

.div-below {
  width: 100%;
  background-color: #eff1f6;
  position: fixed;
  bottom: 0;
}

.field-below {
  background-color: #eff1f6;
}

.input-below {
  width: 100%;
  height: 30px;
  border-radius: 5px;
  outline-style: none;
  border: 1px solid #ccc;
  border-radius: 3px;
  font-size: 16px;
}


.box {
  float: left;
  width: 10%;
  padding-left: 15px;
  padding-bottom: 5px;
}

.clearfix::after {
  content: "";
  clear: both;
  display: table;
}

a:link {
  color: black;
}

a:active {
  color: #faf6f6;
  text-decoration: none;
}


img:active {
  background-color: #b6b9d0;
}

.div-below-smile {
  height: 200px;
  overflow: auto;
}

.div-below-smile > img {
  height: 1.8em;
  padding: 5px;
}

.div-mdg-left {
  width: 100%;
  text-align: left;
  float: left;
}

.div-mdg-right {
  width: 100%;
  float: right;
  text-align: right;
}


.bubble-left {
  padding: 10px;
  text-align: left;
  background: #9EEA6A;
  position: relative;
  -moz-border-radius: 10px;
  -webkit-border-radius: 10px;
  border-radius: 10px;
}


.bubble-left:before {
  content: '';
  position: absolute;
  right: 100%;
  top: -5px;
  width: 10px;
  height: 14px;
  border-width: 0;
  border-style: solid;
  border-color: transparent;
  border-bottom-width: 10px;
  border-bottom-color: currentColor;
  border-radius: 0 0 0 32px;
  color: #9EEA6A;
}

.bubble-right {
  /*width: calc(100% - 20px);*/
  padding: 10px;
  text-align: left;
  background: #9EEA6A;
  position: relative;
  -moz-border-radius: 10px;
  -webkit-border-radius: 10px;
  border-radius: 10px;
}


.bubble-right:before {
  content: '';
  position: absolute;
  left: 100%;
  top: -5px;
  width: 10px;
  height: 14px;
  border-width: 0;
  border-style: solid;
  border-color: transparent;
  border-bottom-width: 10px;
  border-bottom-color: currentColor;
  border-radius: 0 0 32px 0;
  color: #9EEA6A;

}

</style>