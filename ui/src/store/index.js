import Vue from 'vue'
import Vuex from 'vuex'
import user from './modules/user'
import friend from "./modules/friend";
import setting from "@/store/modules/setting";
import getters from './getters'
import drawer from "@/store/modules/drawer";
Vue.use(Vuex)

const store = new Vuex.Store({
  modules: {
    user,
    friend,
    setting,
    drawer
  },
  getters
})

export default store
