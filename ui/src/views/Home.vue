<template>
  <div>
    <div class="imui-center">
      <lemon-imui
          :user="user"
          @pull-messages='handlePullMessages'
          @send="handleSend"
          @menu-avatar-click="userEdit"
          :contextmenu="contextmenuData"
          :contactContextmenu="contactContextmenuData"
          :hideMessageName="hideMessageName"
          :hideMessageTime="hideMessageTime"
          @message-click="messageClick"
          @change-menu="changeMenu"
          @change-contact="changeContact"
          ref="IMUI">
        <template #sidebar-contact-fixedtop>
          <div class="slot-contact-fixedtop">
            <input class="slot-search" v-on:keyup.13="searchUserList" v-model="searchValue" placeholder="搜索通讯录"/>
            <el-dropdown style="padding: 5px 10px;font-size: 19px" trigger="click" @command="showAddFriends">
                <span class="el-dropdown-link">
                  <i class="el-icon-circle-plus-outline el-icon--left"></i>
                </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="1"><i class="el-icon-menu el-icon--left"></i>发起群聊</el-dropdown-item>
                <el-dropdown-item command="2"><i class="el-icon-user-solid el-icon--left"></i>添加好友</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </template>
        <template #message-title="contact">
          <span>{{ contact.displayName }}</span>
          <small v-if="contact.id != 'admin'" class="more" @click="changeDrawer(contact, $refs.IMUI)">
            <i v-if="!drawerVisibleShow && contact.id != 'admin'" class="el-icon-s-unfold" />
            <i v-if="drawerVisibleShow && contact.id != 'admin'" class="el-icon-s-fold" />
            </small>
          <br/>
        </template>

      </lemon-imui>
    </div>
    <audio controls="controls" hidden src="../assets/mp3/lyq1762611.mp3" ref="audio"></audio>

    <el-dialog title="查看图片(可再次点击放大)" :visible.sync="outerVisible" width="500px">
      <el-image
          :src="imgUrl"
          :preview-src-list="srcList"
      >
      </el-image>
    </el-dialog>

    <el-dialog title="用户信息修改" :visible.sync="userOpen" width="500px">

      <el-row :gutter="24">
        <el-col :span="6">
          <img v-bind:src="options.img" height="100% " width="100%" @click="editCropper()" title="点击上传头像"
               class="img-circle img-lg"/>
        </el-col>
        <el-col :span="18">
          <el-form ref="form" :model="user" :rules="userRules" label-width="80px">
            <el-form-item label="用户名称" prop="name">
              <el-input size="small" v-model="user.name"/>
            </el-form-item>
          </el-form>
        </el-col>
      </el-row>

      <div slot="footer" class="dialog-footer">
        <el-button type="primary" size="mini" @click="submitUser">保存</el-button>
        <el-button type="primary" size="mini" @click="userOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog :title="title" :visible.sync="imgEditOpen" width="800px" append-to-body @opened="modalOpened">
      <el-row>
        <el-col :xs="24" :md="12" :style="{height: '350px'}">
          <vue-cropper
              ref="cropper"
              :img="options.img"
              :info="true"
              :autoCrop="options.autoCrop"
              :autoCropWidth="options.autoCropWidth"
              :autoCropHeight="options.autoCropHeight"
              :fixedBox="options.fixedBox"
              @realTime="realTime"
              v-if="imgVisible"
          />
        </el-col>
        <el-col :xs="24" :md="12" :style="{height: '350px'}">
          <div class="avatar-upload-preview">
            <img :src="previews.url" :style="previews.img"/>
          </div>
        </el-col>
      </el-row>
      <br/>
      <el-row>
        <el-col :lg="2" :md="2">
          <el-upload action="#" :http-request="requestUpload" :show-file-list="false" :before-upload="beforeUpload">
            <el-button size="small">
              上传
              <i class="el-icon-upload el-icon--right"></i>
            </el-button>
          </el-upload>
        </el-col>
        <el-col :lg="{span: 1, offset: 2}" :md="2">
          <el-button icon="el-icon-plus" size="small" @click="changeScale(1)"></el-button>
        </el-col>
        <el-col :lg="{span: 1, offset: 1}" :md="2">
          <el-button icon="el-icon-minus" size="small" @click="changeScale(-1)"></el-button>
        </el-col>
        <el-col :lg="{span: 1, offset: 1}" :md="2">
          <el-button icon="el-icon-refresh-left" size="small" @click="rotateLeft()"></el-button>
        </el-col>
        <el-col :lg="{span: 1, offset: 1}" :md="2">
          <el-button icon="el-icon-refresh-right" size="small" @click="rotateRight()"></el-button>
        </el-col>
        <el-col :lg="{span: 2, offset: 6}" :md="2">
          <el-button type="primary" size="small" @click="uploadImg()">提 交</el-button>
        </el-col>
      </el-row>
    </el-dialog>

    <el-dialog :visible.sync="addFriendsOpen" width="400px">
      <div>
        <el-row>
          <el-col :span="4">
            <div class="grid-content bg-purple"></div>
          </el-col>
          <el-col :span="16">
            <el-input placeholder="请输入用户账号" v-model="searchUser" clearable size="small" prefix-icon="el-icon-search"/>
          </el-col>
          <el-col :span="4"> &nbsp;&nbsp;&nbsp;
            <el-button @click="searchUserF" size="small" icon="el-icon-search" circle></el-button>
          </el-col>
        </el-row>
        <br>
        <el-row v-if="this.friendsUser != ''">
          <el-col :span="24">
            <div style="text-align: center">
              <el-avatar :src="this.friendsUser.avatar"></el-avatar>
            </div>
          </el-col>
          <el-col :span="24">
            <ul class="list-group list-group-striped">
              <li class="list-group-item">
                <i class="el-icon-user-solid"></i>
                用户账号
                <div class="pull-right">{{ this.friendsUser.userName }}</div>
              </li>
              <li class="list-group-item">
                <i class="el-icon-info"></i>
                用户名称
                <div class="pull-right">{{ this.friendsUser.nickName }}</div>
              </li>
            </ul>
          </el-col>
          <el-col :span="24">
            <div style="text-align: center">
              <el-button @click="addUserFried" type="text">添加</el-button>
            </div>
          </el-col>
        </el-row>
        <el-row v-if="this.friendsUser == ''">
          <el-col :span="24">
            <div style="height: 160px">
              <el-empty style="height: 95%" description="空"></el-empty>
            </div>
          </el-col>
        </el-row>
      </div>
    </el-dialog>

    <el-dialog title="提示" :visible.sync="explainVisible" width="400px">
      <el-form label-width="80px">
        <el-form-item label="添加说明">
          <el-input v-model="explain"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button size="small" @click="explainVisible = false">取 消</el-button>
        <el-button size="small" type="primary" :disabled="explain == ''" @click="addExplain">确 定</el-button>
      </span>
    </el-dialog>

    <el-dialog title="创建群" :visible.sync="addGroupOpen" width="500px">

      <CreateGroup @fatherMethod="gbAddGroupOpen" />

    </el-dialog>

  </div>
