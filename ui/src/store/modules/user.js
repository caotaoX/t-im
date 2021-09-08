import { getToken, setToken } from '@/utils/auth'
import { login} from '@/api/login'


const user = {
  state: {
    token: getToken(),
    loginUser: {}
  },

  mutations: {
    SET_TOKEN: (state, token) => {
      state.token = token
    },
    SET_USER: (state, loginUser) => {
      state.loginUser = loginUser
    },
  },

  actions: {
// 登录
    Login({ commit }, userInfo) {
      return new Promise((resolve, reject) => {
        login(userInfo).then(res => {
          setToken(res.data.token)
          commit('SET_TOKEN', res.data.token)
          resolve()
        }).catch(error => {
          reject(error)
        })
      })
    },

    SetUser({ commit }, loginUser) {
      return new Promise(() => {
          commit('SET_USER', loginUser)
      })
    },
  }
}


export default user
