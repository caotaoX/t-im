package com.ct.myim.im.service;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import com.ct.myim.common.constant.MsgType;
import com.ct.myim.common.utils.ServletUtils;
import com.ct.myim.framework.redis.TokenService;
import com.ct.myim.framework.web.entity.AjaxResult;
import com.ct.myim.im.cache.ContactsUserCache;
import com.ct.myim.im.cache.MsgDeleteCalipersCache;
import com.ct.myim.im.cache.MsgLookCalipersCache;
import com.ct.myim.im.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
public class MsgService {

    @Value("${file.download.abspath.prefix}")
    private String fileUrl;

    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private UserService userService;

    @Resource
    private TokenService tokenService;

    @Resource
    private NoticeService noticeService;

    @Resource
    private ContactsUserCache contactsUserCache;
    @Resource
    private MsgDeleteCalipersCache msgDeleteCalipersCache;
    @Resource
    private MsgLookCalipersCache msgLookCalipersCache;

    /**
     * 获取未读消息条数
     *
     * @param fromID
     * @param toId
     * @return
     */
    public int getMsgSize(String fromID, String toId, boolean isGroup) {
        MsgLookCalipers calipers = msgLookCalipersCache.getMsgLookCalipersCache(toId,fromID);
        Criteria criteria = new Criteria();
        if (isGroup) {
            criteria.andOperator(Criteria.where("formUserName").is(fromID)
                    .and("deleteType").is(MsgType.DELETE_NO)
                    .and("toContactUserName").ne(toId));

        } else {
            Criteria criteria2 = Criteria.where("formUserName").is(fromID)
                    .and("toContactUserName").is(toId)
                    .and("deleteType").is(MsgType.DELETE_NO);
            criteria.andOperator(criteria2);
        }
        if (calipers != null) {
            criteria.and("sendTime").gte(calipers.getTime());
        }
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.project("formUserName")//指定输出文档中的字段
        );
        return mongoTemplate.aggregate(aggregation, SocketMsg.class, SocketMsg.class).getMappedResults().size();
    }

    /**
     * 最近一条消息的时间戳和最近一条消息的内容
     *
     * @param fromID
     * @param toId
     * @param
     * @return
     */
    public SocketMsg getMsgLastSendTimeOrLastContent(String fromID, String toId, boolean isGroup) {
        MsgDeleteCalipers calipers = msgDeleteCalipersCache.getMsgDeleteCalipersCache(toId,fromID);
        Criteria criteria = new Criteria();
        if (isGroup) {
            criteria.andOperator(Criteria.where("formUserName").is(fromID)
                    .and("deleteType").is(MsgType.DELETE_NO));
        } else {
            criteria.andOperator(Criteria.where("formUserName").is(fromID)
                    .and("toContactUserName").is(toId)
                    .and("deleteType").is(MsgType.DELETE_NO));
        }
        if (calipers != null) {
            criteria.and("sendTime").gte(calipers.getTime());
        }
        Query query = new Query();
        query.addCriteria(criteria);
        PageRequest pageRequest = new PageRequest(0, 1, new Sort(Sort.Direction.DESC, "sendTime"));
        query.with(pageRequest);
        List<SocketMsg> socketMsgs = mongoTemplate.find(query, SocketMsg.class);
        if (socketMsgs != null && socketMsgs.size() > 0) {
            SocketMsg msg = socketMsgs.get(0);
            if (msg.getMessage() != null && msg.getMessage().getFileSize() > 0) {
                msg.getMessage().setContent(fileUrl + msg.getMessage().getContent());
            }
            return socketMsgs.get(0);
        }
        return null;
    }

    public AjaxResult getMsgList(Integer pageSize, String fromID, String toId, boolean isGroup) {
        MsgDeleteCalipers calipers = msgDeleteCalipersCache.getMsgDeleteCalipersCache(fromID,toId);
        Query query = new Query();
        if (isGroup) {
            Criteria criteria = Criteria.where("formUserName").is(toId)
                    .and("deleteType").is(MsgType.DELETE_NO);
            if (calipers != null) {
                criteria.and("sendTime").gte(calipers.getTime());
            }
            query.addCriteria(criteria);
        } else {
            Criteria criteria = Criteria.where("formUserName").is(fromID)
                    .and("toContactUserName").is(toId);
            Criteria criteria2 = Criteria.where("formUserName").is(toId)
                    .and("toContactUserName").is(fromID);
            Collation collation = Collation.of(Locale.CHINESE).numericOrdering(false);
            Criteria criteria3 = new Criteria();
            criteria3.orOperator(criteria, criteria2);
            if (calipers != null) {
                Criteria criteria4 = new Criteria();
                criteria4.andOperator(criteria3, Criteria.where("sendTime").gte(calipers.getTime()).and("deleteType").is(MsgType.DELETE_NO));
                query.collation(collation).addCriteria(criteria4);
            } else {
                Criteria criteria4 = new Criteria();
                criteria4.andOperator(criteria3, Criteria.where("deleteType").is(MsgType.DELETE_NO));
                query.collation(collation).addCriteria(criteria4);
            }
        }
        PageRequest pageRequest = new PageRequest(pageSize / 10, 10, new Sort(Sort.Direction.DESC, "sendTime"));
        query.with(pageRequest);
        List<SocketMsg> socketMsgList = mongoTemplate.find(query, SocketMsg.class);
        List<SocketMessage> list = new ArrayList<>();
        for (int i = socketMsgList.size() - 1; i >= 0; i--) {
            if (socketMsgList.get(i).getMessage() != null && socketMsgList.get(i).getMessage().getFileSize() > 0) {
                socketMsgList.get(i).getMessage().setContent(fileUrl + socketMsgList.get(i).getMessage().getContent());
            }
            list.add(socketMsgList.get(i).getMessage());
        }
        return AjaxResult.success(list);
    }


    public AjaxResult getContactsList(String userName, String searchValue) {
        List<ContactsUser> list = contactsUserCache.getContactsUserCache(userName);
        List<Dict> data = new ArrayList<>();
        for (ContactsUser contactsUser : list) {
            User user = userService.getUserByuserName(contactsUser.getContactsUserName());
            if(user.getUserName().indexOf(searchValue) == -1 && user.getNickName().indexOf(searchValue) == -1){
                continue;
            }
            Dict dict = new Dict();
            dict.set("id", user.getUserName());
            dict.set("displayName", user.getNickName());
            dict.set("avatar", user.getAvatar());
            dict.set("index", user.getIndex());
            dict.set("isGroup", user.isGroup());
            if (user.isGroup()) {
                dict.set("root", user.getRoot());
            }
            if (!user.getUserName().equals("friendLog")) {
                int i = getMsgSize(contactsUser.getContactsUserName(), userName, user.isGroup());
                dict.set("unread", i);
                SocketMsg socketMsg = getMsgLastSendTimeOrLastContent(contactsUser.getContactsUserName(), userName, user.isGroup());
                if (socketMsg != null) {
                    dict.set("lastSendTime", socketMsg.getSendTime());
                    dict.set("lastContent", socketMsg.getMessage().getContent());
                }
            } else {
                dict.set("unread", 0);
            }
            data.add(dict);
        }
        return AjaxResult.success(data);
    }


    /**
     * 删除漫游记录
     *
     * @param userName 联系人或群userName
     * @return
     */
    public AjaxResult deleteRoamingRecord(String userName) {
        User LoginUser = tokenService.getLoginUser(ServletUtils.getRequest()).getUser();
        MsgDeleteCalipers calipers = msgDeleteCalipersCache.getMsgDeleteCalipersCache(LoginUser.getUserName(),userName);
        if (calipers == null) {
            MsgDeleteCalipers msgDeleteCalipers = new MsgDeleteCalipers();
            msgDeleteCalipers.setUserName(LoginUser.getUserName());
            msgDeleteCalipers.setContacts(userName);
            msgDeleteCalipers.setTime(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli());
            mongoTemplate.insert(msgDeleteCalipers);
        } else {
            Update update = new Update();
            update.set("time", LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli());
            mongoTemplate.updateFirst(new Query(Criteria.where("id").is(calipers.getId())), update, MsgDeleteCalipers.class);
        }
        msgDeleteCalipersCache.setMsgDeleteCalipersCache(LoginUser.getUserName(),userName);
        return AjaxResult.success();
    }

    /**
     * 修改消息查看记录
     *
     * @param userName 联系人或群userName
     * @return
     */