</template>

<script>
import Vue from 'vue';
import {getToken, removeToken} from "@/utils/auth";
import {getUserInfo, updateUser, getUser, addUserFrieds, deleteUser} from '@/api/user'
import {add} from '@/api/file'
import {list, getContactsList, deleteRoamingRecord, revokeMsg} from '@/api/msg'
import constant from "@/assets/constant/constant.js"
import emojiData from "@/assets/emoji/emoji";
import {VueCropper} from "vue-cropper";
import FriendLog from "@/components/FriendLog";
import {outLogin} from "@/api/login"
import Push from 'push.js'
import CreateGroup from "@/components/CreateGroup";
import CustomDrawer from "@/components/CustomDrawer";

export default {
  name: "home",
  components: {
    VueCropper,
    CreateGroup
  },
  data() {
    return {
      user: {
        id: '',
        name: '',
        avatar: '',
      },
      socket: undefined,
      wsUrl: 'ws://' + constant.SOCKTE_IP_PROT + '/ws?Authorization=' + getToken(),
      ContactsList: '',
      timer: '',
      outerVisible: false,
      imgUrl: undefined,
      srcList: undefined,
      contextmenuData: [
        {
          click: (e, instance, hide) => {
            const {message} = instance;
            this.imgUrl = message.content
            this.srcList = [message.content]
            this.outerVisible = true
            hide()
          },
          visible: instance => {
            return instance.message.type == "image";
          },
          text: '查看图片'
        },
        {
          click: (e, instance, hide) => {
            const {message} = instance;
            this.download(message.content, message.fileName)
            hide()
          },
          visible: instance => {
            return instance.message.type == "file";
          },
          text: "下载文件",
        },
        {
          click: (e, instance, hide) => {
            const {message} = instance;
            this.download(message.content, message.fileName)
            hide()
          },
          visible: instance => {
            return instance.message.type == "image";
          },
          text: "下载图片",
        },
        {
          click: (e, instance, hide) => {
            let data = {
              contactId: instance.message.toContactId,
              msgId: instance.message.id
            }
            revokeMsg(data).then(value => {
              this.IMUI.removeMessage(instance.message.id)
              hide()
            })
          },
          visible: instance => {
            return instance.message.sendTime > (new Date().getTime() - 1000 * 60 * 2) && instance.message.fromUser.id == this.user.id;
          },
          color: "red",
          text: "撤销",
        }
      ],
      contactContextmenuData: [
        {
          click: (e, instance, hide) => {
            let data = {
              userName: instance.contact.id
            }
            deleteUser(data).then(value => {
              this.IMUI.removeContact(instance.contact.id)
              hide()
            })
          },
          visible: instance => {
            return !instance.contact.isGroup && instance.contact.id != 'admin' && instance.contact.id != 'friendLog';
          },
          color: "red",
          text: "删除联系人",
        },
        {
          click: (e, instance, hide) => {
            let data = {
              userName: instance.contact.id
            }
            deleteUser(data).then(value => {
              this.IMUI.removeContact(instance.contact.id)
              hide()
            })
          },
          visible: instance => {
            return instance.contact.isGroup && instance.contact.id != 'admin' && instance.contact.id != 'friendLog';
          },
          color: "red",
          text: "删除群",
        },
        {
          click: (e, instance, hide) => {
            let data = {
              userName: instance.contact.id
            }
            deleteRoamingRecord(data).then(value => {
              this.IMUI.clearMessages(instance.contact.id)
              hide()
            })
          },
          visible: instance => {
            return instance.contact.id != 'admin' && instance.contact.id != 'friendLog';
          },
          text: "删除漫游消息",
        }
      ],
      hideMessageName: false,
      hideMessageTime: false,
      // 图片操作
      // 是否显示弹出层
      userOpen: false,
      imgEditOpen: false,
      // 是否显示cropper
      imgVisible: false,
      // 弹出层标题
      title: "修改头像",
      options: {
        img: '', //裁剪图片的地址
        autoCrop: true, // 是否默认生成截图框
        autoCropWidth: 200, // 默认生成截图框宽度
        autoCropHeight: 200, // 默认生成截图框高度
        fixedBox: true // 固定截图框大小 不允许改变
      },
      previews: {},
      userRules: {
        name: [
          {required: true, trigger: "blur", message: "名称不能为空"}
        ],
      },
      searchValue: '',
      // 添加好友操作
      addFriendsOpen: false,
      searchUser: '',
      friendsUser: '',
      explain: '',
      explainVisible: false,
      // 新增组
      addGroupOpen: false,
      drawerVisibleShow: true
    }
  },
  created() {
    this.isLogin()
    this.notification()
  },
  mounted() {
    this.IMUI = this.$refs.IMUI;
    this.getUserInfo()
    this.init()
    this.IMUI.initEmoji(emojiData);
    this.IMUI.initMenus([
          {
            name: "messages",
          },
          {
            name: "contacts",
          },
          {
            name: "setUp",
            title: "设置",
            unread: 0,
            renderContainer: () => {
                return (
                  <SystemSettings />
                )
            },
            render: menu => {
              return <i class="el-icon-s-tools"/>;
            },
            isBottom: true,
          }
        ]
    )
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
    }
    ,
    isLogin() {
      if (getToken == null || getToken() == undefined) {
        this.$router.replace({path: '/'})
      }
    }
    ,
    open: function () {
      console.log('socket连接成功')
    }
    ,
    error: function () {
      setTimeout(() => {
        this.init();
      }, 5000);
    }
    ,
    message: function (msg) {
      const {IMUI} = this.$refs;
      let data = JSON.parse(msg.data);
      if (data.httpType == constant.PRIVATE_CHAT) {
        let fromId = data.message.fromUser.id
        data.message.toContactId = fromId
        IMUI.appendMessage(data.message, true);
        this.notice()
      }
      if (data.httpType == constant.GROUP_CHAT) {
        IMUI.appendMessage(data.message, true);
        this.notice()
      }
      if (data.httpType == constant.UPDATE_USER) {
        let user = JSON.parse(data.content);
        IMUI.updateContact(user)
      }
      if (data.httpType == constant.NOTICE_ADD_USER) {
        let user = JSON.parse(data.content);
        IMUI.appendContact(user)
      }
      if (data.httpType == constant.NOTICE_DELETE_USER) {
        IMUI.removeContact(data.content)
      }
      if (data.httpType == constant.NOTICE_OUT_LOGIN) {
        Vue.prototype.msgError("在其他地方登录！")
        outLogin()
        removeToken()
        this.Socket.close();
        this.$router.replace({path: '/'})
      }
      if (data.httpType == constant.OTICE_DELETE_MSG || data.httpType == constant.NOTICE_REVOKE_MSG) {
        IMUI.removeMessage(data.content)
      }

    },
    notice(){
      if(this.$store.getters.voice){
        this.audio();
      }
      if(this.$store.getters.browserJitter){
        this.showPush()
      }
    }
    ,
    close: function () {
      console.log("socket已经关闭")
      setTimeout(() => {
        this.init();
      }, 5000);
    }
    ,

    getContactsList() {
      let data = {
        formUserName: this.user.id,
        searchValue: this.searchValue
      }
      getContactsList(data).then(value => {
        for (let valueKey in value.data) {
          if (value.data[valueKey].id == 'friendLog') {
            value.data[valueKey].renderContainer = () => {
              return <FriendLog></FriendLog>;
            }
          }
        }
        this.IMUI.initContacts(value.data);
      })
    }
    ,
    socketSend(data) {
      this.Socket.send(JSON.stringify(data))
    }
    ,
    getUserInfo() {
      getUserInfo().then(value => {
        this.user.id = value.data.userName
        this.user.name = value.data.nickName
        this.user.avatar = value.data.avatar
        this.$store.dispatch("SetUser", this.user)
        this.getContactsList()
      })
    }
    ,
    handlePullMessages(contact, next) {
      contact.unread = 0
      let data = {
        pageSize: this.IMUI.getCurrentMessages().length,
        fromID: this.user.id,
        toId: contact.id,
        isGroup: contact.isGroup | false
      }
      list(data).then(value => {
        contact.unread = 0
        if (value.data.length < 10) {
          next(value.data, true)
        } else {
          next(value.data, false)
        }
      });
    }
    ,
    handleSend(message, next, file) {
      try {
        let httpType = constant.PRIVATE_CHAT
        if (this.IMUI.getCurrentContact().isGroup) {
          httpType = constant.GROUP_CHAT
        }
        if (message.type == 'file' || message.type == 'image') {
          let msg = this.deepClone(message);
          msg.fromUser.displayName = msg.fromUser.name
          let formData = new FormData()
          formData.append('file', file)
          add(formData).then(value => {
            msg.content = value.msg;
            msg.fileSize = file.size
            msg.fileName = file.name
            let data = {
              httpType: httpType,
              formUserName: this.user.id,
              message: msg
            }
            this.socketSend(data)
          });
        } else {
          message.fromUser.displayName = message.fromUser.name
          let data = {
            httpType: httpType,
            formUserName: this.user.id,
            message: message
          }
          this.socketSend(data)
        }
        next();
      } catch (err) {
        console.log(err)
      }
    }
    ,
    generateRandId() {
      return Math.random().toString(36).substr(-8);
    }
    ,
    audio() {
      this.$refs.audio.currentTime = 0; //从头开始播放提示音
      this.$refs.audio.play();
    }
    ,
    deepClone(target) {
      // 定义一个变量
      let result;
      // 如果当前需要深拷贝的是一个对象的话
      if (typeof target === 'object') {
        // 如果是一个数组的话
        if (Array.isArray(target)) {
          result = []; // 将result赋值为一个数组，并且执行遍历
          for (let i in target) {
            // 递归克隆数组中的每一项
            result.push(this.deepClone(target[i]))
          }
          // 判断如果当前的值是null的话；直接赋值为null
        } else if (target === null) {
          result = null;
          // 判断如果当前的值是一个RegExp对象的话，直接赋值
        } else if (target.constructor === RegExp) {
          result = target;
        } else {
          // 否则是普通对象，直接for in循环，递归赋值对象的所有值
          result = {};
          for (let i in target) {
            result[i] = this.deepClone(target[i]);
          }
        }
        // 如果不是对象的话，就是基本数据类型，那么直接赋值
      } else {
        result = target;
      }
      // 返回最终结果
      return result;
    }
    ,
    download(href, name) {
      let eleLink = document.createElement('a')
      eleLink.download = name
      eleLink.href = href
      eleLink.click()
      eleLink.remove()
    }
    ,
    userEdit() {
      this.options.img = this.user.avatar
      this.userOpen = true;
    }
    ,
    // 编辑头像
    editCropper() {
      this.imgEditOpen = true;
    }
    ,
    // 打开弹出层结束时的回调
    modalOpened() {
      this.imgVisible = true;
    }
    ,
    // 覆盖默认的上传行为
    requestUpload() {
    }
    ,
    // 向左旋转
    rotateLeft() {
      this.$refs.cropper.rotateLeft();
    }
    ,
    // 向右旋转
    rotateRight() {
      this.$refs.cropper.rotateRight();
    }
    ,
    // 图片缩放
    changeScale(num) {
      num = num || 1;
      this.$refs.cropper.changeScale(num);
    }
    ,
    // 上传预处理
    beforeUpload(file) {
      if (file.type.indexOf("image/") == -1) {
        this.msgError("文件格式错误，请上传图片类型,如：JPG，PNG后缀的文件。");
      } else {
        const reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = () => {
          this.options.img = reader.result;
        };
      }
    }
    ,
    // 上传图片
    uploadImg() {
      this.$refs.cropper.getCropBlob(data => {
        let formData = new FormData();
        formData.append("file", data);
        add(formData).then(value => {
          this.user.avatar = process.env.VUE_APP_BASE_API + '/file/getFile?id=' + value.msg;
          this.user.avatarId = value.msg
          this.imgEditOpen = false
        });
      });
    }
    ,
    // 实时预览
    realTime(data) {
      this.previews = data;
    }
    ,
    submitUser() {
      let data = {
        userName: this.user.id,
        avatar: this.user.avatarId,
        nickName: this.user.name
      }
      updateUser(data).then(value => {
        Vue.prototype.msgSuccess('修改成功')
        this.userOpen = false
      })
    }
    ,
    messageClick(e, key, message, instance) {
      if (message.type == 'image') {
        this.imgUrl = message.content
        this.srcList = [message.content]
        this.outerVisible = true
      }
    }
    ,
    searchUserList() {
      this.getContactsList()
    }
    ,
    changeMenu(v) {
      if (this.searchValue.length > 0) {
        this.searchValue = ''
        this.getContactsList()
      }
    }
    ,
    showAddFriends(v) {
      if (v == '1') {
        this.addGroupOpen = true
      }
      if (v == '2') {
        this.addFriendsOpen = true
      }
    },
    gbAddGroupOpen(){
      this.addGroupOpen = false
      this.getContactsList()
    }
    ,
    searchUserF() {
      this.friendsUser = ''
      if (this.searchUser != '') {
        let data = {
          userName: this.searchUser
        }
        getUser(data).then(value => {
          let user = value.data
          if (user != null) {
            this.friendsUser = user
          } else {
            Vue.prototype.msgSuccess(value.msg)
          }
        })
      }
    }
    ,
    addUserFried() {
      this.explainVisible = true
    }
    ,
    addExplain() {
      let data = {
        userName: this.friendsUser.userName,
        explain: this.explain
      }
      addUserFrieds(data).then(value => {
        Vue.prototype.msgSuccess(value.msg)
        this.explainVisible = false;
        this.addFriendsOpen = false;
      })
    }
    ,
    changeContact(contact) {
      contact.unread = 0
      if (contact.id == 'friendLog') {
        this.$store.dispatch("SetTime", new Date().getTime())
      }
      this.$store.dispatch("SetContact", contact)
      this.IMUI.drawerVisible = false
      this.drawerVisibleShow = true
    },
    //申请浏览器通知权限，具体参见上面文档链接
    notification(){
      Push.Permission.request();
    },
    showPush () {
      Push.create("新消息提醒", {
        body: '您有新的消息，请查看！',
        requireInteraction: true, // 是否显示关闭、设置按钮
        icon: '../assets/images/xxtz.png', // 右侧图标，也可以是png url
        timeout: 3000, // 显示时间
      });
    },
    changeDrawer(contact, instance) {
      this.drawerVisibleShow = this.IMUI.drawerVisible
      this.$store.dispatch("SetContact", contact)
      instance.changeDrawer({
        width: 300,
        //height: "90%",
        //offsetX:0 ,
        //offsetY: ,
        //position: "center",
        // inside: true,
        // offsetX: -280,
        // offsetY: -100,
        render: () => {
          return (
              <CustomDrawer/>
          );
        },
      });
    },
  }
}
</script>

