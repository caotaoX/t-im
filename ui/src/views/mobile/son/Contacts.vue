<template>
  <div>
    <van-nav-bar
        left-arrow
    >
      <template #title>
        联系人
      </template>
      <template #left>
        <van-icon name="plus" size="20"/>
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

    <van-pull-refresh v-model="isLoading" @refresh="onRefresh">
      <van-index-bar :index-list="indexList">
        <template v-for="user in contactsList">
          <van-cell v-if="user.id == 'friendLog'" :key="user.id">
            <van-row type="flex">
              <van-col span="4">
                <div class="user-name-c">
                  <van-image
                      width="2.5rem"
                      height="2.5rem"
                      radius="3"
                      :src="user.avatar"
                  />
                </div>
              </van-col>
              <van-col span="20">
                <div class="user-name-c">
                  <p>
                    {{ user.displayName }}
                  </p>
                </div>
              </van-col>
            </van-row>
          </van-cell>
        </template>
        <van-index-anchor index="群组"/>
        <template v-for="user in contactsList">
          <van-cell v-if="user.isGroup == true && user.id != 'friendLog' && user.id != 'admin'" :key="user.id">
            <van-row type="flex">
              <van-col span="4">
                <div class="user-name-c">
                  <van-image
                      width="2.5rem"
                      height="2.5rem"
                      radius="3"
                      :src="user.avatar"
                  />
                </div>
              </van-col>
              <van-col span="20">
                <div class="user-name-c">
                  <p>
                    {{ user.displayName }}
                  </p>
                </div>
              </van-col>
            </van-row>
          </van-cell>
        </template>
        <van-index-anchor index="好友"/>
        <template v-for="index of indexList">
          <van-index-anchor :index="index" :key="index"/>
          <template v-for="user in contactsList">
            <van-cell
                v-if="user.isGroup == false && user.id != 'friendLog' && user.id != 'admin' && user.index == index"
                :key="user.id">
              <van-row type="flex">
                <van-col span="4">
                  <div class="user-name-c">
                    <van-image
                        width="2.5rem"
                        height="2.5rem"
                        radius="3"
                        :src="user.avatar"
                    />
                  </div>
                </van-col>
                <van-col span="20">
                  <div class="user-name-c">
                    <p>
                      {{ user.displayName }}
                    </p>
                  </div>
                </van-col>
              </van-row>
            </van-cell>
          </template>
        </template>
      </van-index-bar>
    </van-pull-refresh>
  </div>
</template>

<script>

import {Toast} from 'vant';

export default {
  name: "Contacts",
  data() {
    return {
      indexList: [],
      contactsList: [],
      contactsListBack: [],
      isLoading: false,
      navType: false,
      searchValue: ''
    }
  },
  methods: {
    putContactsList(contactsList) {
      this.contactsList = contactsList
      this.contactsListBack = contactsList
      this.refreshIndexList()
      this.isLoading = false;
    },
    onRefresh() {
      this.$emit('refreshContacts');
      Toast('刷新成功');
        this.isLoading = false;
    },
    clickSearch() {
      this.navType = true
    },
    onCancel() {
      this.contactsList = this.contactsListBack
      this.refreshIndexList()
      this.navType = false
    },
    onInput() {
      if (this.searchValue != '' && this.searchValue != undefined) {
        this.contactsList = []
        this.contactsListBack.forEach(value => {
          if (value.displayName.indexOf(this.searchValue) != -1) {
            this.contactsList.push(value)
          }
        })
        this.refreshIndexList()
      } else {
          this.putContactsList(this.contactsListBack)
      }
    },
    refreshIndexList() {
      let map = new Map();
      this.contactsList.forEach(v => {
        if (v.id != 'admin' && v.isGroup == false) {
          map.set(v.index, v.index)
        }
      })
      this.indexList = []
      map.forEach(v => {
        this.indexList.push(v)
      })
    }
  }
}
</script>

<style scoped>
van-index-bar > van-cell {
  padding: 0px 10px;
  margin: 2px;
  height: 2.7rem;
}

van-col {
  height: 2.5rem;
}

p {
  margin: 0;
  padding: 0;
  font-size: 1.2em;
  text-overflow: ellipsis;
  display: inline-block;
  vertical-align: middle;
  text-align: left;
  display: block
}

.van-image {
  margin: 0;
  padding: 0;
  display: inline-block;
  vertical-align: middle;
  text-align: left;
  display: block
}

.user-name-c {
  overflow: hidden;
  position: relative;
  display: table-cell;
  vertical-align: middle;
  width: 100%;
  height: 2.5rem;
}
</style>