//    public void editLookMsgRecord(String userName,String contactId) {
////        User user = userService.getUserByuserName(userName);
////        MsgLookCalipers calipers = mongoTemplate.findOne(new Query(Criteria.where("userName").is(user.getUserName())
////                .and("contacts").is(contactId)), MsgLookCalipers.class);
////        if (calipers == null) {
////            MsgLookCalipers msgLookCalipers = new MsgLookCalipers();
////            msgLookCalipers.setUserName(user.getUserName());
////            msgLookCalipers.setContacts(contactId);
////            msgLookCalipers.setTime(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli());
////            mongoTemplate.insert(msgLookCalipers);
////        } else {
////            Update update = new Update();
////            update.set("time", LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli());
////            mongoTemplate.updateFirst(new Query(Criteria.where("id").is(calipers.getId())), update, MsgLookCalipers.class);
////        }
//    }

    public AjaxResult deleteMsg(String contactId, String msgId) {
        Update update = new Update();
        update.set("deleteType", MsgType.DELETE_YES);
        mongoTemplate.updateMulti(new Query(Criteria.where("msgId").is(msgId)), update, SocketMsg.class);
        User LoginUser = tokenService.getLoginUser(ServletUtils.getRequest()).getUser();
        User user = userService.getUserByuserName(contactId);
        if (user.isGroup()) {
            List<ContactsUser> userList = contactsUserCache.getContactsUserCache(LoginUser.getUserName());
            for (ContactsUser contactsUser : userList) {
                noticeService.removeMessage(contactsUser.getContactsUserName(), msgId);
            }
        } else {
            noticeService.removeMessage(contactId, msgId);
        }
        return AjaxResult.success();
    }

    public AjaxResult revokeMsg(String contactId, String msgId) {
        Update update = new Update();
        update.set("deleteType", MsgType.DELETE_YES);
        mongoTemplate.updateMulti(new Query(Criteria.where("msgId").is(msgId)), update, SocketMsg.class);
        User LoginUser = tokenService.getLoginUser(ServletUtils.getRequest()).getUser();
        User user = userService.getUserByuserName(contactId);
        if (user.isGroup()) {
            List<ContactsUser> userList = contactsUserCache.getContactsUserCache(LoginUser.getUserName());
            for (ContactsUser contactsUser : userList) {
                noticeService.removeMessage(contactsUser.getContactsUserName(), msgId);
            }
        } else {
            noticeService.removeMessage(contactId, msgId);
        }
        return AjaxResult.success();
    }

}