<style lang="stylus">
::selection {
  background: #000;
  color: #fff;
}

body
  font-family "Microsoft YaHei"
  background #f6f6f6 !important

#app
  width 90%
  //margin 0 auto
  //padding-bottom 20px
  //padding-top 60px

  .scroll-top
    cursor pointer
    position fixed
    bottom 40px
    left 50%
    border-radius 50%
    background #fff
    font-size 18px
    overflow hidden
    width 40px
    height 40px
    line-height 40px
    user-select none
    text-align center
    transform rotate(-45deg) translateX(-50%)
    box-shadow 0 0 30px rgba(0, 0, 0, 0.1);

    &:hover
      font-size 22px

a
  color #0c5ed9
  text-decoration none
  font-size 12px

.action
  margin-top 20px

  .lemon-button
    margin-right 10px
    margin-bottom 10px

.link
  font-size 14px
  margin-top 15px

  a
    display inline-block
    margin 0 5px
    text-decoration none
    background #ffba00
    border-radius 4px
    padding 5px 10px
    color rgba(0, 0, 0, 0.8)

.logo
  position relative
  display inline-block
  margin 60px auto
  user-select none

.logo-text
  font-size 38px

.logo-sub
  font-size 18px
  color #999
  font-weight 300

.logo-badge
  position absolute
  top -10px
  left 230px
  background #000
  border-radius 16px
  color #f9f9f9
  font-size 12px
  padding 4px 8px

