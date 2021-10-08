<template>
  <div>
    <keep-alive>
      <template v-if="active === 0">
        <Messages @showChat="showChat" @editArrayByUser="editArrayByUser" ref="messages"/>
      </template>
      <template v-if="active === 1">
        <Contacts  @refreshContacts="refreshContacts" ref="contacts"/>
      </template>
      <template v-if="active === 2">
        <Setting/>
      </template>
      <template v-if="active === 3">
        <ImChat @socketSend="socketSend" @refreshUserMessages="refreshUserMessages" @onClickLeft="onChatClickLeft" ref="imchat" />
      </template>
    </keep-alive>

    <template v-if="active != 3">
      <div style="height: 4rem;"></div>
      <van-tabbar v-model="active" @change="onChange">
        <van-tabbar-item>
          <template #icon>
            <Awesome-icon name="comments"></Awesome-icon>
          </template>
        </van-tabbar-item>
        <van-tabbar-item>
          <template #icon>
            <Awesome-icon name="id-card"></Awesome-icon>
          </template>
        </van-tabbar-item>
        <van-tabbar-item>
          <template #icon>
            <Awesome-icon name="user-cog"></Awesome-icon>
          </template>
        </van-tabbar-item>
      </van-tabbar>
    </template>
    <audio controls="controls" hidden src="../../assets/mp3/lyq1762611.mp3" ref="audio"></audio>
  </div>
</template>

<script>

import Contacts from "@/views/mobile/son/Contacts";
import Messages from "@/views/mobile/son/Messages";
import Setting from "@/views/mobile/son/Setting";
import constant from "@/assets/constant/constant";
import {getToken, removeToken} from "@/utils/auth";
import {getUserInfo} from "@/api/user";
import {getContactsList, list} from "@/api/msg";
import {
  addArrayMess,
  addMessagesMap,
  deleteMessagesMap,
  editArrayByUser,
  getContact,
  textTransformation
} from "@/utils/ObjUtils";
import ImChat from "@/views/mobile/son/ImChat";
import Vue from "_vue@2.6.14@vue";
import emojiData from "@/assets/emoji/emoji";
import {outLogin} from "@/api/login";

