import request from '@/utils/request'

// 获取用户信息
export function getUserInfo() {
  return request({
    url: '/user/getUserIfon',
    method: 'get'
  })
}


export function registerUser(data) {
    return request({
        url: '/user/registerUser',
        method: 'post',
        data: data
    })
}


export function registerGroup(data) {
    return request({
        url: '/user/registerGroup',
        method: 'post',
        data: data
    })
}

export function editGroup(data) {
    return request({
        url: '/user/editGroup',
        method: 'post',
        data: data
    })
}

export function updateUser(data) {
    return request({
        url: '/user/update',
        method: 'put',
        data: data
    })
}

export function getUser(data) {
    return request({
        url: '/user/getUser',
        method: 'get',
        params: data
    })
}

export function addUserFrieds(data) {
    return request({
        url: '/user/addUserFrieds',
        method: 'post',
        params: data
    })
}

export function getFriedsList(data) {
    return request({
        url: '/user/getFriedsList',
        method: 'get',
        params: data
    })
}

export function handleFried(data) {
    return request({
        url: '/user/handleFried',
        method: 'post',
        params: data
    })
}
export function deleteUser(data) {
    return request({
        url: '/user/deleteUser',
        method: 'post',
        params: data
    })
}

export function getGroupUser(data) {
    return request({
        url: '/user/getGoupUser',
        method: 'get',
        params: data
    })
}

export function deleteGroupUser(data) {
    return request({
        url: '/user/deleteGroupUser',
        method: 'DELETE',
        params: data
    })
}

export function getNoGroupUser(data) {
    return request({
        url: '/user/getNoGroupUser',
        method: 'get',
        params: data
    })
}

export function addGroupUserList(data) {
    return request({
        url: '/user/addGroupUserList',
        method: 'post',
        params: data
    })
}
