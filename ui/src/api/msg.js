import request from '@/utils/request'

export function list(query) {
    return request({
        url: '/msg/list',
        method: 'get',
        params: query
    })
}

export function getContactsList(query) {
    return request({
        url: '/msg/getContactsList',
        method: 'get',
        params: query
    })
}

export function deleteRoamingRecord(data){
    return request({
        url: '/msg/deleteRoamingRecord',
        method: 'post',
        params: data
    })
}
export function revokeMsg(data){
    return request({
        url: '/msg/revokeMsg',
        method: 'post',
        params: data
    })
}