.title
  font-size 24px
  line-height 26px
  border-left 1px solid #ffba00
  padding-left 15px
  margin-bottom 15px
  margin-top 30px
  user-select none

.table
  width 100%
  border-radius 10px
  background #fff
  border-collapse collapse

  tr
    cursor pointer

  tr:not(.table-head):hover
    background #ffba00 !important

  tr:nth-of-type(even)
    background #f9f9f9

  th
    user-select none
    color #999

  td,
  th
    text-align left
    padding 10px 15px
    font-size 14px
    font-weight normal

.imui-center
  margin-bottom 60px
  padding-top 30px
  margin-left 10%

  .lemon-wrapper
    border: 1px solid #ddd;

  .lemon-drawer
    border: 1px solid #ddd;
    border-left: 0;

.drawer-content
  padding 15px

.more
  font-size 12px
  line-height 24px
  height 24px
  position absolute
  top 14px
  right 14px
  cursor pointer
  user-select none
  color #f1f1f1
  display inline-block
  border-radius 4px
  background #111
  padding 0 8px

  &:active
    background #999

.bar
  text-align center
  line-height 30px
  background #fff
  margin 15px
  color #666
  user-select none
  font-size 12px

.cover
  text-align center
  user-select none
  position absolute
  top 50%
  left 50%
  transform translate(-50%, -50%)

  i
    font-size 84px
    color #e6e6e6

  p
    font-size 18px
    color #ddd
    line-height 50px