export default {
  name: "Home",
  components: {
    Contacts,
    Messages,
    Setting,
    ImChat
  },
  data() {
    return {
      active: undefined,
      currentView: Contacts,
      wsUrl: 'ws://' + constant.SOCKTE_IP_PROT + '/ws?Authorization=' + getToken(),
      user: {
        id: '',
        name: '',
        avatar: '',
      },
      contactsList: [],
      messagesMap: new Map(),
      currentContact: null,
      emojiMap: new Map()
    };
  },
  mounted() {
    this.active = 0
    this.$forceUpdate()
    this.getUserInfo()
    this.init()
  },
created() {
  this.intiEmoji()
  this.isLogin()
},
  methods: {
    init: function () {
      if (!window.WebSocket) {
        Vue.prototype.msgError("您的浏览器不支持socket")
      } else {
        this.isLogin()
        // 实例化socket
        this.Socket = new WebSocket(this.wsUrl)
        // 监听socket连接
        this.Socket.onopen = this.open
        // 监听socket错误信息
        this.Socket.onerror = this.error
        // 监听socket消息
        this.Socket.onmessage = this.message
        // 监听socket关闭消息
        this.Socket.onclose = this.close
      }
    },
    intiEmoji() {
      emojiData[0].children.forEach(e => {
        this.emojiMap.set('[!' + e.name + ']', e.src)
      })
    },
    isLogin() {
      if (getToken == null || getToken() == undefined) {
        this.$router.replace({path: '/'})
      }
    } ,
    open: function () {
      console.log('socket连接成功')
    }
    ,
    error: function () {
      setTimeout(() => {
        this.init();
      }, 5000);
    },
    message: function (msg) {
      let data = JSON.parse(msg.data);
      let message = data.message
      if (data.httpType == constant.PRIVATE_CHAT) {
        this.editContact(message, false)
        let fromId = message.fromUser.id
        message.toContactId = fromId
        addMessagesMap(this.messagesMap,message)
        if(this.active == 3 && this.currentContact.id == fromId){
          this.$refs.imchat.appendMessage(this.messagesMap.get(fromId))
          this.editLookMsgRecord(this.currentContact.id)
        }
        this.notice()
      }
      if (data.httpType == constant.GROUP_CHAT) {
        this.editContact(message, true)
        addMessagesMap(this.messagesMap,message)
        if (this.active == 3 && this.currentContact.id == message.toContactId) {
          this.$refs.imchat.appendMessage(this.messagesMap.get(message.toContactId))
          this.editLookMsgRecord(this.currentContact.id)
        }
        this.notice()

      }
      // if (data.httpType == constant.UPDATE_USER) {
      //   let user = JSON.parse(data.content);
      //   IMUI.updateContact(user)
      // }
      // if (data.httpType == constant.NOTICE_ADD_USER) {
      //   let user = JSON.parse(data.content);
      //   IMUI.appendContact(user)
      // }
      // if (data.httpType == constant.NOTICE_DELETE_USER) {
      //   IMUI.removeContact(data.content)
      // }
      if (data.httpType == constant.NOTICE_OUT_LOGIN) {
        Vue.prototype.msgError("在其他地方登录！")
        outLogin()
        removeToken()
        this.Socket.close();
        this.$router.replace({path: '/'})
      }
      if (data.httpType == constant.OTICE_DELETE_MSG || data.httpType == constant.NOTICE_REVOKE_MSG) {
        deleteMessagesMap(this.messagesMap,data.content)
        if(this.active == 3) {
          this.$refs.imchat.appendMessage(this.messagesMap.get(this.currentContact.id))
        }
      }
    },
    notice() {
      if (this.$store.getters.voice) {
        this.audio();
      }
    },
    audio() {
      this.$refs.audio.currentTime = 0; //从头开始播放提示音
      this.$refs.audio.play();
    },
    onChange() {
      if (this.active == 0) {
        this.pubMessContactsList()
      }
      if (this.active == 1) {
        this.putContactsList()
      }
    },
    putContactsList() {
      try {
        setTimeout(() => {
          this.$refs.contacts.putContactsList(this.contactsList)
        }, 50);
      } catch (e) {
        this.putContactsList()
      }
    },
    pubMessContactsList() {
      try {
        setTimeout(() => {
          this.$refs.messages.putContactsList(this.contactsList)
        }, 50);
      } catch (e) {
        this.putContactsList()
      }
    },
    getUserInfo() {
      getUserInfo().then(value => {
        this.user.id = value.data.userName
        this.user.name = value.data.nickName
        this.user.avatar = value.data.avatar
        this.$store.dispatch("SetUser", this.user)
        this.getContactsList()
      })
    },
    getContactsList() {
      let data = {
        formUserName: this.user.id,
        searchValue: ''
      }
      getContactsList(data).then(value => {
        this.contactsList = value.data
        this.onChange()
      })
    },
    refreshContacts() {
      let data = {
        formUserName: this.user.id,
        searchValue: ''
      }
      getContactsList(data).then(value => {
        this.contactsList = value.data
        this.putContactsList()
      })
    },
    editArrayByUser(user) {
      editArrayByUser(this.contactsList, user)
    },
    showChat(user) {
      this.active = 3
      this.currentContact = user
      this.pubImChatUser(user)
      this.editLookMsgRecord(user.id)
    },
    refreshUserMessages(user){
      user.unread = 0
      editArrayByUser(this.contactsList, user)
      let messagesList = this.messagesMap.get(user.id)
      let pageSize = 0;
      if(messagesList != null){
        pageSize = messagesList.length
      } else {
        messagesList = []
      }
      let data = {
        pageSize: pageSize,
        fromID: this.user.id,
        toId: user.id,
        isGroup: user.isGroup | false
      }
      list(data).then(value => {
        let next = true
        if (value.data.length < 10) {
          next= false
        }
        messagesList = addArrayMess(value.data,messagesList)
        this.messagesMap.set(user.id,messagesList)
        this.$refs.imchat.putMessagesList(messagesList,next)
      });
    },
    pubImChatUser(user) {
      try {
        setTimeout(() => {
          this.$refs.imchat.putUser(user,this.messagesMap.get(user.id))
        }, 50);
      } catch (e) {
        this.pubImChatUser(user)
      }
    },
    onChatClickLeft(){
      this.active = 0
      this.onChange()
    },
    editContact(msg,b){
      let id = b == false ? msg.fromUser.id : msg.toContactId
      let user = getContact(this.contactsList,id)
      user.unread = user.unread + 1
      user.sendTime = msg.fromUser.sendTime
      user.lastContent = textTransformation(msg.content,this.emojiMap)
      editArrayByUser(this.contactsList,user)
    },
    editLookMsgRecord(contactId) {
      let data = {
        httpType: constant.EDIT_LOOK_MSG_RECORD,
        formUserName: this.user.id,
        toContactUserName: contactId
      }
      this.socketSend(data)
    },
    socketSend(data) {
      this.Socket.send(JSON.stringify(data))
    }
  }
}
</script>

<style scoped>

</style>