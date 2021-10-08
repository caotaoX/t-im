<template>
  <div>
    <el-row :gutter="24">
      <el-col :span="6">
        <img v-bind:src="options.img" height="100% " width="100%" @click="editCropper()" title="点击上传头像"
             class="img-circle img-lg"/>
      </el-col>
      <el-col :span="18">
        <el-form ref="form" :model="group" :rules="groupRules" label-width="80px">
          <el-form-item label="群账号" prop="id">
            <el-input size="small" v-model="group.id"/>
          </el-form-item>
          <el-form-item label="群名称" prop="id">
            <el-input size="small" v-model="group.name"/>
          </el-form-item>
        </el-form>
      </el-col>
    </el-row>
    <div style="text-align: right">
      <el-button type="primary" size="mini" @click="submitUser">保存</el-button>
      <el-button  size="mini" @click="gb" >取 消</el-button>
    </div>
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

  </div>
</template>

<script>

import {VueCropper} from "vue-cropper";
import {add} from "@/api/file";
import {registerGroup} from "@/api/user"

export default {
  name: "CreateGroup",
  components: {
    VueCropper,
  },
  data(){
    return {
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
      group: {
        id: '',
        name: '',
        avatar: '',
      },
      groupRules: {
        id: [
          {required: true, trigger: "blur", message: "群账号不能为空"},
          {min: 6, message: "长度在6位以上", trigger: "blur"},
          {max: 12, message: "长度在12位以下", trigger: "blur"},
        ],
        name: [
          {required: true, trigger: "blur", message: "群名称不能为空"},
          {max: 12, message: "长度在12位以下", trigger: "blur"},
        ],
      },
    }
  },
  methods: {
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
          this.group.avatar = process.env.VUE_APP_BASE_API + '/file/getFile?id=' + value.msg;
          this.group.avatarId = value.msg
          this.imgEditOpen = false
        });
      });
    }
    ,
    // 实时预览
    realTime(data) {
      this.previews = data;
    },
    submitUser(){
      let data = {
          userName: this.group.id,
          nickName: this.group.name,
          avatar: this.group.avatarId
      }
      registerGroup(data).then(value => {
        this.$emit('fatherMethod');
      })
    },
    gb(){
      this.$emit('fatherMethod');
    }
  }
}
</script>

<style scoped>

</style>