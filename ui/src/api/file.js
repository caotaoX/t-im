import request from '@/utils/request'

// 登录方法
export function add(data) {
    return request({
        url: '/file/add',
        method: 'post',
        data: data
    })
}

// 下载附件
export function getFile(data) {
    return request({
        url: '/file/getFile',
        method: 'get',
        params: data,
        responseType: 'blob'
    })
}