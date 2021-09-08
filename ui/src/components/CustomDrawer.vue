<template>
  <div style="height: 100%;overflow:auto;">
    <div style="padding-top: 10px;padding-left: 2px;padding-right: 2px">
      <el-row v-if="contact.isGroup == false">
        <el-col :span="24">
          <div style="text-align: center">
            <el-avatar :src="contact.avatar"></el-avatar>
          </div>
        </el-col>
        <el-col :span="24">
          <ul class="list-group list-group-striped">
            <li class="list-group-item">
              <i class="el-icon-user-solid"></i>
              用户账号
              <div class="pull-right">{{ contact.id }}</div>
            </li>
            <li class="list-group-item">
              <i class="el-icon-info"></i>
              用户名称
              <div class="pull-right">{{ contact.displayName }}</div>
            </li>
          </ul>
        </el-col>
      </el-row>

      <div v-if="contact.isGroup == true">
        <div class="slot-contact-fixedtop">
          <input class="slot-search" v-on:keyup.13="searchUserList" v-model="searchValue" placeholder="搜索群用户"/>
          <el-button icon="el-icon-circle-plus-outline" @click="showAddFriends" type="text" circle></el-button>
        </div>
        <el-row :gutter="2">
          <el-col v-for="(user,i) in list" :span="6">
            <div @click="deleteUserShow(user)">
              <el-avatar shape="square" :size="70" :src="user.avatar"></el-avatar>
              <p v-if="user.id != contact.root"
                 style="color: #9195A3;size: A3;overflow: hidden;text-overflow: ellipsis;white-space: nowrap;">
                {{ user.id }}</p>
              <p v-if="user.id == contact.root"
                 style="color: red;size: A3;overflow: hidden;text-overflow: ellipsis;white-space: nowrap;">{{
                  user.id
                }}</p>
            </div>
          </el-col>
        </el-row>
        <br/>
        <div v-if="list.length == 12" style="text-align: center">
          <el-button @click="getAll(true)" type="text">加载全部...</el-button>
        </div>
        <div v-if="list.length > 12" style="text-align: center">
          <el-button @click="getAll(false)" type="text">加载部分...</el-button>
        </div>
        <el-row>
          <el-col :span="24">
            <ul class="list-group list-group-striped">
              <li class="list-group-item">
                <i class="el-icon-user-solid"></i>
                群账号
                <div class="pull-right">{{ this.contact.id }}</div>
              </li>
              <li class="list-group-item">
                <i class="el-icon-info"></i>
                群名称
                <div class="pull-right">{{ this.contact.displayName }}</div>
              </li>
            </ul>
          </el-col>
        </el-row>
        <div v-if="contact.root == loginUser.id" style="text-align: center">
          <el-button @click="edit" type="text">编辑</el-button>
        </div>
      </div>
    </div>

    <el-dialog title="修改群" :visible.sync="addGroupOpen" :modal="false" width="500px">
      <EditGroup @grouprMethod="gbAddGroupOpen" :group="contact"/>

    </el-dialog>

    <el-dialog :visible.sync="deleteUserOpen" :modal="false" width="400px">
      <el-row>
        <el-col :span="24">
          <div style="text-align: center">
            <el-avatar :size="70" :src="this.deleteUser.avatar"></el-avatar>
          </div>
        </el-col>
        <el-col :span="24">
          <ul class="list-group list-group-striped">
            <li class="list-group-item">
              <i class="el-icon-user-solid"></i>
              用户账号
              <div class="pull-right">{{ this.deleteUser.id }}</div>
            </li>
            <li class="list-group-item">
              <i class="el-icon-info"></i>
              用户名称
              <div class="pull-right">{{ this.deleteUser.name }}</div>
            </li>
          </ul>
        </el-col>
      </el-row>
      <div v-if="loginUser.id == contact.root && deleteUser.id != loginUser.id" slot="footer" class="dialog-footer">
        <el-button type="text" @click="submitDeleteUser" size="mini">删除</el-button>
      </div>
    </el-dialog>

    <el-dialog :visible.sync="addUserOpen" :modal="false" width="400px">
      <el-row>
        <el-col :span="4">
          <div class="grid-content bg-purple"></div>
        </el-col>
        <el-col :span="16">
          <el-input placeholder="请输入好友账号或名称" v-model="searchAddUser" clearable size="small"
                    prefix-icon="el-icon-search"/>
        </el-col>
        <el-col :span="4"> &nbsp;&nbsp;&nbsp;
          <el-button @click="searchAddUserList" size="small" icon="el-icon-search" circle></el-button>
        </el-col>
      </el-row>
      <br>
      <div style="height: 100%;overflow:auto;">
        <el-table :data="addUserList" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="50" align="center"/>
          <el-table-column width="100" label="头像" align="center">
            <template slot-scope="scope">
              <el-avatar :size="30" :src="scope.row.avatar"></el-avatar>
            </template>
          </el-table-column>
          <el-table-column  label="用户账号" align="center" prop="id" :show-overflow-tooltip="true"/>
          <el-table-column  label="用户名称" align="center" prop="name" :show-overflow-tooltip="true"/>
        </el-table>
      </div>
      <div style="text-align: center">
        <el-button @click="submitAddUserUser" type="text">添加</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {getGroupUser, deleteGroupUser, getNoGroupUser,addGroupUserList} from "@/api/user"
