/**
 * 格式化13位时间戳
 * @param timestamp
 * @returns {string}
 */
export function timestampFormat(timestamp) {
    function zeroize(num) {
        return (String(num).length == 1 ? '0' : '') + num;
    }

    var curTimestamp = parseInt(new Date().getTime()); //当前时间戳
    var timestampDiff = curTimestamp - timestamp; // 参数时间戳与当前时间戳相差秒数

    var curDate = new Date(curTimestamp); // 当前时间日期对象
    var tmDate = new Date(timestamp);  // 参数时间戳转换成的日期对象

    var Y = tmDate.getFullYear(), m = tmDate.getMonth() + 1, d = tmDate.getDate();
    var H = tmDate.getHours(), i = tmDate.getMinutes(), s = tmDate.getSeconds();

    if (timestampDiff < 60) { // 一分钟以内
        return "刚刚";
    } else if (timestampDiff < 3600) { // 一小时前之内
        return Math.floor(timestampDiff / 60) + "分钟前";
    } else if (curDate.getFullYear() == Y && curDate.getMonth() + 1 == m && curDate.getDate() == d) {
        return '' + zeroize(H) + ':' + zeroize(i);
    } else {
        var newDate = new Date((curTimestamp - 86400) * 1000); // 参数中的时间戳加一天转换成的日期对象
        if (newDate.getFullYear() == Y && newDate.getMonth() + 1 == m && newDate.getDate() == d) {
            return '昨天' + zeroize(H) + ':' + zeroize(i);
        } else if (curDate.getFullYear() == Y) {
            return zeroize(m) + '月' + zeroize(d) + '日 ' + zeroize(H) + ':' + zeroize(i);
        } else {
            return Y + '年' + zeroize(m) + '月' + zeroize(d) + '日 ' + zeroize(H) + ':' + zeroize(i);
        }
    }
}

/**
 * 将表情字符串替换为图片标签
 * @param text
 * @param emojiMap
 * @returns {*}
 */
export function textTransformation(text,emojiMap) {
    // if()
    function replaceString(repStr, rgExp, replaceText) {
        let str = repStr.replace(rgExp, replaceText)
        if (str.indexOf(rgExp) != -1) {
            str = replaceString(str, rgExp, replaceText);
        }
        return str;
    }
    emojiMap.forEach(function (value, key, map) {
        if (text.indexOf(key) != -1) {
            let img = '<img src="' + map.get(key) + '" style="height: 0.8em;" />'
            text = replaceString(text, key, img)
        }
    })
    return text
}

/**
 * 将表情字符串替换为图片标签
 * @param text
 * @param emojiMap
 * @returns {*}
 */
export function textTransformationImChat(text,emojiMap) {
    function replaceString(repStr, rgExp, replaceText) {
        let str = repStr.replace(rgExp, replaceText)
        if (str.indexOf(rgExp) != -1) {
            str = replaceString(str, rgExp, replaceText);
        }
        return str;
    }
    emojiMap.forEach(function (value, key, map) {
        if (text.indexOf(key) != -1) {
            let img = '<img src="' + map.get(key) + '" style="height: 20px;" />'
            text = replaceString(text, key, img)
        }
    })
    return text
}


/**
 *修改用户数组
 */
export function editArrayByUser(contactsList,user){
    contactsList.forEach(u => {
        if(u.id == user.id){
            u = user
        }
    })
}

/**
 * 为数组中新增数组
 * @param formA
 * @param toA
 */
export function addArrayMess(formA,toA){
    toA.forEach(v => {
        formA.push(v)
    })
    return formA
}

/**
 * 为为集合中数组新增消息
 * @param formA
 * @param toA
 */
export function addMessagesMap(msgMap,msg){
    let msgList = msgMap.get(msg.toContactId)
    if(msgList == null){
        msgList = []
    }
    msgList.push(msg)
    msgMap.set(msg.toContactId,msgList)
}

/**
 * 为为集合中数组删除消息
 * @param formA
 * @param toA
 */
export function deleteMessagesMap(msgMap,id){
    msgMap.forEach(list => {
        for (let i = 0; i < list.length; i++) {
            if(list[i].id == id){
                list.splice(i, 1);
            }
        }
    })
}

export function getContact(contactList,id){
    let user = null
    contactList.forEach(v => {
        if(v.id == id){
            user =  v
        }
    })
    return user
}