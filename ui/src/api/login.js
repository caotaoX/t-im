import request from '@/utils/request'

// 登录方法
export function login(data) {
  return request({
    url: '/login',
    method: 'post',
    data: data
  })
}

export function outLogin() {
  return request({
    url: '/outLogin',
    method: 'post'
  })
}
