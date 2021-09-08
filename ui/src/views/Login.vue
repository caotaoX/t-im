<template>
  <div class="login">
    <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
      <h3 class="title">我的IM</h3>
      <el-form-item prop="userName">
        <el-input v-model="loginForm.userName" type="text" auto-complete="off" placeholder="账号">
        </el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input
            v-model="loginForm.password"
            type="password"
            auto-complete="off"
            placeholder="密码"
            @keyup.enter.native="handleLogin"
        >
        </el-input>
      </el-form-item>
      <el-form-item prop="code">
        <el-button @click="showRegister" type="text">注册账号</el-button>
      </el-form-item>
      <el-form-item style="width:100%;">
        <el-button
            :loading="loading"
            size="medium"
            type="primary"
            style="width:100%;"
            @click.native.prevent="handleLogin"
        >
          <span v-if="!loading">登 录</span>
          <span v-else>登 录 中...</span>
        </el-button>
      </el-form-item>
    </el-form>
    <!--  底部  -->
    <div class="el-login-footer">
      <span>Copyright © 2018-2021 CT All Rights Reserved.</span>
    </div>

    <el-dialog :visible.sync="open" title="注册账号" width="500px" :close-on-press-escape="false"
               :show-close="false" append-to-body :close-on-click-modal="false">
      <el-form ref="form" :model="registerForm" :rules="registerRules" label-width="80px">
        <el-form-item label="账号" prop="userName">
          <el-input v-model="registerForm.userName" placeholder="请输入账号"/>
        </el-form-item>
        <el-form-item label="名称" prop="nickName">
          <el-input v-model="registerForm.nickName" placeholder="请输入名称"/>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="registerForm.password" placeholder="请输入密码" type="password"/>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="mini" @click="submitRegister">提交</el-button>
          <el-button type="primary" size="mini" @click="close">关闭</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>

  </div>
</template>

<script>

import {
  registerUser
} from '@/api/user'
import Vue from "vue";

export default {
  name: "Login",
  data() {
    return {
      loginForm: {
        userName: undefined,
        password: undefined,
      },
      loginRules: {
        userName: [
          {required: true, trigger: "blur", message: "用户名不能为空"}
        ],
        password: [
          {required: true, trigger: "blur", message: "密码不能为空"}
        ],
      },
      loading: false,
      redirect: undefined,
      registerForm: {
        userName: undefined,
        nickName: undefined,
        password: undefined
      },
      registerRules: {
        userName: [
          {required: true, trigger: "blur", message: "账号不能为空"},
          {min: 6, message: "长度在6位以上", trigger: "blur"},
          {max: 12, message: "长度在12位以下", trigger: "blur"},
        ],
        nickName: [
          {required: true, trigger: "blur", message: "名称不能为空"},
        ],
        password: [
          {required: true, trigger: "blur", message: "密码不能为空"}
        ],

      },
      open: false
    };
  },
  created() {
  },
  methods: {
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true;
          this.store
          this.$store.dispatch("Login", this.loginForm).then(() => {
            this.$router.push('/home');
          }).catch(() => {
            this.loading = false;
          });
        }
      });
    },
    showRegister() {
      this.open = true
    },
    close() {
      this.open = false
    },
    submitRegister() {
      this.$refs.form.validate(valid => {
        if (valid) {
          registerUser(this.registerForm).then(value => {
            this.open = false
            Vue.prototype.msgSuccess(value.msg)
          })

        }
      })
    }
  }
};
</script>

<style rel="stylesheet/scss" lang="scss">
.login {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  background-image: url("../assets/images/e3e205e20a62e2c3e3a428ccae41de52.jpeg");
  background-size: cover;
}

.title {
  margin: 0px auto 30px auto;
  text-align: center;
  color: #707070;
}

.login-form {
  border-radius: 6px;
  background: #ffffff;
  width: 400px;
  padding: 25px 25px 5px 25px;

  .el-input {
    height: 38px;

    input {
      height: 38px;
    }
  }

  .input-icon {
    height: 39px;
    width: 14px;
    margin-left: 2px;
  }
}

.login-tip {
  font-size: 13px;
  text-align: center;
  color: #bfbfbf;
}

.login-code {
  width: 33%;
  height: 38px;
  float: right;

  img {
    cursor: pointer;
    vertical-align: middle;
  }
}

.el-login-footer {
  height: 40px;
  line-height: 40px;
  position: fixed;
  bottom: 0;
  width: 100%;
  text-align: center;
  color: #fff;
  font-family: Arial;
  font-size: 12px;
  letter-spacing: 1px;
}

.login-code-img {
  height: 38px;
}
</style>
