<template>

  <ul class="infinite-list" :infinite-scroll-delay="500" :infinite-scroll-distance="20" v-infinite-scroll="load"
      style="overflow:auto">
    <el-row v-for="(user,i) in userList" style="border-bottom:1px solid #ddd">
      <el-col :span="4">
        <div style="padding-top: 10px">
          <el-avatar :size="45" :src="user.url"></el-avatar>
        </div>
      </el-col>
      <el-col :span="16">
        <el-row>
          <el-col :span="24">
            <b>{{ user.name }}</b> &nbsp; {{ user.time }}
          </el-col>
          <el-col :span="24">
            <p style="padding-top: 2px">{{ user.explain }}</p>
          </el-col>
        </el-row>
      </el-col>
      <el-col :span="4">
        <el-row v-if="user.state == 10">
          <el-col :span="24">
            <el-button type="text" @click="handleFried(user.id,true)" size="small">同 意</el-button>
          </el-col>
          <el-col :span="24">
            <el-button size="small" @click="handleFried(user.id,false)" type="text">删 除</el-button>
          </el-col>
        </el-row>
        <el-row v-if="user.state == 9">
          <el-result style="height: 20px" v-if="user.type == 9 && user.state == 9 " icon="success"/>
          <el-result style="height: 20px" v-if="user.type == 10 && user.state == 9 " icon="error"/>
        </el-row>
      </el-col>
    </el-row>
  </ul>
</template>

<script>
import {getFriedsList, handleFried} from "@/api/user"

export default {
  name: "FriendLog",
  data() {
    return {
      userList: [],
      size: 0,
      type: true
    }
  },
  watch: {
    "$store.getters.timeDate": {
      deep: true,
      handler: function (newValue, oldValue) {
        this.userList = []
        this.type = true
        this.getList();
      }
    }
  },
  component: {
    getTimeDate(){
      return this.$store.getters.timeDate
    }
  },
  created() {
    this.getList()
  },
  methods: {
    load() {
      if (this.type) {
        this.getList()
      }
    },
    getList() {
      let data = {
        pageSize: this.userList.length
      }
      if (!this.type)
        return
      getFriedsList(data).then(value => {
        if (value.data.length < 10) {
          this.type = false
        } else {
          this.type = true
        }
        for (let dataKey in value.data) {
          this.userList.push(value.data[dataKey])
        }
        this.size = this.size + 10
      })
      this.type = false
    },
    handleFried(id, type) {
      let data = {
        id: id,
        type: type
      }
      handleFried(data).then(value => {
        this.userList = []
        this.type = true
        this.getList()
      })
    },

  }
}
</script>


<style scoped>
.el-col {
  padding-top: 5px;
}
</style>