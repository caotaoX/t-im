<template>
  <div>
    <van-nav-bar
    >
      <template #title>
        消息
      </template>
      <template #right>
        <van-icon name="search" size="20" @click="clickSearch"/>
      </template>
    </van-nav-bar>
    <van-search
        v-if="navType"
        v-model="searchValue"
        show-action
        placeholder="请输入搜索关键词"
        shape="round"
        @input="onInput"
        @cancel="onCancel"
        autofocus
    />

    <template v-for="user in contactsList">
      <van-swipe-cell>
        <van-cell :key="user.id" @click="showChat(user)">
          <van-row type="flex">
            <van-col span="4">
                <div class="user-name-c">
                  <van-badge :content="user.unread == 0 ? null : user.unread" max="99">
                  <van-image
                      width="2.3rem"
                      height="2.3rem"
                      radius="3"
                      :src="user.avatar"
                      class="van-image-zdy"
                  />
                  </van-badge>

                </div>
            </van-col>
            <van-col span="20">
              <el-row>
                <div class="user-name-c-name">
                  <p class="p-name">
                    {{ user.displayName }}
                  </p>
                  <p class="p-time">
                    {{
                      timestampFormat(user.lastSendTime)
                    }}
                  </p>
                </div>
              </el-row>
              <el-row>
                <div class="user-name-c-mess">
                  <p class="p-mess" v-if="user.type == 'file'">
                    [文件]
                  </p>
                  <p class="p-mess" v-if="user.type == 'image'">
                    [图片]
                  </p>
                  <p class="p-mess" v-html="textTransformation(user.lastContent)" v-if="user.type == 'text'">
                  </p>
                </div>
              </el-row>
            </van-col>
          </van-row>
        </van-cell>
        <template #right>
          <van-button style="height: 100%;" square type="danger" @click="dangerButton(user)" text="删除"/>
        </template>
      </van-swipe-cell>
    </template>
  </div>
</template>

<script>

import emojiData from "@/assets/emoji/emoji";
import {deleteRoamingRecord} from "@/api/msg";
import {editArrayByUser, textTransformation, timestampFormat} from "@/utils/ObjUtils";
import {Dialog} from 'vant';

export default {
  name: "Messages",
  data() {
    return {
      contactsList: [],
      emojiMap: new Map,
      navType: false,
      contactsListBack: [],
      searchValue: ''
    }
  },
  mounted() {
    this.intiEmoji()
  },
  methods: {
    intiEmoji() {
      emojiData[0].children.forEach(e => {
        this.emojiMap.set('[!' + e.name + ']', e.src)
      })
    },

    putContactsList(contactsList) {
      this.contactsList = []
      contactsList.forEach(v => {
        if (v.unread != 0 || v.lastContent != null) {
          this.contactsList.push(v)
        }
      })
      this.contactsList.sort(function (a, b) {
        return a.lastSendTime < b.lastSendTime ? 1 : -1
      });
      this.contactsListBack = this.contactsList
    },
    dangerButton(user) {
      Dialog.confirm({
        title: '提示',
        message: '操作会清除漫游数据，是否确认删除！',
      })
          .then(() => {
            let data = {
              userName: user.id
            }
            deleteRoamingRecord(data).then(value => {
              user.unread = 0
              user.lastContent = null
              editArrayByUser(this.contactsList, user)
              this.$emit('editArrayByUser', user);
              this.putContactsList(this.contactsList)
            })
          })

      console.log(user)
    },
    clickSearch() {
      this.navType = true
    },
    onCancel() {
      this.putContactsList(this.contactsListBack)
      this.navType = false
    },
    onInput() {
      if (this.searchValue != '' && this.searchValue != undefined) {
        this.contactsList = []
        this.contactsListBack.forEach(value => {
          if ((this.searchValue.unread != 0 || this.searchValue.lastContent != null) && value.displayName.indexOf(this.searchValue) != -1) {
            this.contactsList.push(value)
          }
        })
      } else {
        this.putContactsList(this.contactsListBack)
      }
    },
    showChat(user) {
      this.$emit('showChat', user);
    },
    textTransformation(text) {
      return textTransformation(text, this.emojiMap)
    },
    timestampFormat(timestamp) {
      return timestampFormat(timestamp)
    }
  }
}
</script>

<style scoped>
van-cell {
  /*padding: 0px 5px;*/
  /*height: 2.7rem;*/
}

van-col {
  /*height: 2.7rem;*/
}

.p-name {
  width: 70%;
  margin: 0;
  padding: 0;
  font-size: 1.1em;
  text-overflow: ellipsis;
  display: inline-block;
  vertical-align: middle;
  float: left;
  display: block;
  overflow: hidden;
  white-space: nowrap;

}


.p-time {
  width: 30%;
  margin: 0;
  padding: 0;
  font-size: 0.8em;
  display: inline-block;
  vertical-align: middle;
  float: right;
  text-align: right;
  display: block;
  color: #bfbfbf;
}

.p-mess {
  margin: 0;
  padding: 0;
  font-size: 1em;
  height: 20px;
  display: inline-block;
  vertical-align: middle;
  text-align: left;
  display: block;
  color: #bfbfbf;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.van-image-zdy {
  vertical-align: middle;
}

.user-name-c {
  width: 100%;
  height: 100%;
  overflow: hidden;
  position: relative;
  vertical-align: middle;
  display: flex;
  align-items: center;

}

.user-name-c-name {
  width: 100%;
  overflow: hidden;
  position: relative;
  vertical-align: middle;
  height: 1.7rem;
}

.user-name-c-mess {
  overflow: hidden;
  position: relative;
  display: table-cell;
  vertical-align: middle;
  width: 100%;
  height: 1.5rem;
}

</style>