.article-item
  line-height 34px
  cursor pointer

  &:hover
    text-decoration underline
    color #318efd

pre
  background #fff
  border-radius 8px
  padding 15px

.lemon-simple .lemon-container {
  z-index: 5
}

.lemon-simple .lemon-drawer {
  z-index: 4
}


input#switch[type=checkbox] {
  height: 0;
  width: 0;
  display: none;
}

label#switch-label {
  cursor: pointer;
  text-indent: -9999px;
  width: 34px;
  height: 20px;
  background: #aaa;
  display: block;
  border-radius: 100px;
  position: relative;
}

label#switch-label:after {
  content: '';
  position: absolute;
  top: 2px;
  left: 2px;
  width: 16px;
  height: 16px;
  background: #fff;
  border-radius: 20px;
  transition: 0.3s;
}

input#switch:checked + label {
  background: #0fd547;
}

input#switch:checked + label:after {
  left: calc(100% - 2px);
  transform: translateX(-100%);
}

label#switch-label:active:after {
  width: 20px;
}

.slot-group
  width 170px
  border-left 1px solid #ddd;
  height 100%
  box-sizing border-box
  padding 10px

  .slot-search
    margin 5px 0

.slot-group-notice
  color #999
  padding 6px 0
  font-size 12px

.slot-group-title
  font-size 12px

.slot-group-member
  font-size 12px
  line-height 18px

.slot-group-menu span
  display inline-block
  cursor pointer
  color #888
  margin 4px 10px 0 0
  border-bottom 2px solid transparent;

  &:hover
    color #000
    border-color #333

.slot-contact-fixedtop
  padding 10px
  border-bottom 1px solid #ddd

.slot-search
  width 80%
  box-sizing border-box
  font-size 14px
  border 1px solid #bbb
  padding 5px 10px

.el-dropdown-link {
  cursor: pointer;
  color: #409EFF;
}

.el-icon-arrow-down {
  font-size: 12px;
}

.demonstration {
  display: block;
  color: #8492a6;
  font-size: 14px;
  margin-bottom: 20px;
}

.bg-purple {
  background: #ffffff;
}

.grid-content {
  border-radius: 4px;
  min-height: 36px;
}
</style>