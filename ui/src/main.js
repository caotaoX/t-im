import Vue from 'vue'
import App from './App.vue'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import '@/assets/styles/im.scss'
import router from './router'
import store from './store'
import LemonIMUI from 'lemon-imui';
import 'lemon-imui/dist/index.css';
import FriendLog from "@/components/FriendLog"
import SystemSettings from "@/components/SystemSettings";
import CustomDrawer from "@/components/CustomDrawer";

Vue.prototype.msgSuccess = function (msg) {
  this.$message({ showClose: true, message: msg, type: "success" });
}

Vue.prototype.msgError = function (msg) {
  this.$message({ showClose: true, message: msg, type: "error" });
}

Vue.prototype.msgInfo = function (msg) {
  this.$message.info(msg);
}

Vue.component('FriendLog',FriendLog)
Vue.component("SystemSettings",SystemSettings)
Vue.component("CustomDrawer",CustomDrawer)

Vue.config.productionTip = false
Vue.use(LemonIMUI);
Vue.use(ElementUI);

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