import EditGroup from "@/components/EditGroup";

export default {
  name: "CustomDrawer",
  components: {
    EditGroup
  },
  watch: {
    "$store.getters.contacti": {
      deep: true,
      handler: function (newValue, oldValue) {
        this.searchValue = ''
        this.contact = newValue
        if (this.contact.isGroup) {
          this.all = false
          this.getList()
        }
      }
    }
  },
  data() {
    return {
      contact: {},
      searchValue: '',
      all: false,
      list: [],
      loginUser: this.$store.getters.user.loginUser,
      addGroupOpen: false,
      deleteUserOpen: false,
      deleteUser: {},
      addUserOpen: false,
      searchAddUser: '',
      addUserList: [],
      ids: '',

    }
  },
  created() {
    this.contact = this.$store.getters.contacti
    if (this.contact.isGroup) {
      this.all = false
      this.getList()
    }
  },
  methods: {
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map((item) => item.id)
    },
    searchAddUserList() {
      let data = {
        groupId: this.contact.id,
        searchValue: this.searchAddUser
      }
      getNoGroupUser(data).then(value => {
        this.addUserList = value.data
      })
    },
    searchUserList() {
      this.getList()
    },
    showAddFriends() {
      this.searchAddUserList();
      this.addUserOpen = true
    },
    getList() {
      let data = {
        id: this.contact.id,
        all: this.all,
        searchValue: this.searchValue
      }
      getGroupUser(data).then(value => [
        this.list = value.data
      ])
    },
    getAll(b) {
      if (b) {
        this.all = true
      } else {
        this.all = false
      }
      this.searchValue = ''

      this.getList()
    },
    edit() {
      this.addGroupOpen = true
    },
    gbAddGroupOpen() {
      this.addGroupOpen = false
    },
    deleteUserShow(user) {
      this.deleteUser = user
      this.deleteUserOpen = true
    },
    submitDeleteUser() {
      let data = {
        groupId: this.contact.id,
        userId: this.deleteUser.id
      }
      deleteGroupUser(data).then(value => {
        this.deleteUserOpen = false
        this.all = false
        this.getList();
      })
    },
    submitAddUserUser() {
      const ids = this.ids.toString()
      let data = {
        ids: ids,
        groupId: this.contact.id
      }
      addGroupUserList(data).then(value => {
        this.addUserOpen = false
        this.all = false
        this.getList();
      })
    }
  }
}
</script>

<style scoped>
::-webkit-scrollbar-track-piece {
  background-color: #f8f8f8;
}

::-webkit-scrollbar {
  width: 9px;
  height: 9px;
}

::-webkit-scrollbar-thumb {
  background-color: #dddddd;
  background-clip: padding-box;
  min-height: 28px;
}

::-webkit-scrollbar-thumb:hover {
  background-color: #bbb;
}
</style>